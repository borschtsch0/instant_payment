package misis

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}

import scala.concurrent.Future
import scala.util.Success

object AkkaKafkaDemo extends App {
    implicit val system: ActorSystem = ActorSystem("App")
    implicit val ec = system.dispatcher

    val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)

    val producerConfig = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)

//            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    private val topic = "akka-kafka-topic"
    val kafkaSource = Consumer.committableSource(consumerSettings, Subscriptions.topics(topic))
        .map(message => message.record.value())

    def printFlow[T] = Flow[T].map { x =>
        println(s"flow: ${x}")
        x
    }

    val kafkaGraph = kafkaSource.via(printFlow).to(Sink.ignore)
    kafkaGraph.run()


    val produceGraph = Source(1 to 100)
            .map(_.toString)
            .map(value => new ProducerRecord[String, String](topic, value))
            .to(Producer.plainSink(producerSettings))

    produceGraph.run()
}
