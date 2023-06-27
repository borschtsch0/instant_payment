package misis.repository

import misis.TopicName
import misis.kafka.Streams
import misis.model.{AccountUpdate, AccountUpdated, TransferStart}
import io.circe.generic.auto._

class Repository(streams: Streams){
    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]

//    implicit val updatedTopicName: TopicName[AccountUpdated] = streams.simpleTopicName[AccountUpdated]

    def transfer(transfer: TransferStart) = {
        if (transfer.value > 0) {
          streams.produceCommand(AccountUpdate(transfer.sourceId, -transfer.value, Some(transfer.destinationId), None, transfer.category))
        }
    }
}

