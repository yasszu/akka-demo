package app

import com.twitter.server.TwitterServer

object ConsumerServer extends TwitterServer {

  val postConsumer = PostConsumer()

  def main(): Unit = {
    postConsumer.run()
  }

}