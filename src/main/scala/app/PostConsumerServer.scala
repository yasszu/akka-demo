package app

import java.util.Properties

import example.avro.messages.Post
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig}

class PostConsumerServer() extends ConsumerServer[String, Post] {

  override val topic: String = "post"

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "localhost:9092")
    p.setProperty("group.id", "test")
    p.setProperty("enable.auto.commit", "true")
    p.setProperty("auto.commit.interval.ms", "1000")
    p.setProperty("schema.registry.url", "http://0.0.0.0:8081")
    p.setProperty(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
    p.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p.setProperty("value.deserializer", classOf[KafkaAvroDeserializer].getCanonicalName)
    p
  }

  override val consumer: Consumer[String, Post] = KafkaConsumer[String, Post](props)

  override def onSubscribe(records: Iterator[(String, Post)]): Unit = {
    records.foreach { case (key: String, post: Post) =>
      println(s"key:$key, value: {id:${post.getId}, timestamp: ${post.getTimestamp}}")
    }
  }

}

object PostConsumerServer {
  def apply(): PostConsumerServer = new PostConsumerServer()
}