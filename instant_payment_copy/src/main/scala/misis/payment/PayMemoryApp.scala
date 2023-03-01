package misis.payment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.repository.AccCbRepositoryInMemory
import misis.payment.route.{AccountRoute, CashbackRoute, HelloRoute}

object PayMemoryApp extends App with FailFastCirceSupport {

  implicit val system: ActorSystem = ActorSystem("PayApp")
  implicit val ec = system.dispatcher
  val repository = new AccCbRepositoryInMemory

// путь, по которому мы принимаем запросы к нашему приложению
  val helloRoute = new HelloRoute().route
  val accRoute = new AccountRoute(repository).route
  val cashRoute = new CashbackRoute(repository).route

  // создание сервера http
  Http().newServerAt("0.0.0.0", 8080).bind(helloRoute ~ accRoute ~ cashRoute)
}
