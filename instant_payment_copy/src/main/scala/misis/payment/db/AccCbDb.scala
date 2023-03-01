package misis.payment.db

import misis.payment.model.{Account, Cashback}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.util.UUID

object AccCbDb {
  // Определим схему счета
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
    val id = column[UUID]("id", O.PrimaryKey)
    val owner = column[String]("owner")
    val number = column[String]("number")
    val volume = column[Int]("volume")

    def * = (id, owner, number, volume) <> ((Account.apply _).tupled, Account.unapply _)
  }
    val accountTable = TableQuery[AccountTable]

    // Определим схему кэшбека
    class CashbackTable(tag: Tag) extends Table[Cashback](tag, "cashbacks") {
      val cat = column[String]("cat", O.PrimaryKey)
      val percent = column[Int]("percent")

      def * = (cat, percent) <> ((Cashback.apply _).tupled, Cashback.unapply _)
    }
      val cashbackTable = TableQuery[CashbackTable]

}
