package app

import akka.Done
import org.apache.kafka.common.errors.WakeupException

import scala.concurrent.{ExecutionContext, Future}

trait ConsumerServer[K, V] {

  val timeout: Long = 100

  val topic: String

  val consumer: Consumer[K, V]

  final def run()(implicit ec: ExecutionContext): Future[Done] = Future {
    onStart()
    sys.addShutdownHook {
      onWakeup()
    }
    try {
      while (true) {
        onSubscribe(consumer.poll(timeout))
      }
    } catch {
      case _: WakeupException => println("Stopping consumer...")
      case e => e.printStackTrace()
    }
    finally {
      onClose()
    }
    Done
  }

  protected def onStart(): Unit = {
    println("Start a consumer")
    consumer.subscribe(topic)
  }

  protected def onWakeup(): Unit = {
    println("\nWakeup a consumer")
    consumer.wakeup()
  }

  protected def onClose(): Unit = {
    println("Close a consumer")
    consumer.close()
  }

  def onSubscribe(records: Iterator[(K, V)]): Unit

}

