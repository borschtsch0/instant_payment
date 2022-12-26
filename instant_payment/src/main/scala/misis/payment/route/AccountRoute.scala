package misis.payment.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.model._
import misis.payment.repository.{AccCbRepository, LessThanZero}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountRoute(repository: AccCbRepository)(implicit val ec: ExecutionContext) extends FailFastCirceSupport {
  def route =
    (path("accounts") & get) {
      val list = repository.list()
      complete(list)
    } ~
      path("account") {
        (post & entity(as[CreateAcc])) { newAcc =>
          complete(repository.createAcc(newAcc))
        }
      } ~
      path("account" / JavaUUID) { id =>
        get {
          onComplete(repository.get(id)) {
            case Success(value) => complete(value)
            case Failure(e: NoSuchElementException) => complete(StatusCodes.NotFound)
          }
        }
      } ~ // Выдать список счетов другого пользователя приложения
      path("person" / Segment) { phone =>
        get {
          onComplete(repository.getAcc(GetAcc(phone))) {
            case Success(value) => complete(value)
            case Failure(e: NoSuchElementException) => complete(StatusCodes.NotFound)
          }
        }
      } ~ // Выдать список СВОИХ счетов
      path("own" / Segment) { phone =>
        get {
          onComplete(repository.getAccOwn(GetAcc(phone))) {
            case Success(value) => complete(value)
            case Failure(e: NoSuchElementException) => complete(StatusCodes.NotFound)
          }
        }
      } ~ // Пополнение счета
      path("account" / "operation") {
        (put & entity(as[TopupAcc])) { updateAcc =>
          complete(repository.topupAcc(updateAcc))
        }
      } ~ // Снятие денег со счета
      path("account" / "operation") {
        (put & entity(as[TakeoutMoney])) { updateAcc =>
          onComplete(repository.takeoutMoney(updateAcc)) {
              case Success(value) => complete(value)
              case Failure(e: NoSuchElementException) => complete(StatusCodes.NotFound)
              case Failure(e: LessThanZero) => complete(StatusCodes.NotAcceptable, "Недостаточно средств для выполнения операции. Пополните счет.")
            }
        }
      } ~
      path("account" / "operation") {
        (put & entity(as[MoneyOrder])) { updateAccs =>
          onComplete(repository.moneyOrder(updateAccs)) {
              case Success(value) => complete(value)
              case Failure(e: LessThanZero) => complete(StatusCodes.NotAcceptable, "Недостаточно средств для выполнения операции. Пополните счет.")
            }
        }
      }
}