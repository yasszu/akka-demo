package app

import java.util.Properties

import com.twitter.app.App
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._

trait Consumer[K, V] {

  val timeout: Long = 100

  val topic: String

  val consumer: KafkaConsumer[K, V]

  final def run(): Unit = {
    onStart()
    sys.addShutdownHook {
      onWakeup()
    }
    try {
      while (true) {
        onSubscribe(consumer.poll(100).iterator.asScala)
      }
    } catch {
      case e: WakeupException => println("Stopping consumer...")
    }
    finally {
      onClose()
    }

  }

  protected def onStart(): Unit = {
    import java.util
    consumer.subscribe(util.Arrays.asList(topic))
  }

  protected def onWakeup(): Unit = {
    println("Wakeup a consumer")
    consumer.wakeup()
  }

  protected def onClose(): Unit = {
    println("Close a consumer")
    consumer.close()
  }

  def onSubscribe(records: Iterator[ConsumerRecord[K, V]]): Unit

}

trait ConsumerModule extends Consumer[String, String] {
  self: App =>

  override val topic: String = "timelines"

  val props: Properties = {
    val p = new Properties()
    p.put("bootstrap.servers", "localhost:9092")
    p.put("group.id", "test")
    p.put("enable.auto.commit", "true")
    p
  }

  val consumer = new KafkaConsumer[String, String](props, new StringDeserializer, new StringDeserializer)

  override def onSubscribe(records: Iterator[ConsumerRecord[String, String]]): Unit = {
    records.foreach { r =>
      val key = r.key()
      val value = r.value()
      println(s"key:$key, value:$value")
    }
  }

}
