package greet

import akka.actor._
import greet.GreetingActor.Greeting

object Greet extends App {
  val system = ActorSystem("greeting-system")
  val myActor = system.actorOf(Props[GreetingActor])
  myActor ! Greeting("Robert")
  myActor ! Greeting("Paul")
  myActor ! Greeting("Joe")
}
