package misis.payment.repository

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import misis.payment.model.{MoneyOrder, OrderRequest}

import scala.concurrent.{ExecutionContext, Future}

class PaymentClient(implicit val ec: ExecutionContext, actorSystem: ActorSystem) extends FailFastCirceSupport {
  def payment(order: OrderRequest): Future[MoneyOrder] = {
    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = s"http://localhost:8081/account/topup",
      entity = HttpEntity(MediaTypes.`application/json`, order.asJson.noSpaces)
    )
    for {
      response <- Http().singleRequest(request)
      result <- Unmarshal(response).to[MoneyOrder]
    } yield result

  }

}
