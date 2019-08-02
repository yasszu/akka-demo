package app

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

object Main extends App {

  val system = ActorSystem("kafka-consumer")

  implicit val ec: ExecutionContext = system.dispatcher

  val postConsumerServer = PostConsumerServer()

  val postProducerServer = PostProducerServer()

  postConsumerServer.run()

  postProducerServer.run()

}