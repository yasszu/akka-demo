package app

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object KafkaProducerServer extends App {

  implicit val system: ActorSystem = ActorSystem("KafkaProducer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val askTimeout: Timeout = Timeout(30.seconds)

  val bootstrapServers = system.settings.config.getString("app.kafka.bootstrap.servers")
  val topic = system.settings.config.getString("app.kafka.topic")
  val groupId = system.settings.config.getString("app.kafka.group")
  val producerConfig = system.settings.config.getConfig("akka.kafka.producer")

  val producerSettings =
    ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  system.scheduler.schedule(1 seconds, 10 seconds) {
    Source(1 to 10)
      .map(_.toString)
      .map { value =>
        val key = System.currentTimeMillis().toString
        println(s"key:$key, value:$value")
        new ProducerRecord[String, String](topic, key, value)
      }
      .runWith(Producer.plainSink(producerSettings))
  }

}