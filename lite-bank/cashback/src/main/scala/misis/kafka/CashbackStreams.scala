package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountUpdate, GetCashback, TransferDone}
import misis.repository.CashbackRepository

import scala.concurrent.ExecutionContext

class CashbackStreams(repository: CashbackRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
  override def group = "cashback"

  // создается консьюмер, который слушает все сообщения из топика TransferDone
  // и который будет обновлять кэшбеки отправителя денег
  kafkaSource[TransferDone]
    .filter(command =>
      command.category.nonEmpty
    )
    .map { e =>
      val cb_value = (e.amount / 100) * repository.cashbackMap(e.category.get)
      println(s"В ходе этой операции отправителю будет назначен кэшбек в размере ${cb_value} тугриков.")
      repository.updateCashback(e.fromId,e.amount,e.category.get)
      e
    }
    .to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает все сообщения из топика GetCashback
  // и который будет обновлять кэшбеки отправителя денег
  kafkaSource[GetCashback]
    .filter(command => repository.cbAccountMap.contains(command.accountId)
    )
    .map { e =>
      val value = repository.getCashback(e.accountId)
      println(s"На счет ${e.accountId} направлен кэшбек в размере ${value} тугриков.")
      produceCommand(AccountUpdate(e.accountId, value))
      repository.clearCashback(e.accountId)
      e
    }
    .to(Sink.ignore)
    .run()
}
