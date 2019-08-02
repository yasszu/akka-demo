package app

import java.util.Properties

class PostConsumerServer() extends ConsumerServer[String, String] {

  override val topic: String = "post"

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "localhost:9092")
    p.setProperty("group.id", "test")
    p.setProperty("enable.auto.commit", "true")
    p.setProperty("auto.commit.interval.ms", "1000")
    p.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p
  }

  override val consumer: Consumer[String, String] = Consumer[String, String](props)

  override def onSubscribe(records: Iterator[(String, String)]): Unit = {
    records.foreach { case (key, value) =>
      println(s"key:$key, value:$value")
    }
  }

}

object PostConsumerServer {
  def apply(): PostConsumerServer = new PostConsumerServer()
}