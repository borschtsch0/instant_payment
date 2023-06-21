package misis.kafka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext

class AccountStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
  extends WithKafka {

  def group = s"account-${repository.accountId}"

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
            toId = command.toId//,
//            category = command.category,
//            tags = command.tags
          )
        )
    }
    .to(kafkaSink)
    .run()

  kafkaSource[AccountUpdated]
    .filter(event => repository.accountMap.contains(event.accountId))
    .map { e =>
      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.accountMap(e.accountId).amount}")
      if (e.toId.nonEmpty) {
        produceCommand(AccountUpdate(e.toId.get, -e.value, None))
      } else {
        println("Операция успешно завершена.")
        e
      }
    }
    .to(Sink.ignore)
    .run()

//  kafkaSource[AccountUpdated]
//    .filter(event => repository.account.id == event.accountId)
//    .map { e =>
//      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.account.amount}")
//      e
//    }
//    .to(Sink.ignore)
//    .run()
}