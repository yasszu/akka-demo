package app

import akka.Done
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}

trait ConsumerServer[K, V] {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

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
      case _: WakeupException => logger.info("Stopping consumer...")
      case e: Throwable => e.printStackTrace()
    }
    finally {
      onClose()
    }
    Done
  }

  protected def onStart(): Unit = {
    logger.info("Start a consumer")
    consumer.subscribe(topic)
  }

  protected def onWakeup(): Unit = {
    logger.info("Wakeup a consumer")
    consumer.wakeup()
  }

  protected def onClose(): Unit = {
    logger.info("Close a consumer")
    consumer.close()
  }

  def onSubscribe(records: Iterator[(K, V)]): Unit

}

