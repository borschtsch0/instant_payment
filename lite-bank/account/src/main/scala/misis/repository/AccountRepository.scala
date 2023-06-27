package misis.repository

import misis.model.Account

import scala.collection.mutable
import scala.concurrent.Future

class AccountRepository(val accountId: Int){
  // карта счетов-аккаунтов mutable map
  // я посчитала, что стоит использовать созданный кейс класс Account
  val accountMap = mutable.Map[Int, Account]()

  def create(value: Int): Future[Account] = {
    accountMap.put(accountId, Account(accountId, value))
    Future.successful(accountMap(accountId))
  }

  def getBalance(acc: Int) = {
    accountMap(acc).amount
  }

  def update(value: Int): Future[Account] = {
    accountMap.put(accountId, accountMap(accountId).update(value)) // обновление аккаунта
    Future.successful(accountMap(accountId))
  }
}
