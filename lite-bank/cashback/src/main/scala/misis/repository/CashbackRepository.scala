package misis.repository

import scala.collection.mutable
import scala.concurrent.Future

class CashbackRepository() {
  val cashbackMap = mutable.Map[String, Int]()

  def createCashback(name: String, percent: Int) = {
    cashbackMap + (name -> percent)
  }

  def updateCashback(name: String, percent: Int) = {
    cashbackMap += (name -> percent)
    cashbackMap
  }
}
