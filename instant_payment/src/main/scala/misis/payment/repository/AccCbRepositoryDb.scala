package misis.payment.repository

import akka.http.scaladsl.model.StatusCodes
import misis.payment.model.{Account, Cashback, CreateAcc, CreateCashback, GetAcc, MoneyOrder, TakeoutMoney, TopupAcc, UpdateCashback}
import slick.jdbc.PostgresProfile.api._
import misis.payment.db.AccCbDb._

import java.lang.Math.round
import java.util.UUID
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
        if (cb.nonEmpty)
          cb.head
        else
          throw new CashbackNonExist
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
      if (cb.nonEmpty)
        cb.head
      else
        throw new CashbackNonExist
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
    val acnt = db.run(query.result.head)
    acnt
//    if (acnt.value.nonEmpty)
//      acnt
//    else
//      throw new AccountNonExist
  }

  def find(acc: UUID): Future[Option[Account]]  = {
    db.run(accountTable.filter(_.id === acc).result.headOption)
  }

  override def getAcc(acc: GetAcc): Future[Seq[UUID]] = {
    val query = accountTable.filter(_.number === acc.number)
    val acnt = db.run(query.map(ac => ac.id).result)

    acnt.map { ac =>
      if (ac.nonEmpty)
        ac
      else
        throw new AccountNonExist
    }
  }

  override def getAccOwn(acc: GetAcc): Future[Seq[(UUID, Int)]] = {
    val query = accountTable.filter(_.number === acc.number)
    val acnt = db.run(query.map(ac => (ac.id, ac.volume)).result)

    acnt.map{ ac =>
      if (ac.nonEmpty)
        ac
      else
        throw new AccountNonExist
    }
  }

  override def topupAcc(acc: TopupAcc): Future[Account] = {
    val account = db.run(accountTable.filter(_.id === acc.id).map(ac => ac.id).result).map { ac =>
      if (ac.nonEmpty) {
        get(acc.id).map { acnt =>
          val new_vol = acnt.volume + acc.add
          db.run {
            accountTable.filter(_.id === acc.id)
              .map(_.volume).update(new_vol)
          }
          acnt
        }
      }
      else
        throw new AccountNonExist
    }
    account.flatten
  }

  override def takeoutMoney(acc: TakeoutMoney): Future[Account] = {
    val account = db.run(accountTable.filter(_.id === acc.id).map(ac => ac.id).result).map { ac =>
      if (ac.nonEmpty) {
        get(acc.id).map { acnt =>
          val new_vol = acnt.volume - acc.subtr
          if (new_vol < 0)
            throw new LessThanZero
          else
            db.run {
              accountTable.filter(_.id === acc.id)
                .map(_.volume).update(new_vol)
            }
            acnt
        }
      }
      else
        throw new AccountNonExist
    }
    account.flatten
  }

  override def moneyOrder(acc: MoneyOrder): Future[Int] = {
    // 0 шаг - проверка ошибок
    db.run(accountTable.filter(_.id === acc.from_id).map(_.id).result).map { ac =>
      if (ac.nonEmpty) ac
      else throw new AccountNonExist
    }
    db.run(accountTable.filter(_.id === acc.to_id).map(_.id).result).map { ac =>
      if (ac.nonEmpty) ac
      else throw new AccountNonExist
    }
    get(acc.from_id).map { account =>
      if ((account.volume - acc.summa) >= 0)
        account.volume - acc.summa
      else
        throw new LessThanZero
    }

    val cashb = acc.cat.getOrElse("0")
    // 1 шаг - снятие денег с первого счета, если они вообще есть
    get(acc.from_id).map { account =>
      val new_vol = account.volume - acc.summa
      if (cashb != "0") {
        getPercent(cashb).map { perc =>
          val newest_vol = new_vol + round(acc.summa / 100 * perc)
          for {
            _ <- db.run {
              accountTable
                .filter(_.id === acc.from_id)
                .map(_.volume).update(newest_vol)
            }
            res <- find(acc.from_id)
          } yield res
        }
      }
      else {
        for {
          _ <- db.run {
            accountTable
              .filter(_.id === acc.from_id)
              .map(_.volume).update(new_vol)
          }
          res <- find(acc.from_id)
        } yield res
      }


        // 2 шаг - пополнение второго счета
      get(acc.to_id).flatMap { accnt =>
        val new_volume = accnt.volume + acc.summa
        db.run {
          accountTable
            .filter(_.id === acc.to_id)
            .map(_.volume).update(new_volume)
        }
      }
      acc.summa
    }
  }
}

case class LessThanZero() extends Exception // Выбрана слишком большая сумма для перевода
case class AccountNonExist() extends Exception // Банковского счета не существует
case class CashbackNonExist() extends Exception