package misis.model

import java.time.Instant

case class Account(id: Int, amount: Int) {
  def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountUpdate(accountId: Int,
                         value: Int,
                         toId: Option[Int] = None,
                         fee: Option[Int] = None,
                         category: Option[String] = None)
case class TransferContinue(start_time: Instant, fromId: Int, toId: Int, amount: Int, category: Option[String] = None)


trait Event
case class AccountUpdated(
                           accountId: Int,
                           value: Int,
                           fee: Option[Int] = None,
                           toId: Option[Int] = None,
                           publishedAt: Option[Instant] = Some(Instant.now()),
                           category: Option[String] = None
                         )
case class TransferDone(start_time: Instant, fromId: Int, toId: Int, amount: Int, category: Option[String] = None)
