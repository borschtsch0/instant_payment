package misis.payment.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.model._
import misis.payment.repository.{AccCbRepository, CashbackNonExist}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.util.{Failure, Success}

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
      } ~
      path("cashback" / "update") {
        (put & entity(as[UpdateCashback])) { newPerc =>
          onComplete(repository.updateCashback(newPerc)) {
            case Success(value) => complete(value)
            case Failure(e: CashbackNonExist) => complete(StatusCodes.NotFound, "Такой тип кэшбека не существует.")
          }
        }
      } ~
      path("cashback" / Segment) { cat =>
        get {
          onComplete(repository.getPercent(cat)) {
            case Success(value) => complete(value)
            case Failure(e: CashbackNonExist) => complete(StatusCodes.NotFound, "Такой тип кэшбека не существует.")
          }
        }
      }

}
