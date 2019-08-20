package app

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ConsumerManager {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  private var servers: Map[Int, ConsumerApplication] = Map.empty

  private var factories: Map[Int, ConsumerServerFactory] = Map.empty

  def addFactory(factory: ConsumerServerFactory): Unit = {
    val pid = factories.size + 1
    factories += (pid -> factory)
  }

  def runAll()(implicit ec: ExecutionContext): Unit = {
    initServers()
    servers.keys.foreach(run)
  }

  private def initServers(): Unit = {
    factories.foreach { case (pid, factory) =>
      val server = factory.generate()
      servers += (pid -> server)
    }
  }

  private def run(pid: Int)(implicit ec: ExecutionContext): Unit = {
    servers(pid).run() onComplete {
      case Success(_) =>
        logger.info("Stopped successfully")
      case Failure(_) =>
        logger.error("Non fatal error occurred")
        logger.error("Restart consumer")
        resetServer(pid)
        run(pid)
    }
  }

  private def resetServer(pid: Int): Unit = {
    servers -= pid
    val server = factories(pid).generate()
    servers += (pid -> server)
  }

  def shutdown(): Unit = {
    logger.info("Shutdown consumers")
    servers.values.foreach(_.onStop())
  }

}

object ConsumerManager {
  def apply(): ConsumerManager = new ConsumerManager()
}