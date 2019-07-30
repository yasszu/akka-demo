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
        onSubscribe(consumer.poll(timeout).iterator.asScala)
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
    println("Start a consumer")
    consumer.subscribe(util.Arrays.asList(topic))
  }

  protected def onWakeup(): Unit = {
    println("\nWakeup a consumer")
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
