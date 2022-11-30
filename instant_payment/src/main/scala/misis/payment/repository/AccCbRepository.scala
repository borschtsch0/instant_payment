package misis.payment.repository

import misis.payment.model._

import java.util.UUID

trait AccCbRepository {
  def list(): List[Account]
  def offers(): List[Cashback]
  def createCashback(opt: CreateCashback): Cashback
  def updateCashback(opt: UpdateCashback): Option[Cashback]
  def getPercent(opt: String): Int
  def createAcc(acc: CreateAcc): Account
  def getAcc(acc: GetAcc): List[UUID]
  def getAccOwn(acc: GetAcc): List[(UUID, Int)]
  def topupAcc(acc: TopupAcc): Option[Account]
  def takeoutMoney(acc: TakeoutMoney): Option[Account]
  def moneyOrder(acc: MoneyOrder): Option[Unit]
}
