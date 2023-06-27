package misis.model

import java.util.UUID


case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountCreate(accountId: Int, value: Int)
case class AccountBalance(accountId: Int)
case class AccountUpdate(accountId: Int, value: Int,
                         toId: Option[Int] = None,
                         fee: Option[Int] = None,
                         category: Option[String] = None)

trait Event
case class AccountUpdated(
                           accountId: Int,
                           value: Int,
                           fee: Option[Int] = None,
                           toId: Option[Int] = None,
                           category: Option[String] = None
                         )

