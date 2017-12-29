package concurrent.actors

import concurrent.actors.ActorC.Message

object ActorC {

  case class Message()

}

class ActorC extends Ask[Int] {

  override def execute(message: Any): Int = message match {
    case Message =>
      println(">>>Start " + self.toString)
      Thread.sleep(5000)
      println("<<<Finish " + self.toString)
      3000
  }

}
