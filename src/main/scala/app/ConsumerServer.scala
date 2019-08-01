package app

import akka.actor.ActorSystem
import com.twitter.server.TwitterServer
import com.twitter.util.Await

import scala.concurrent.ExecutionContext

object ConsumerServer extends TwitterServer {

  val system = ActorSystem("kafka-consumer")

  implicit val ec: ExecutionContext = system.dispatcher

  val postConsumer = PostConsumer()

  def main(): Unit = {
    Await.ready(postConsumer.run())
  }

}