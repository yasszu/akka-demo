package concurrent

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import concurrent.actors.{ActorA, ActorB, ActorC}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

  val futureA: Future[Int] = {
    import actors.ActorA._
    (actorA ? Message).mapTo[Int]
  }

  val futureB: Future[Int] = {
    import actors.ActorB._
    (actorB ? Message).mapTo[Int]
  }

  val futureC: Future[Int] = {
    import actors.ActorC._
    (actorC ? Message).mapTo[Int]
  }

  val future = for {
    a <- futureA
    b <- futureB
    c <- futureC
  } yield (a, b, c)
  // Note that each futures is not running in parallel.

  future.onComplete {
    case Success((a, b, c)) => println(s"Result: $a, $b, $c")
    case Failure(throwable) => throwable.printStackTrace()
  }
}
