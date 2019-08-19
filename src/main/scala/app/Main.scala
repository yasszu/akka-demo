package app

import akka.actor.ActorSystem
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext

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
  val consumerManger = ConsumerManager()
  consumerManger.addConsumer(postConsumerServer)
  consumerManger.runAll()

  // Stop the consumer when the VM exits
  sys.addShutdownHook {
    logger.info("Stopping consumer...")
    consumerManger.shutdown()
  }

}