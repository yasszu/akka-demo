package greet

import akka.actor._
import greet.GreetingActor.Greeting
import scala.io.StdIn

object Greet extends App {

  val system = ActorSystem("greeting-system")
  val myActor = system.actorOf(Props[GreetingActor])
  
   try {
     println(">>> Input any words <<<")
     while(true) {
       val line = StdIn.readLine()
       myActor ! Greeting(line)
     }
     } finally {
       system.terminate()
    }

}