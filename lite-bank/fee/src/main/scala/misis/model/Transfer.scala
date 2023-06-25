package misis.model

case class Transfer(sourceId: Int, value: Int,destinationId: Int, category: Option[String])
