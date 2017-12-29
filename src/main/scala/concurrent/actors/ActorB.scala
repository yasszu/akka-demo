package concurrent.actors

import concurrent.actors.ActorB.Message

object ActorB {

  case class Message()

}

class ActorB extends Ask[Int] {

  override def execute(message: Any): Int = message match {
    case Message =>
      println(">>>Start " + self.toString)
      Thread.sleep(5000)
      println("<<<Finish " + self.toString)
      200
  }

}
