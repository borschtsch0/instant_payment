package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import misis.repository.{AccountExists, AccountRepository}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountRoute(repository: AccountRepository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    def routes =
        (path("hello") & get) {
            complete("ok")
        } ~
          (path("create" / Segment) & post) { value =>
            val amount = value.toInt
            amount >= 0 match {
              case true =>
                repository.create(amount)
                complete(amount)
              case false =>
                complete(StatusCodes.NotAcceptable, "Счет не был создан. Сумма должна быть положительной.")
            }

          }
}
