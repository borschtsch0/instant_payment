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

class AccountRoute(repository: AccCbRepository) extends FailFastCirceSupport {
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
          complete(repository.get(id))
        }
      } ~ // Выдать список счетов другого пользователя приложения
      path("person" / Segment) { phone =>
        get {
          complete(repository.getAcc(GetAcc(phone)))
        }
      } ~ // Выдать список СВОИХ счетов
      path("own" / Segment) { phone =>
        get {
          complete(repository.getAccOwn(GetAcc(phone)))
        }
      } ~ // Пополнение счета
      path("account" / "operation") {
        (put & entity(as[TopupAcc])) { updateAcc =>
          complete(repository.topupAcc(updateAcc))
        }
      } ~ // Снятие денег со счета
      path("account" / "operation") {
        (put & entity(as[TakeoutMoney])) { updateAcc =>
          complete(repository.takeoutMoney(updateAcc))
        }
      } ~
      path("account" / "operation") {
        (put & entity(as[MoneyOrder])) { updateAccs =>
          complete(repository.moneyOrder(updateAccs))
        }
      }
}
