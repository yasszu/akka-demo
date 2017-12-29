package concurrent.actors

import concurrent.actors.ActorA.Message

object ActorA {

  case class Message()

}

class ActorA extends Ask[Int] {

  override def execute(message: Any): Int = message match {
    case Message =>
      println(">>>Start " + self.toString)
      Thread.sleep(5000)
      println("<<<Finish " + self.toString)
      10
  }

}

