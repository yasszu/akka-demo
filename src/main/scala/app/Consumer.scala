package app

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._

trait Consumer {

  final def run(): Unit = {
    onStart()
    sys.addShutdownHook {
      onWakeup()
    }
    try {
      while (true) {
        onSubscribe()
      }
    } catch {
      case _: WakeupException => println("Stopping consumer...")
    }
    finally {
      onClose()
    }

  }

  protected def onStart(): Unit

  protected def onSubscribe(): Unit

  protected def onWakeup(): Unit

  protected def onClose(): Unit

}

case class ConsumerImpl() extends Consumer {

  val props: Properties = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", "PLAINTEXT_HOST://localhost:9092")
    p.setProperty("group.id", "test")
    p.setProperty("enable.auto.commit", "true")
    p.setProperty("auto.commit.interval.ms", "1000")
    p.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    p
  }

  val consumer = new KafkaConsumer[String, String](props, new StringDeserializer, new StringDeserializer)

  val topic: util.Collection[String] = Seq("timeline").asJavaCollection


  override protected def onStart(): Unit = {
    println("Start a consumer")
    consumer.subscribe(topic)
  }

  override protected def onSubscribe(): Unit = {
//        consumer.poll(100L).iterator.asScala.foreach { record =>
//          val key = record.key()
//          val value = record.value()
//          println(s"key:$key, value:$value")
//        }
    val records = consumer.poll(100)
    for (record <- records.iterator.asScala) {
      val key = record.key()
      val value = record.value()
      println(s"key:$key, value:$value")
    }
  }

  override protected def onWakeup(): Unit = {
    println("\nWakeup a consumer")
    consumer.wakeup()
  }

  override protected def onClose(): Unit = {
    println("Close a consumer")
    consumer.close()
  }

}


