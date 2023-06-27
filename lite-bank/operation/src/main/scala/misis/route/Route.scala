package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.Streams
import misis.model.{AccountBalance, AccountCreate, AccountUpdate, TransferStart}
import misis.repository.Repository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class Route(streams: Streams, repository: Repository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

  implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
  implicit val createTopicName: TopicName[AccountCreate] = streams.simpleTopicName[AccountCreate]
  implicit val balanceTopicName: TopicName[AccountBalance] = streams.simpleTopicName[AccountBalance]

    def routes =
        (path("hello") & get) {
            complete("ok")
        } ~
          path("create" / IntNumber / Segment) { (accountId, value) =>
            val amount = value.toInt
            streams.produceCommand(AccountCreate(accountId, amount))
            complete(amount)
          } ~
            (path("update" / IntNumber / Segment) { (accountId, value) =>
                val command = AccountUpdate(accountId, value.toInt, None)
                streams.produceCommand(command)
                complete(command)
            }) ~
            (path("transfer") & post & entity(as[TransferStart])) { transfer =>
                repository.transfer(transfer)
                complete(transfer)
            } ~
          path("balance" / IntNumber) { accountId =>
            val command = AccountBalance(accountId)
            streams.produceCommand(command)
            complete(command)
          }
}


