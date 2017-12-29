package concurrent.actors

import akka.actor.Props
import concurrent.actors.ActorD.Message

object ActorD {

  case class Message()

  def props(value: Int): Props = Props(new ActorD(value))

}

class ActorD(value: Int) extends Ask[Int] {

  override def execute(message: Any): Int = message match {
    case Message =>
      println(">>>Start " + self.toString)
      println(s"value = $value")
      Thread.sleep(5000)
      println("<<<Finish " + self.toString)
      value * 100
  }

}

