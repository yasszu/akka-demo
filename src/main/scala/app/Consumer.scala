package app

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

trait Consumer[K, V] {

  def subscribe(topic: String): Unit

  def poll(timeout: Long): Iterator[(K, V)]

  def wakeup(): Unit

  def close(): Unit

}

object Consumer {
  def apply[K, V](props: Properties): Consumer[K, V] = new KafkaConsumerImpl[K, V](props)
}

class KafkaConsumerImpl[K, V](props: Properties) extends Consumer[K, V] {

  val consumer = new KafkaConsumer[K, V](props)

  override def subscribe(topic: String): Unit = {
    import java.util
    consumer.subscribe(util.Arrays.asList(topic))
  }

  override def poll(timeout: Long): Iterator[(K, V)] = {
    consumer.poll(timeout).iterator().asScala.map { record =>
      (record.key(), record.value())
    }
  }

  override def wakeup(): Unit = consumer.wakeup()

  override def close(): Unit = consumer.close()

}