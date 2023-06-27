package misis.model

case class TransferStart (sourceId: Int, value: Int, destinationId: Int, category: Option[String] = None)
