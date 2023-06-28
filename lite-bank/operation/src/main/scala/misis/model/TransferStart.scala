package misis.model

import java.time.Instant

case class TransferStart (sourceId: Int, value: Int, destinationId: Int, category: Option[String] = None)
