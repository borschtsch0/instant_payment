package misis.model

case class Transfer(sourceId: Int, destinationId: Int, value: Int, category: Option[String])
