package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import misis.repository.{AccountExists, AccountRepository, LessThanZero}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountRoute(repository: AccountRepository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    def routes =
        (path("hello") & get) {
            complete("ok")
        } //~
//          path("create" / Segment) { value =>
//            val amount = value.toInt
//            onComplete(repository.create(amount)) {
//              case Failure(exception: AccountExists) => complete(StatusCodes.NotAcceptable, "Счет уже был создан.")
//              case Failure(exception: LessThanZero) => complete(StatusCodes.NotAcceptable, "Счет не был создан. Сумма должна быть положительной.")
//              case Success(value) => complete(amount)
//            }
//          }
}
