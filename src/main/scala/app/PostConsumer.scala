package app

import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

case class PostConsumer() extends Consumer[String, String] {

  override val topic: String = "post"

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "localhost:9092")
    p.setProperty("group.id", "test")
    p.setProperty("enable.auto.commit", "true")
    p.setProperty("auto.commit.interval.ms", "1000")
    p
  }

  val consumer =
    new KafkaConsumer[String, String](
      props,
      new StringDeserializer,
      new StringDeserializer)

  override def onSubscribe(records: Iterator[ConsumerRecord[String, String]]): Unit = {
    records.foreach { r =>
      val key = r.key()
      val value = r.value()
      println(s"key:$key, value:$value")
    }
  }

}