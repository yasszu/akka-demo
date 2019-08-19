package app

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ConsumerManager()(implicit ec: ExecutionContext) {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  private var consumers: Map[Int, ConsumerServer] = Map.empty

  def addConsumer(consumer: ConsumerServer): Unit = {
    val pid = consumers.size + 1
    consumers ++= Map(pid -> consumer)
  }

  def runAll(): Unit = {
    consumers.keys.foreach(run)
  }

  def run(pid: Int): Unit = {
    consumers(pid).run() onComplete {
      case Success(done) =>
        logger.info("Stopped successfully")
      case Failure(_) =>
        logger.error("Unexpected error occurred")
        logger.error("Restart")
        run(pid)
    }
  }

  def shutdown(): Unit = {
    consumers.values.foreach(_.onStop())
  }

}

object ConsumerManager {
  def apply()(implicit ec: ExecutionContext): ConsumerManager = new ConsumerManager()(ec)
}