package misis.repository

import misis.kafka.FeeStreams
import misis.model.UpdateFee

import scala.collection.mutable
import scala.concurrent.Future

class FeeRepository(){
  val feeMap = mutable.Map[Int, Int]() // номер счета -> накопленная сумма
  val limit = 1500 // после накопления 1,5к тугриков, следующие переводы будут сопровождаться начислением комиссии
  val percent = 5 // процент комиссии для списания

  // Достигнут ли предел бесплатных переводов?
  // Если накопление счета не было создано, то оно создается
  def isLimitReached(acc: Int): Boolean = {
    feeMap.contains(acc) match {
      case true =>
        feeMap(acc) >= limit
      case false =>
        feeMap.put(acc, 0)
        feeMap(acc) >= limit
    }
  }

  // (Если предел достигнут) получить процент вычета комиссии
  def getFeePercent(value: Int): Int = {
    value / 100 * percent
  }

  // (Если не достигнут предел) обновить карту с накоплениями счетов
  def updateFee(upd: UpdateFee) = {
    feeMap.contains(upd.accountId) match {
      case true =>
        val summa = feeMap(upd.accountId)
        feeMap.put(upd.accountId, summa + upd.value)
      case false =>
        feeMap.put(upd.accountId, upd.value)
    }
    feeMap(upd.accountId)
  }

}
