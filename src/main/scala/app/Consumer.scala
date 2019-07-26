package app

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException

import scala.collection.JavaConverters._

trait Consumer {

  final def run(): Unit = {
    try {
      while (true) {
        sys.addShutdownHook {
          onWakeup()
        }
        onStart()
      }
    } catch {
      case e: WakeupException => println("Stopping consumer...")
    }
    finally {
      onClose()
    }

  }

  protected def onStart(): Unit

  protected def onWakeup(): Unit

  protected def onClose(): Unit

}

case class ConsumerImpl() extends Consumer {

  val props: Properties = {
    val p = new Properties()
    p.put("bootstrap.servers", "localhost:9092")
    p.put("group.id", "test")
    p.put("enable.auto.commit", "true")
    p
  }

  def consumer = new KafkaConsumer[String, String](props)

  override protected def onStart(): Unit = {
    val records = consumer.poll(100).iterator.asScala
    records.foreach { r =>
      val key = r.key()
      val value = r.value()
      println(s"key:$key, value:$value")
    }
  }

  override protected def onWakeup(): Unit = {
    println("Wakeup a consumer")
    consumer.wakeup()
  }

  override protected def onClose(): Unit = {
    println("Close a consumer")
    consumer.close()
  }

}


