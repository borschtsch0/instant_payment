package misis.payment.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.model._
import misis.payment.repository.{AccCbRepository, AccountNonExist, LessThanZero}
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
            case Failure(e: AccountNonExist) => complete(StatusCodes.NotFound, "Банковский счет не найден.")
          }
        }
      } ~ // Выдать список счетов другого пользователя приложения
      path("person" / Segment) { phone =>
        get {
          onComplete(repository.getAcc(GetAcc(phone))) {
            case Success(value) => complete(value)
            case Failure(e: AccountNonExist) => complete(StatusCodes.NotFound, "Банковские счета данного пользователя не найдены.")
          }
        }
      } ~ // Выдать список СВОИХ счетов
      path("own" / Segment) { phone =>
        get {
          onComplete(repository.getAccOwn(GetAcc(phone))) {
            case Success(value) => complete(value)
            case Failure(e: AccountNonExist) => complete(StatusCodes.NotFound, "Банковские счета не найдены.")
          }
        }
      } ~ // Пополнение счета
      path("account" / "topup") {
        (put & entity(as[TopupAcc])) { updateAcc =>
          onComplete(repository.topupAcc(updateAcc)) {
            case Success(value) => complete(value)
            case Failure(e) => complete(StatusCodes.NotFound, "Банковский счет не найден.")
          }
        }
      } ~ // Снятие денег со счета
      path("account" / "takeout") {
        (put & entity(as[TakeoutMoney])) { updateAcc =>
          onComplete(repository.takeoutMoney(updateAcc)) {
              case Success(value) => complete(value)
              case Failure(e: LessThanZero) => complete(StatusCodes.NotAcceptable, "Недостаточно средств для выполнения операции. Пополните счет.")
              case Failure(e: AccountNonExist) => complete(StatusCodes.NotFound, "Банковский счет не найден.")
            }
        }
      } ~
      path("account" / "order") {
        (put & entity(as[MoneyOrder])) { updateAccs =>
          onComplete(repository.moneyOrder(updateAccs)) {
              case Success(value) => complete(value)
              case Failure(e: LessThanZero) => complete(StatusCodes.NotAcceptable, "Недостаточно средств для выполнения операции. Пополните счет.")
              case Failure(e: AccountNonExist) => complete(StatusCodes.NotFound, "Банковский счет не найден.")
            }
        }
      }
}