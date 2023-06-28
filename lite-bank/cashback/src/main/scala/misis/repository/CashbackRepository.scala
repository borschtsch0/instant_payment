package misis.repository

import scala.collection.mutable
import scala.concurrent.Future

class CashbackRepository() {
  val cashbackMap = mutable.Map[String, Int](("silver" -> 1),("gold" -> 3),("platinum"-> 5))

  val cbAccountMap = mutable.Map[Int, Int]()

//  def createCashback(name: String, percent: Int) = {
//    cashbackMap + (name -> percent)
//    Future.successful(cashbackMap(name))
//  }
//
//  def updateCashbackValue(name: String, percent: Int) = {
//    cashbackMap += (name -> percent)
//    Future.successful(cashbackMap(name))
//  }

  def updateCashback(accId: Int, amount: Int, cat: String) = {
    if (cashbackMap.contains(cat)) {
      if (!cbAccountMap.contains(accId)) {
        val value = (amount / 100) * cashbackMap(cat)
        cbAccountMap += (accId -> value)
      }
      else {
        val value = (amount / 100) * cashbackMap(cat)
        cbAccountMap += (accId -> (cbAccountMap(accId) + value))
      }
    }
    cbAccountMap(accId)
  }

  def getCashback(accId: Int) = {
    cbAccountMap(accId)
  }

  def clearCashback(accId: Int) = {
    cbAccountMap += (accId -> 0)
    cbAccountMap(accId)
  }
}
