package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.repository.{FeeRepository}

import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"
}
