package misis.repository

import misis.model.Account

import scala.collection.mutable
import scala.concurrent.Future

class AccountRepository(val accountId: Int){
  // карта счетов-аккаунтов mutable map
  // я посчитала, что стоит использовать созданный кейс класс Account
  val accountMap = mutable.Map[Int, Account]()

  def create(value: Int): Future[Account] = {
    accountMap.contains(accountId) match {
      case true =>
        println(s"Счет ${accountId} уже был создан. Баланс: ${value}")
        Future.successful(accountMap(accountId))
      case false =>
        accountMap.put(accountId, Account(accountId, value))
        println(s"Новый счет ${accountId} был успешно создан. Баланс: ${value}")
        Future.successful(accountMap(accountId))
    }
  }

  def update(value: Int): Future[Account] = {
    accountMap.put(accountId, accountMap(accountId).update(value)) // обновление аккаунта
    Future.successful(accountMap(accountId))
  }
}

case class AccountExists() extends Exception
