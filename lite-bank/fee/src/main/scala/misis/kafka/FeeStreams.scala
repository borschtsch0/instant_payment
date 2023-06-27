package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.FeeRepository

import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"

  // Начисление комиссии
  kafkaSource[AccountUpdate]
    .filter(event =>
      event.toId.nonEmpty)
    .map { e =>
      repository.updateFee(e.accountId, e.value).map(_ =>
        repository.isLimitReached(e.accountId) match {
          case true =>
            println(s"Для этой процедуры предусмотрена комиссия, которая составит ${repository.getFeePercent(e.value)} тугриков.")
            produceCommand(AccountUpdate(e.accountId, -repository.getFeePercent(e.value), e.toId, Some(e.value), e.category))
          case false =>
            println("Для данной процедуры комиссия не предусмотрена.")
            AccountUpdated(e.accountId, 0, e.toId, Some(e.value), e.category)
        }
      )
    }
    .to(Sink.ignore)
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