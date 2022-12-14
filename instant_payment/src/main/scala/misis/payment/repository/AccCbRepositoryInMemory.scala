package misis.payment.repository

import misis.payment.model._

import java.lang.Math.round
import java.util.UUID
import scala.collection.mutable

class AccCbRepositoryInMemory extends AccCbRepository {
  // операции с кэшбеком

  private val types = mutable.Map[String, Cashback]()

  override def offers():List[Cashback] = types.values.toList

  override def createCashback(opt: CreateCashback): Cashback = {
    val cb = Cashback(cat = opt.cat, percent = opt.perc)
    types.put(cb.cat, cb)
    cb
  }

  override def updateCashback(opt: UpdateCashback): Option[Cashback] = {
    types.get(opt.cat).map { cb =>
      val updated = cb.copy(percent = opt.perc)
      types.put(opt.cat, updated)
      updated
    }
  }

  override def getPercent(opt: String): Int = {
    types(opt).percent
  }

  // операции со счетами

  private val bank = mutable.Map[UUID, Account]()

  override def list(): List[Account] = bank.values.toList

  override def createAcc(acc: CreateAcc): Account = {
    val account = Account(id = UUID.randomUUID(), owner = acc.owner, number = acc.number, volume = acc.volume)
    bank.put(account.id, account)
    account
  }


  override def get(acc: UUID): Account = bank(acc)

  override def getAcc(acc: GetAcc): List[UUID] = {
    val l = list()
    l.filter(account => account.number == acc.number).map(_.id)
  }


  override def getAccOwn(acc: GetAcc): List[(UUID, Int)] = {
    val l = list()
    l.filter(account => account.number == acc.number).map(obj => (obj.id, obj.volume))
  }

  override def topupAcc(acc: TopupAcc): Option[Account] = {
    bank.get(acc.id).map { account =>
      val new_vol = account.volume + acc.add
      val updated = account.copy(volume = new_vol)
      bank.put(account.id, updated)
      updated
    }
  }

  override def takeoutMoney(acc: TakeoutMoney): Option[Account] = {
    bank.get(acc.id).map { account =>
      val new_vol = account.volume - acc.subtr
      if (new_vol < 0) throw new Error("Недостаточно средств для выполнения операции. Пополните счет.")
      else {
        val updated = account.copy(volume = new_vol)
        bank.put(account.id, updated)
        updated
      }
    }
  }

  override def moneyOrder(operation: MoneyOrder): Option[Unit] = {
    val cashb = operation.cat.get
    // 1 шаг - снятие денег с первого счета, если они вообще есть
    bank.get(operation.from_id).map { account =>
      if ((account.volume - operation.summa) < 0) throw new Error("Недостаточно средств для выполнения операции. Пополните счет.")
      else {
        val new_vol = account.volume - operation.summa
        if (cashb != "0") {
          val newest_vol = new_vol + round(operation.summa * getPercent(cashb) / 100)
          val updated = account.copy(volume = newest_vol)
          bank.put(account.id, updated)
          val perc = round(operation.summa * getPercent(cashb) / 100)
//          println(s"Отправителю было начислено ${round(operation.summa * getPercent(cashb) / 100)} тугриков кэшбеком.")
        }
        else {
          val updated = account.copy(volume = new_vol)
          bank.put(account.id, updated)
        }
      }
    }
    // 2 шаг - пополнение второго счета
    bank.get(operation.to_id).map { account =>
      val new_vol = account.volume + operation.summa
      val updated = account.copy(volume = new_vol)
      bank.put(account.id, updated)
      println(s"Пополнение счета на ${operation.summa} тугриков успешно завершено.")
    }
  }
}
