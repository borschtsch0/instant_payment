package misis.payment.repository

import misis.payment.model._

import java.lang.Math.round
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class AccCbRepositoryInMemory(implicit val ec: ExecutionContext) extends AccCbRepository {
  // операции с кэшбеком

  private val types = mutable.Map[String, Cashback]()

  override def offers(): Future[Seq[Cashback]] = Future {
    types.values.toList
  }

  override def createCashback(opt: CreateCashback): Future[Cashback] = Future {
    val cb = Cashback(cat = opt.cat, percent = opt.perc)
    types.put(cb.cat, cb)
    cb
  }

  override def updateCashback(opt: UpdateCashback): Future[Option[Cashback]] = Future {
    types.get(opt.cat).map { cb =>
      val updated = cb.copy(percent = opt.perc)
      types.put(opt.cat, updated)
      updated
    }
  }

  override def getPercent(opt: String): Future[Int] = Future {
    types(opt).percent
  }

  // операции со счетами

  private val bank = mutable.Map[UUID, Account]()

  override def list(): Future[Seq[Account]] = Future {
    bank.values.toList
  }

  override def createAcc(acc: CreateAcc): Future[Account] = Future {
    val account = Account(id = UUID.randomUUID(), owner = acc.owner, number = acc.number, volume = acc.volume)
    bank.put(account.id, account)
    account
  }


  override def get(acc: UUID): Future[Account] = Future {
    bank(acc)
  }

  override def getAcc(acc: GetAcc): Future[Seq[UUID]] = {
    // Получение нужного номера
    val l = list().map(accounts => accounts.filter(account => account.number == acc.number))
    // Получение айдишника
    l.map(accounts => accounts.map(_.id))
  }


  override def getAccOwn(acc: GetAcc): Future[Seq[(UUID, Int)]] = {
    // Получение нужного номера
    val lst = list().map(accounts => accounts.filter(account => account.number == acc.number))
    // Получение айдишника и размера счета
    lst.map(accounts => {accounts.map(obj => (obj.id, obj.volume))})
  }

  override def topupAcc(acc: TopupAcc): Future[Option[Account]] = Future {
    bank.get(acc.id).map { account =>
      val new_vol = account.volume + acc.add
      val updated = account.copy(volume = new_vol)
      bank.put(account.id, updated)
      updated
    }
  }

  override def takeoutMoney(acc: TakeoutMoney): Future[Option[Account]] = Future {
    bank.get(acc.id).map { account =>
      val new_vol = account.volume - acc.subtr
      val updated = account.copy(volume = new_vol)
      bank.put(account.id, updated)
      updated
    }
  }

  override def moneyOrder(operation: MoneyOrder): Future[Int] = Future {
    val cashb = operation.cat.getOrElse("0")
    // 1 шаг - снятие денег с первого счета, если они вообще есть
    bank.get(operation.from_id).map { account =>
      val new_vol = account.volume - operation.summa
      if (cashb != "0") {
        val perc = types(cashb).percent
        val newest_vol = new_vol + round(operation.summa / 100 * perc)
        val updated = account.copy(volume = newest_vol)
        bank.put(account.id, updated)
      }
      else {
        val updated = account.copy(volume = new_vol)
        bank.put(account.id, updated)
      }
    }
    // 2 шаг - пополнение второго счета
    bank.get(operation.to_id).map { account =>
      val new_vol = account.volume + operation.summa
      val updated = account.copy(volume = new_vol)
      bank.put(account.id, updated)
    }
    operation.summa
  }
}
