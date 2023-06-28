package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.CashbackStreams
import misis.repository.CashbackRepository
import misis.route.CashbackRoute
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object CashbackApp extends App  {
    implicit val system: ActorSystem = ActorSystem("MyApp")
    implicit val ec = system.dispatcher

    private val repository = new CashbackRepository()
    private val streams = new CashbackStreams(repository)

    private val route = new CashbackRoute()
    Http().newServerAt("0.0.0.0", 8073).bind(route.routes)
}
