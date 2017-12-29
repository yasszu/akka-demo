package concurrent

class ActorA extends BaseActor {

  override def exec(): Int = {
    println(">>>Start " + self.toString)
    Thread.sleep(5000)
    println("<<<Finish " + self.toString)
    10
  }

}
