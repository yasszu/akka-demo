package concurrent

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * A ~> B ~> C
  */
object Composing extends App {

  implicit val timeout: Timeout = Timeout(60 seconds)

  val system = ActorSystem("mySystem")
  val actorA = system.actorOf(Props[ActorA], "actorA")
  val actorB = system.actorOf(Props[ActorB], "actorB")
  val actorC = system.actorOf(Props[ActorC], "actorC")

  val future = for {
    a <- (actorA ? Request).mapTo[Int]
    b <- (actorB ? Request).mapTo[Int]
    c <- (actorC ? Request).mapTo[Int]
  } yield (a, b, c)
  // Note that each futures is not running in parallel.

  future.onComplete {
    case Success((a, b, c)) => println(s"Result: $a, $b, $c")
    case Failure(throwable) => throwable.printStackTrace()
  }
}
