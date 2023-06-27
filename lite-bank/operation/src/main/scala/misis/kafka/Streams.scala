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
    .filter(event => event.toId.nonEmpty
      && event.fee.nonEmpty)
    .map { e =>
      println(s"C аккаунта ${e.accountId} было списано ${-e.value} тугриков.")
      println("1 часть перевода осуществлена.")
      if (e.fee.get == 0)
        produceCommand(AccountUpdate(e.toId.get, -e.value))
      else if (e.value < e.fee.get)
        produceCommand(AccountUpdate(e.toId.get, -e.fee.get))
    }
    .to(Sink.ignore)
    .run()
}
