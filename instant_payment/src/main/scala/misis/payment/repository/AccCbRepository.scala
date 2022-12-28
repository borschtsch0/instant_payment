package misis.payment.repository

import misis.payment.model._

import java.util.UUID
import scala.concurrent.Future

trait AccCbRepository {
  def offers(): Future[Seq[Cashback]]
  def createCashback(opt: CreateCashback): Future[Cashback]
  def updateCashback(opt: UpdateCashback): Future[Option[Cashback]]
  def getPercent(opt: String): Future[Int]

  def list(): Future[Seq[Account]]
  def createAcc(acc: CreateAcc): Future[Account]
  def get(acc: UUID): Future[Account] // получить конкретный счет
  def getAcc(acc: GetAcc): Future[Seq[UUID]] // получить все счета конкретного пользователя
  def getAccOwn(acc: GetAcc): Future[Seq[(UUID, Int)]] // получить все счета конкретного пользователя (от себя)
  def topupAcc(acc: TopupAcc): Future[Account]
  def takeoutMoney(acc: TakeoutMoney): Future[Account]
  def moneyOrder(acc: MoneyOrder): Future[Int]
}
