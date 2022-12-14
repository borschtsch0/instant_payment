package misis.payment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.repository.AccCbRepositoryInMemory
import misis.payment.route.{AccountRoute, CashbackRoute, HelloRoute}

// при добавлении трейта FailFastCirceSupport преобразование объектов в json производится автоматически
object PayHttpApp extends App with FailFastCirceSupport {

  implicit val system: ActorSystem = ActorSystem("PayApp") // для использования akka определяем систему
  val repository = new AccCbRepositoryInMemory

// путь, по которому мы принимаем запросы к нашему приложению
  val helloRoute = new HelloRoute().route
  val accRoute = new AccountRoute(repository).route
  val cashRoute = new CashbackRoute(repository).route

  // создание сервера http
  Http().newServerAt("0.0.0.0", 8080).bind(helloRoute ~ accRoute ~ cashRoute)
}
