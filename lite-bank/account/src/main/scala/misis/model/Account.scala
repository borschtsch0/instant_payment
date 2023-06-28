package misis.model

import java.time.Instant
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
case class TransferContinue(start_time: Instant, fromId: Int, toId: Int, amount: Int, category: Option[String] = None)

trait Event
case class AccountCreated(accountId: Int, value: Int)
case class AccountUpdated(
                           accountId: Int,
                           value: Int,
                           fee: Option[Int] = None,
                           toId: Option[Int] = None,
                           publishedAt: Option[Instant] = Some(Instant.now()),
                           category: Option[String] = None
                         )
case class TransferDone(start_time: Instant, fromId: Int, toId: Int, amount: Int, category: Option[String] = None)
