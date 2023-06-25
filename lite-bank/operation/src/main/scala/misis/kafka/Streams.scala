package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model._

import scala.concurrent.ExecutionContext

class Streams()(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "operation"

  // создается консьюмер, который слушает сообщения из топика AccountUpdated,
  // относящиеся ТОЛЬКО к трансферу
  kafkaSource[AccountUpdated]
    .filter(event => event.toId.nonEmpty && event.main_value.isEmpty)
    .map { e =>
      println(s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}.")
      produceCommand(AccountUpdate(e.toId.get, -e.value, None))
    }
    .to(Sink.ignore)
    .run()
}
