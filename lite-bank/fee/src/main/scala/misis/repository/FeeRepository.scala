package misis.repository

import misis.kafka.FeeStreams
import misis.model.{Account}

import scala.collection.mutable
import scala.concurrent.Future

class FeeRepository(){
  val feeMap = mutable.Map[Int, Int]() // номер счета -> накопленная сумма
  val limit = 1500 // после накопления 1,5к тугриков, следующие переводы будут сопровождаться начислением комиссии
  val percent = 5 // процент комиссии для списания

  // Достигнут ли предел бесплатных переводов?
  // Если накопление счета не было создано, то оно создается
  def isLimitReached(acc: Int): Boolean = {
    feeMap(acc) >= limit
  }

  // (Если предел достигнут) получить процент вычета комиссии
  def getFeePercent(value: Int): Int = {
    value / 100 * percent
  }

  // обновить карту с накоплениями счетов
  def updateFee(accId: Int, value: Int) = {
    feeMap.contains(accId) match {
      case true =>
        val summa = feeMap(accId)
        feeMap += (accId -> (summa + value))
      case false =>
        feeMap + (accId -> value)
    }
  }

}
