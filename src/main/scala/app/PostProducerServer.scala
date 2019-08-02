package app

import java.util.Properties

import akka.actor.{ActorSystem, Cancellable}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class PostProducerServer {

  implicit val system: ActorSystem = ActorSystem("kafka-producer")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val topic: String = "post"

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "localhost:9092")
    p.setProperty("acks", "all")
    p.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    p.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    p
  }

  val producer = new KafkaProducer[String, String](props)

  def run(): Cancellable = {
    println("Start producer")
    system.scheduler.schedule(1 seconds, 5 seconds) {
      sendRecords()
    }
  }

  def sendRecords(): Unit = {
    (1 to 5).map { v =>
      val key = System.currentTimeMillis().toString
      val record = createRecord(key, v.toString)
      producer.send(record)
    }
  }

  def createRecord(key: String, value: String): ProducerRecord[String, String] = {
    new ProducerRecord[String, String](topic, key, value)
  }

}


object PostProducerServer {
  def apply(): PostProducerServer = new PostProducerServer()
}