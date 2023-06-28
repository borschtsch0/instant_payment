package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.Streams
import misis.model.{AccountBalance, AccountCreate, AccountUpdate, GetCashback, TransferStart}
import misis.repository.Repository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class Route(streams: Streams, repository: Repository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {


    def routes =
        (path("hello") & get) {
            complete("ok")
        } ~
          path("create" / IntNumber / Segment) { (accountId, value) =>
            implicit val createTopicName: TopicName[AccountCreate] = streams.simpleTopicName[AccountCreate]

            val amount = value.toInt
            streams.produceCommand(AccountCreate(accountId, amount))
            complete(amount)
          } ~
            (path("update" / IntNumber / Segment) { (accountId, value) =>
              implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]

              val command = AccountUpdate(accountId, value.toInt, None)
              streams.produceCommand(command)
              complete(command)
            }) ~
            (path("transfer") & post & entity(as[TransferStart])) { transfer =>
                repository.transfer(transfer)
                complete(transfer)
            } ~
          path("balance" / IntNumber) { accountId =>
            implicit val balanceTopicName: TopicName[AccountBalance] = streams.simpleTopicName[AccountBalance]

            val command = AccountBalance(accountId)
            streams.produceCommand(command)
            complete(command)
          } ~
          path("get_cashback" / IntNumber) { accountId =>
            implicit val commandTopicName: TopicName[GetCashback] = streams.simpleTopicName[GetCashback]

            val command = GetCashback(accountId)
            streams.produceCommand(command)
            complete(command)
          }
}


