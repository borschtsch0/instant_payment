package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.Streams
import misis.model.AccountUpdate
import misis.repository.Repository
import misis.route.Route
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object OperationApp extends App  {
    implicit val system: ActorSystem = ActorSystem("OperationApp")
    implicit val ec = system.dispatcher

    private val streams = new Streams()
    private val repository = new Repository(streams)

    private val route = new Route(streams, repository)
    Http().newServerAt("0.0.0.0", 8071).bind(route.routes)
}
