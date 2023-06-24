package misis.model

import java.util.UUID


case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountCreate(accountId: Int, value: Int)
case class AccountUpdate(accountId: Int, value: Int, toId: Option[Int])

trait Event
case class AccountCreated(accountId: Int, value: Int)
case class AccountUpdated(accountId: Int, value: Int, toId: Option[Int])
