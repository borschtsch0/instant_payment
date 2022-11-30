package misis.payment.model

case class Cashback(cat: String, percent: Int)

// операции над кэшбеком
case class CreateCashback(cat: String, perc: Int) // создание кэшбека
case class UpdateCashback(cat: String, perc: Int) // обновление кэшбека