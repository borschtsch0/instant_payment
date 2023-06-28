package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import io.circe.syntax._
import misis.{TopicName, WithKafka}
import misis.model.{AccountUpdate, TransferDone}
import misis.repository.FeeRepository

import java.time.Instant
import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"

  implicit val commandTopicName: TopicName[AccountUpdate] = simpleTopicName[AccountUpdate]

  // Начисление комиссии
  kafkaSource[TransferDone]
//    .filter(command =>
//    Instant.now().compareTo(command.start_time) >= -2
//    && Instant.now().compareTo(command.start_time) <= 0)
    .map { e =>
      repository.updateFee(e.fromId, e.amount)
      repository.isLimitReached(e.fromId, e.amount) match {
        case true =>
          println(s"Для данной процедуры предусмотрена комиссия, которая составит ${repository.getFeePercent(e.amount)} тугриков.")
          AccountUpdate(e.fromId, -repository.getFeePercent(e.amount))
        case false =>
          println("Для данной процедуры комиссия не предусмотрена.")
          AccountUpdate(e.fromId, 0)
      }
    }
    .to(kafkaSink)
    .run()
}