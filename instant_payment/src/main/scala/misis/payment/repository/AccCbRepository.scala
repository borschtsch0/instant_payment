package misis.payment.repository

import misis.payment.model._

import java.util.UUID

trait AccCbRepository {
  def offers(): List[Cashback]
  def createCashback(opt: CreateCashback): Cashback
  def updateCashback(opt: UpdateCashback): Option[Cashback]
  def getPercent(opt: String): Int

  def list(): List[Account]
  def createAcc(acc: CreateAcc): Account
  def get(acc: UUID): Account // получить конкретный счет
  def getAcc(acc: GetAcc): List[UUID] // получить все счета конкретного пользователя
  def getAccOwn(acc: GetAcc): List[(UUID, Int)] // получить все счета конкретного пользователя (от себя)
  def topupAcc(acc: TopupAcc): Option[Account]
  def takeoutMoney(acc: TakeoutMoney): Option[Account]
  def moneyOrder(acc: MoneyOrder): Option[Unit]
}
