package misis.payment

import misis.payment.model.{CreateAcc, CreateCashback, GetAcc, MoneyOrder, TakeoutMoney, TopupAcc}
import misis.payment.repository.AccCbRepositoryInMemory

import java.util.UUID

object PayApp extends App {
  val repository = new AccCbRepositoryInMemory

  repository.createCashback(CreateCashback("national", 20))
  repository.createCashback(CreateCashback("regional", 10))

  // Выведем все зарегистрированные кэшбеки
  println(repository.offers())

  val lvt1 = repository.createAcc(CreateAcc("Lars von Trier", "+7(911)111-11-11", 100000))
  val lvt2 = repository.createAcc(CreateAcc("Lars von Trier", "+7(911)111-11-11", 50000))
  val lvt3 = repository.createAcc(CreateAcc("Lars von Trier", "+7(911)111-11-11", 25000))
  val cw = repository.createAcc(CreateAcc("Christoph Waltz", "+7(922)222-22-22", 14000))
  val ss = repository.createAcc(CreateAcc("Stellan Skarsgard", "+7(933)333-33-33", 20000))

  val lvt_number = lvt1.number

  // Выведем все зарегистрированные счета
  println(repository.list())
  println()

  // Посмотрим все возможные счета у Ларса фон Триера
  println(repository.getAccOwn(GetAcc(lvt_number)))
  val id_lvt = repository.getAcc(GetAcc(lvt_number))
  // Проведем несколько операций только между его счетами
  repository.takeoutMoney(TakeoutMoney(id_lvt.last, 2500))
  repository.moneyOrder(MoneyOrder(id_lvt.last, id_lvt.tail.head, None, 2500))
  println(repository.list().filter(obj => obj.owner == lvt2.owner))
  println()

  // Посмотрим на другие операции с другими клиентами
  val id_cw = repository.getAcc(GetAcc(cw.number))
  repository.topupAcc(TopupAcc(id_cw.head, 6000))
  val id_ss = repository.getAcc(GetAcc(ss.number))
  repository.moneyOrder(MoneyOrder(id_cw.head, id_ss.head, Option("regional"), 3000))

  println(repository.list().filterNot(obj => obj.owner == lvt2.owner))
}
