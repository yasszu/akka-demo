package app

import akka.Done
import com.twitter.util.Future
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.errors.WakeupException

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

trait Consumer[K, V] {

  val timeout: Long = 100

  val topic: String

  val consumer: KafkaConsumer[K, V]

  final def run()(implicit ec: ExecutionContext): Future[Done] = Future {
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
    Done
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
