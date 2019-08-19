package app

import java.util.Properties

import akka.actor.{ActorSystem, Cancellable}
import example.avro.messages.Post
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class PostProducerServer { self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  val topic: String = "post"

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "localhost:9092")
    p.setProperty("acks", "all")
    p.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    p.setProperty("schema.registry.url", "http://0.0.0.0:8081")
    p.setProperty("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
    p
  }

  val producer = new KafkaProducer[String, Post](props)

  def run()(implicit ec: ExecutionContext, system: ActorSystem): Cancellable = {
    logger.info("Start a producer")
    system.scheduler.schedule(1 seconds, 1 seconds) {
      logger.info("Send messages")
      sendRecords(5)
    }
  }

  def sendRecords(amount: Int): Unit = {
    (1 to amount).map { v =>
      val timestamp = System.currentTimeMillis()
      val post = new Post()
      post.setId(v)
      post.setTimestamp(timestamp)
      val record = createRecord("none", post)
      producer.send(record)
    }
  }

  def createRecord(key: String, value: Post): ProducerRecord[String, Post] = {
    new ProducerRecord[String, Post](topic, key, value)
  }

}


object PostProducerServer {
  def apply(): PostProducerServer = new PostProducerServer()
}