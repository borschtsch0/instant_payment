package misis.payment.db

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import misis.payment.db.AccCbDb._

class InitDb(implicit val ec: ExecutionContext, db: Database) {
  def prepare(): Future[_] ={
    db.run(cashbackTable.schema.createIfNotExists)

    db.run(accountTable.schema.createIfNotExists)
  }
}
