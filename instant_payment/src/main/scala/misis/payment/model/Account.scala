package misis.payment.model

import java.util.UUID

// непосредственно банковский счет
case class Account(id: UUID = UUID.randomUUID(), owner: String, number: String, volume: Int)

case class CreateAcc(owner: String, number: String, volume: Int) // создание счета
case class GetAcc(number: String) // Получение списка счетов по номеру
case class TopupAcc(id: UUID, add: Int) // пополнение счета
case class TakeoutMoney(id: UUID, subtr: Int) // снятие денег со счета
case class MoneyOrder(from_id: UUID, to_id: UUID, cat: Option[String], summa: Int) // перевод денег между счетами + нужна категория