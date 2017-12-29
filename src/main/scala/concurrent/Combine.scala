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

case class Result(a: Int, b: Int)

/**
  * A ~> B ~> merge
  * C ~> merge
  * merge ~> D
  */
object Combine extends App {

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

  val futureAB: Future[Int] = for {
    a <- futureA
    b <- futureB
  } yield a + b

  val result: Future[Result] = futureAB zip futureC map { case (a, b) => Result(a, b) }

  result.onComplete {
    case Success(Result(a, b)) => println(s"Result: $a, $b")
    case Failure(throwable) => throwable.printStackTrace()
  }

}
