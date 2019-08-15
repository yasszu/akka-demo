package app

import akka.Done
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

trait ConsumerServer[K, V] {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  val timeout: FiniteDuration = 1000 milliseconds

  val topic: String

  val consumer: Consumer[K, V]

  final def run()(implicit ec: ExecutionContext): Future[Done] = {
    // Setup the consumer
    onStart()

    // Stop the consumer when the VM exits
    sys.addShutdownHook {
      logger.info("Stopping consumer...")
      onWakeup()
    }

    val promise = Promise[Done]()

    // Start subscribing messages
    Future {
      Try {
        while (true) {
          onSubscribe(consumer.poll(timeout.toMillis))
        }
        Done
        // throw new IllegalStateException("Test")
      } recoverWith {
        case _: WakeupException => Success(Done)
        case e =>
          logger.error("Non fatal error occurred")
          e.printStackTrace()
          handleNonFatalError(e)
      } match {
        case Success(done) =>
          onClose()
          promise success done
        case Failure(e) =>
          onClose()
          promise failure e
      }
    }

    promise.future
  }

  def handleNonFatalError(error: Throwable): Try[Done] = {
    Failure(error)
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

