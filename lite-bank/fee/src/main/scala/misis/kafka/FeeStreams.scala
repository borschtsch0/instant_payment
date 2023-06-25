package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountFeeCheck, AccountUpdate, AccountUpdated}
import misis.repository.FeeRepository

import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"

  // создается консьюмер, который слушает сообщения из топика AccountFeeCheck
  kafkaSource[AccountFeeCheck]
    .map { e =>
      repository.updateFee(e.accountId, e.value)
      repository.isLimitReached(e.accountId) match {
        case true =>
          println(s"Для этой процедуры предусмотрена комиссия, которая составит ${repository.getFeePercent(e.value)} тугриков.")
          produceCommand(AccountUpdate(e.accountId, -(e.value + repository.getFeePercent(e.value)), Some(e.toId), Some(e.value), e.category))
        case false =>
          produceCommand(AccountUpdate(e.accountId, -e.value, Some(e.toId), None, e.category))
      }
    }
    .to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает сообщения из топика AccountUpdated,
  // относящиеся ТОЛЬКО к трансферу
  kafkaSource[AccountUpdated]
    .filter(event => event.toId.nonEmpty && event.main_value.nonEmpty)
    .map { e =>
      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}.")
      produceCommand(AccountUpdate(accountId = e.toId.get, value = -e.main_value.get))
    }
    .to(Sink.ignore)
    .run()
}
