package app

import com.twitter.server.TwitterServer

object Main extends TwitterServer with ConsumerModule {

  run()

}