package app

import akka.actor.ActorSystem
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main extends App {
  self =>

  val logger: Logger = LoggerFactory.getLogger(self.getClass)

  implicit val system: ActorSystem = ActorSystem("kafka")

  implicit val ec: ExecutionContext = system.dispatcher

  val postConsumerServer = PostConsumerServer()

  val postProducerServer = PostProducerServer()

  // Start a producer
  postProducerServer.run()

  // Start a consumer
  postConsumerServer.run() onComplete {
    case Success(done) =>
      logger.info("Stopped successfully")
    case Failure(_) =>
      logger.error("Unexpected error occurred")
      system.terminate()
  }

}