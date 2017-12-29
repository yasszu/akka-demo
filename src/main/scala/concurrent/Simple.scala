package concurrent

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import concurrent.actors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Simple extends App {

  implicit val timeout: Timeout = Timeout(60 seconds)

  val system = ActorSystem("mySystem")

  val actorA = system.actorOf(Props[ActorA], "actorA")

  val actorB = system.actorOf(Props[ActorB], "actorB")

  val actorC = system.actorOf(Props[ActorC], "actorC")

  val actorD = system.actorOf(ActorD.props(555), "actorE")

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
  val futureD: Future[Int] = {
    import actors.ActorD._
    (actorD ? Message).mapTo[Int]
  }

  futureA onComplete {
    case Success(i: Int) => println("result: " + i)
    case Failure(throwable) => throwable.printStackTrace()
  }

  futureB onComplete {
    case Success(i: Int) => println("result: " + i)
    case Failure(throwable) => throwable.printStackTrace()
  }

  futureC onComplete {
    case Success(i: Int) => println("result: " + i)
    case Failure(throwable) => throwable.printStackTrace()
  }

  futureD onComplete {
    case Success(i: Int) => println("result: " + i)
    case Failure(throwable) => throwable.printStackTrace()
  }
}
