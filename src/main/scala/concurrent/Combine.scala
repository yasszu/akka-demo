package concurrent

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

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
object ClientCombine extends App {

  implicit val timeout: Timeout = Timeout(60 seconds)
  val system = ActorSystem("mySystem")

  val actorA = system.actorOf(Props[ActorA], "actorA")
  val actorB = system.actorOf(Props[ActorB], "actorB")
  val actorC = system.actorOf(Props[ActorC], "actorC")

  val futureAB: Future[Int] = for {
    a <- (actorA ? Request).mapTo[Int]
    b <- (actorB ? Request).mapTo[Int]
  } yield a + b

  val futureC: Future[Int] = (actorC ? Request).mapTo[Int]

  val result: Future[Result] = futureAB zip futureC map { case (a, b) => Result(a, b) }

  result.onComplete {
    case Success(Result(a, b)) => println(s"Result: $a, $b")
    case Failure(throwable) => throwable.printStackTrace()
  }

}
