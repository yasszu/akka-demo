package example

import akka.actor._

class GreetingActor extends Actor {

  import GreetingActor._

  def receive: PartialFunction[Any, Unit] = {
    case Greeting(from) => greet(from)
    case _ => println("Hello!")
  }

  def greet(from: String): Unit = {
    println(s"Hello, $from!")
  }

}

object GreetingActor {

  case class Greeting(from: String)

}