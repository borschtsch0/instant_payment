package misis.payment.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.model._
import misis.payment.repository.AccCbRepository
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class CashbackRoute(repository: AccCbRepository) extends FailFastCirceSupport {
  def route =
    (path("cashbacks") & get) {
      val offers = repository.offers()
      complete(offers)
    } ~
      path("cashback") {
        (post & entity(as[CreateCashback])) { newPerc =>
          complete(repository.createCashback(newPerc))
        }
      }
}
