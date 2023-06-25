package misis.kafka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountCreate, AccountCreated, AccountUpdate, AccountUpdated}
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
    .filter(command => repository.accountMap.contains(command.accountId))
    .map { e =>
      println(s"Аккаунт ${e.accountId} создан. Баланс: ${repository.accountMap(e.accountId).amount}")
      println("Операция успешно завершена.")
      e
    }
    .to(Sink.ignore)
    .run()

  // создается консьюмер, который слушает все сообщения из топика AccountUpdate
  kafkaSource[AccountUpdate]
    .filter(command => repository.accountMap.contains(command.accountId)
      && repository.accountMap(command.accountId).amount + command.value >= 0)
    .mapAsync(1) { command =>
      repository
        .update(command.value)
        .map(_ =>
          AccountUpdated(
            accountId = command.accountId,
            value = command.value,
            toId = command.toId,
            main_value = command.main_value,
            category = command.category
          )
        )
    }
    .to(kafkaSink)
    .run()

  // создается консьюмер, который слушает все сообщения из топика AccountUpdated
  kafkaSource[AccountUpdated]
    .filter(event => repository.accountMap.contains(event.accountId) && event.toId == None)
    .map { e =>
      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.accountMap(e.accountId).amount}")
      println("Операция успешно завершена.")
      e
    }
    .to(Sink.ignore)
    .run()

//  kafkaSource[AccountUpdated]
//    .filter(event => repository.accountMap.contains(event.accountId))
//    .map { e =>
//      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.accountMap(e.accountId).amount}")
//      e
//    }
//    .to(Sink.ignore)
//    .run()
}