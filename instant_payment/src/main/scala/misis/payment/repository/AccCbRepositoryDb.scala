package misis.payment.repository

import akka.http.scaladsl.model.StatusCodes
import misis.payment.model.{Account, Cashback, CreateAcc, CreateCashback, GetAcc, MoneyOrder, TakeoutMoney, TopupAcc, UpdateCashback}
import slick.jdbc.PostgresProfile.api._
import misis.payment.db.AccCbDb._

import java.lang.Math.round
import java.util.UUID
import scala.collection.immutable.LinearSeq
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AccCbRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends AccCbRepository {
  override def offers(): Future[Seq[Cashback]] = {
    db.run(cashbackTable.result)
  }

  override def createCashback(opt: CreateCashback): Future[Cashback] = {
    val cb = Cashback(cat = opt.cat, percent = opt.perc)
    for {
      _ <-db.run(cashbackTable += cb)
      res <- db.run(cashbackTable.filter(_.cat === cb.cat).result.head)
    } yield res
  }

  override def updateCashback(opt: UpdateCashback): Future[Option[Cashback]] = {
    for {
      _ <- db.run(cashbackTable
        .filter(_.cat === opt.cat)
        .map(cb => cb.percent).result).map { cb =>
        cb.nonEmpty match {
          case true => cb.head
          case false => throw new CashbackNonExist
        }
      }

      _ <- db.run {
        cashbackTable
          .filter(_.cat === opt.cat)
          .map(_.percent)
          .update(opt.perc)
      }

      res <- findCashback(opt.cat)
    } yield res
  }

  override def getPercent(opt: String): Future[Int] = {
    db.run(cashbackTable.filter(_.cat === opt).map(cb => cb.percent).result).map { cb =>
      cb.nonEmpty match {
        case true => cb.head
        case false => throw new CashbackNonExist
      }
    }
  }

   def findCashback(opt: String): Future[Option[Cashback]] = {
     db.run(cashbackTable.filter(_.cat === opt).result.headOption)
  }


  override def list(): Future[Seq[Account]] = {
    db.run(accountTable.result)
  }

  override def createAcc(acc: CreateAcc): Future[Account] = {
    val accnt = Account(owner = acc.owner, number = acc.number, volume = acc.volume)
    for {
      _ <- db.run(accountTable += accnt)
      res <- get(accnt.id)
    } yield res
  }

  override def get(acc: UUID): Future[Account] = {
    val query = accountTable.filter(_.id === acc)
    db.run(query.result.head)
  }

  def find(acc: UUID): Future[Option[Account]]  = {
    db.run(accountTable.filter(_.id === acc).result.headOption)
  }

  override def getAcc(acc: GetAcc): Future[Seq[UUID]] = {
    val query = accountTable.filter(_.number === acc.number)
    db.run(query.map(ac => ac.id).result).map { ac =>
      ac.nonEmpty match {
        case true => ac
        case false => throw new AccountNonExist
      }
    }
  }

  override def getAccOwn(acc: GetAcc): Future[Seq[(UUID, Int)]] = {
    val query = accountTable.filter(_.number === acc.number)
    db.run(query.map(ac => (ac.id, ac.volume)).result).map { ac =>
      ac.nonEmpty match {
        case true => ac
        case false => throw new AccountNonExist
      }
    }
  }

  override def topupAcc(acc: TopupAcc): Future[Account] = {
    db.run(accountTable.filter(_.id === acc.id).map(ac => ac.id).result).flatMap { ac =>
      ac.nonEmpty match {
        case true => get(acc.id).map { acnt =>
            val new_vol = acnt.volume + acc.add
            db.run {
              accountTable.filter(_.id === acc.id)
                .map(_.volume).update(new_vol)
            }
            acnt
          }
        case false => throw new AccountNonExist
      }
    }
  }

  override def takeoutMoney(acc: TakeoutMoney): Future[Account] = {
    db.run(accountTable.filter(_.id === acc.id).map(ac => ac.id).result).flatMap { ac =>
      ac.nonEmpty match {
        case true => get(acc.id).map { acnt =>
          val new_vol = acnt.volume - acc.subtr
          new_vol match {
            case _ if new_vol > 0 => db.run {
              accountTable.filter(_.id === acc.id)
                .map(_.volume).update(new_vol)
            }
            case _ if new_vol < 0 => throw new LessThanZero
          }
          acnt
        }
        case false => throw new AccountNonExist
      }
    }
  }

  override def moneyOrder(acc: MoneyOrder): Future[Int] = {
    // 0 шаг - проверка ошибок
    // Проверка существования счета отправителя
    db.run(accountTable.filter(_.id === acc.from_id).map(_.id).result).flatMap { ac =>
      ac.nonEmpty match {
        // Проверка существования счета получателя
        case true => db.run(accountTable.filter(_.id === acc.to_id).map(_.id).result).flatMap { ac =>
          ac.nonEmpty match {
            // Проверка на овердрафт
            case true => get(acc.from_id).flatMap { account =>
              (account.volume - acc.summa) >= 0 match {
                // 1 шаг - снятие денег с первого счета
                case true => get(acc.from_id).map { account =>
                  val new_vol = account.volume - acc.summa
                  acc.cat.nonEmpty match {
                    case true => getPercent(acc.cat.get).map { perc =>
                      val newest_vol = new_vol + round(acc.summa / 100 * perc)
                      for {
                        _ <- db.run(accountTable.filter(_.id === acc.from_id).map(_.volume).update(newest_vol))
                        res <- find(acc.from_id)
                      } yield res
                    }
                    case false => for {
                      _ <- db.run(accountTable.filter(_.id === acc.from_id).map(_.volume).update(new_vol))
                      res <- find(acc.from_id)
                    } yield res
                  }
                  // 2 шаг - пополнение второго счета
                  get(acc.to_id).flatMap { accnt =>
                    val new_volume = accnt.volume + acc.summa
                    db.run(accountTable.filter(_.id === acc.to_id).map(_.volume).update(new_volume))
                  }

                  acc.summa
                }
                case false => throw new LessThanZero
              }
            }
            case false => throw new AccountNonExist
          }
        }
        case false => throw new AccountNonExist
      }
    }


  }
}

case class LessThanZero() extends Exception // Выбрана слишком большая сумма для перевода
case class AccountNonExist() extends Exception // Банковского счета не существует
case class CashbackNonExist() extends Exception // Кэшбека не существует