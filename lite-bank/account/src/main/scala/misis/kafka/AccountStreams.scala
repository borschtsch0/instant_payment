package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext
import scala.runtime.Nothing$

class AccountStreams(repository: AccountRepository)(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) extends WithKafka {

  def group = s"account-${repository.accountId}"

  val AccountUpd = kafkaSource[AccountUpdate]
    .filter(command => repository.account.id == command.accountId)
    .mapAsync(1) { command =>
      repository.account.amount + command.value >= 0 match {
        case true =>
          repository
            .update(command.value)
            .map(_ =>
              AccountUpdated(
                accountId = command.accountId,
                value = command.value,
                category = command.category,
                tags = command.tags
              )
            )
        case false => throw new LessThanZero
      }
    }

  AccountUpd == LessThanZero match {
    case false => {
      AccountUpd.to(kafkaSink)
        .run()

      kafkaSource[AccountUpdated]
        .filter(event => repository.account.id == event.accountId)
        .map { e =>
          println(
            s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository.account.amount}"
          )
          e
        }
        .to(Sink.ignore)
        .run()
    }
    case true => throw LessThanZero()
  }

}

case class LessThanZero() extends Exception // Выбрана слишком большая сумма для перевода