package misis.kafka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountBalance, AccountCreate, AccountCreated, AccountUpdate, AccountUpdated}
import misis.repository.AccountRepository

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
      println(s"Аккаунт ${e.accountId} создан. Баланс: ${repository.accountMap(e.accountId).amount}")
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
      println(s"Баланс аккаунта ${command.accountId}: ${res}")
    }.to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает ВСЕ сообщения из топика AccountUpdate
  kafkaSource[AccountUpdate]
    .filter(command =>
      repository.accountMap.contains(command.accountId)
        && repository.accountMap(command.accountId).amount + command.value >= 0)
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
      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.accountMap(e.accountId).amount}")
      println("Операция успешно завершена.")
      e
    }
    .to(Sink.ignore)
    .run()
}