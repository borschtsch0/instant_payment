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

    private val repository = new FeeRepository()
    private val streams = new FeeStreams(repository)

    private val route = new FeeRoute(repository, streams)
    Http().newServerAt("0.0.0.0", 8072).bind(route.routes)
}
