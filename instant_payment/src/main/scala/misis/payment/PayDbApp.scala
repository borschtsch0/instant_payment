package misis.payment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.payment.db.InitDb
import misis.payment.repository.{AccCbRepositoryDb, PaymentClient}
import misis.payment.route.{AccountRoute, CashbackRoute, HelloRoute}
import slick.jdbc.PostgresProfile.api._

// при добавлении трейта FailFastCirceSupport преобразование объектов в json производится автоматически
object PayDbApp extends App with FailFastCirceSupport {

  implicit val system: ActorSystem = ActorSystem("PayApp") // для использования akka определяем систему
  implicit val ec = system.dispatcher
  implicit val db = Database.forConfig("database.postgres")
  val port = ConfigFactory.load().getInt("port")


  new InitDb().prepare()
  val client = new PaymentClient
  val repository = new AccCbRepositoryDb(client)
// путь, по которому мы принимаем запросы к нашему приложению
  val helloRoute = new HelloRoute().route
  val accRoute = new AccountRoute(repository).route
  val cashRoute = new CashbackRoute(repository).route

  // создание сервера http
  Http().newServerAt("0.0.0.0", port).bind(helloRoute ~ accRoute ~ cashRoute)
}

// docker run -ti 55c8ddb312b0 bash - вход в контейнер
// docker-compose -f docker/docker-compose.yml up --build - перезапуск конфигурации приложения