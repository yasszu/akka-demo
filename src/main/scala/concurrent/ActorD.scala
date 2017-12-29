package concurrent

import akka.actor.Props

class ActorD(value: Int) extends BaseActor {

  override def exec(): Int = {
    println(">>>Start " + self.toString)
    println(s"value = $value")
    Thread.sleep(5000)
    println("<<<Finish " + self.toString)
    value * 100
  }

}

object ActorD {
  def props(value: Int): Props = Props(new ActorD(value))
}
