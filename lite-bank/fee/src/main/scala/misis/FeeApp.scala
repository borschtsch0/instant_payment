package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.FeeStreams
import misis.model.AccountUpdate
import misis.repository.FeeRepository
import misis.route.FeeRoute
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object FeeApp extends App  {
    implicit val system: ActorSystem = ActorSystem("MyApp")
    implicit val ec = system.dispatcher
    val port = ConfigFactory.load().getInt("port")

    private val repository = new FeeRepository()
    private val streams = new FeeStreams(repository)

    private val route = new FeeRoute()
    Http().newServerAt("0.0.0.0", port).bind(route.routes)
}
