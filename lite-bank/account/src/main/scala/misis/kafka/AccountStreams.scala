package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model._
import misis.repository.AccountRepository

import java.time.Instant
import scala.concurrent.ExecutionContext

class AccountStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
  extends WithKafka {

  override def group = s"account-${repository.accountId}"

  // создается консьюмер, который слушает все сообщения из топика AccountCreate
  kafkaSource[AccountCreate]
    .filter(command => !repository.accountMap.contains(command.accountId)
      && repository.accountId == command.accountId
      && command.value >= 0)
    .mapAsync(1) { command =>
      repository
        .create(command.value)
        .map(_ =>
          AccountCreated(
            accountId = command.accountId,
            value = command.value
          )
        )
    }.to(kafkaSink)
    .run()

  // создается консьюмер, который слушает все сообщения из топика AccountCreated
  kafkaSource[AccountCreated]
    .filter(command => repository.accountMap.contains(command.accountId)
    )
    .map { e =>
      println(s"Счет ${e.accountId} создан. Баланс: ${repository.accountMap(e.accountId).amount}")
      println("Операция успешно завершена.")
      e
    }
    .to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает все сообщения из топика AccountBalance
  kafkaSource[AccountBalance]
    .filter(command => repository.accountMap.contains(command.accountId)
    )
    .map { command =>
      val res = repository.getBalance(command.accountId)
      println(s"Баланс счета ${command.accountId}: ${res}")
    }.to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает ВСЕ сообщения из топика AccountUpdate
  kafkaSource[AccountUpdate]
    .filter(command =>
      repository.accountMap.contains(command.accountId)
        && repository.accountMap(command.accountId).amount + command.value >= 0
    && command.value !=0)
    .mapAsync(1) { command =>
      repository
        .update(command.value)
        .map(_ =>
          AccountUpdated(
            accountId = command.accountId,
            value = command.value,
            toId = command.toId,
            fee = Option(command.fee.getOrElse(0)),
            category = command.category
          )
        )
    }
    .to(kafkaSink)
    .run()

  // создается консьюмер, который слушает все сообщения из топика AccountUpdated
  // относящиеся только к пополнению/уменьшению счета
  kafkaSource[AccountUpdated]
    .filter(command =>
      command.toId.isEmpty
        && repository.accountMap.contains(command.accountId)
    )
    .map { e =>
      println(s"Счет ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.accountMap(e.accountId).amount}")
      println("Операция успешно завершена.")
      e
    }
    .to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает сообщения из топика TransferContinue
  // где command.toId определен в системе
  kafkaSource[TransferContinue]
    .filter(command =>
    repository.accountMap.contains(command.toId))
    .map {e =>
      println(s"Средства успешно сняты со счета ${e.fromId}.")
      TransferDone(Instant.now(),e.fromId, e.toId, e.amount, e.category)
    }
    .to(kafkaSink)
    .run()
}