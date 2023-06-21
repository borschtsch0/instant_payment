package misis.repository

import misis.model.Account

import scala.collection.mutable
import scala.concurrent.Future

class AccountRepository(val accountId: Int, defAmount: Int){
    var account = Account(accountId, defAmount)
  // карта счетов-аккаунтов mutable map
   val account_list = mutable.Map[Int, Account]()

    def update(value: Int): Future[Account] = {
      account = account.update(value)
      //account_list += (account.id -> account)
      Future.successful(account)
    }
}

