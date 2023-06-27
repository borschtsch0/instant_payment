package misis.kafka

import akka.actor.ActorSystem
import io.circe.generic.auto._
import io.circe.syntax._
import misis.{TopicName, WithKafka}
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.FeeRepository

import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"

  implicit val commandTopicName: TopicName[AccountUpdate] = simpleTopicName[AccountUpdate]

  // Начисление комиссии
  kafkaSource[AccountUpdate]
    .filter(event =>
      event.toId.nonEmpty && event.fee.isEmpty)
    .map { e =>
      repository.updateFee(e.accountId, e.value)
      repository.isLimitReached(e.accountId, e.value) match {
        case true =>
          println(s"Для этой процедуры предусмотрена комиссия, которая составит ${repository.getFeePercent(e.value)} тугриков.")
          AccountUpdate(e.accountId, -repository.getFeePercent(e.value))
        case false =>
          println("Для данной процедуры комиссия не предусмотрена.")
          AccountUpdate(e.accountId, 0)
      }
    }
    .to(kafkaSink)
    .run()
}

//  // создается консьюмер, который слушает сообщения из топика AccountFeeCheck
//  kafkaSource[AccountFeeCheck]
//    .map { e =>
//      repository.updateFee(e.accountId, e.value)
//      repository.isLimitReached(e.accountId) match {
//        case true =>
//          println(s"Для этой процедуры предусмотрена комиссия, которая составит ${repository.getFeePercent(e.value)} тугриков.")
//          produceCommand(AccountUpdate(e.accountId, -(e.value + repository.getFeePercent(e.value)), Some(e.toId), Some(e.value), e.category))
//        case false =>
//          produceCommand(AccountUpdate(e.accountId, -e.value, Some(e.toId), None, e.category))
//      }
//    }
//    .to(Sink.ignore)
//    .run()