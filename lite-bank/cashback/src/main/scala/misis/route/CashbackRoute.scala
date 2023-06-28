package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.CashbackStreams
import misis.model.GetCashback
import misis.repository.CashbackRepository

import scala.concurrent.ExecutionContext


class CashbackRoute(implicit ec: ExecutionContext) extends FailFastCirceSupport {

  def routes =
        (path("hello") & get) {
            complete("ok")
        }
}
