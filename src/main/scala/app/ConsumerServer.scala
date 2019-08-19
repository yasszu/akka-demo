package app

import akka.Done
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

trait ConsumerServer {

  def run()(implicit ec: ExecutionContext): Future[Done]

  def onStart(): Unit

  def onStop(): Unit

  def onClose(): Unit

}

trait ConsumerServerImpl[K, V] extends ConsumerServer {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  val timeout: FiniteDuration = 1000 milliseconds

  val topic: String

  val consumer: Consumer[K, V]

  final def run()(implicit ec: ExecutionContext): Future[Done] = {
    // Setup the consumer
    onStart()

    // Start subscribing messages
    val promise = Promise[Done]()
    Future {
      val done = Try {
        while (true) {
          subscribe(consumer.poll(timeout.toMillis))
        }
        Done
      } recoverWith {
        case _: WakeupException => Success(Done)
        case e =>
          logger.error("Non fatal error occurred")
          e.printStackTrace()
          handleNonFatalError(e)
      }
      done match {
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

  def onStart(): Unit = {
    logger.info("Start a consumer")
    consumer.subscribe(topic)
  }

  def onStop(): Unit = {
    logger.info("Stop a consumer")
    consumer.wakeup()
  }

  def onClose(): Unit = {
    logger.info("Close a consumer")
    consumer.close()
  }

  def subscribe(records: Iterator[(K, V)]): Unit

}

