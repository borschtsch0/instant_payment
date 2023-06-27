package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.FeeStreams
import misis.repository.FeeRepository

import scala.concurrent.ExecutionContext


class FeeRoute(repository: FeeRepository, streams: FeeStreams)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

//  implicit val commandTopicName: TopicName[AccountFeeCheck] = streams.simpleTopicName[AccountFeeCheck]

    def routes =
        (path("hello") & get) {
            complete("ok")
        } //~
//          (path("transfer") & post & entity(as[Transfer])) { transfer =>
//            streams.produceCommand(AccountFeeCheck(transfer.sourceId, transfer.value, transfer.destinationId, transfer.category))
//            complete(transfer)
//          }
}
