package misis.model

case class Account(id: Int, amount: Int) {
  def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountFeeCheck(accountId: Int, value: Int, toId: Int, category: Option[String] = None)
case class AccountUpdate(accountId: Int, value: Int, toId: Option[Int] = None, main_value: Option[Int] = None, category: Option[String] = None)

trait Event
case class AccountUpdated(
                           accountId: Int,
                           value: Int,
                           main_value: Option[Int] = None,
                           toId: Option[Int] = None,
                           category: Option[String] = None
                         )
