package concurrent

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, _}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Parallel extends App {

  implicit val timeout: Timeout = Timeout(60 seconds)
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()


  lazy val futureA = (x: Int) => Future {
    println(">>>Start futureA")
    Thread.sleep(5000)
    println("<<<Finish futureA")
    x * 2
  }

  lazy val futureB = (x: Int) => Future {
    println(">>>Start futureB")
    Thread.sleep(5000)
    println("<<<Finish futureB")
    x * x
  }

  lazy val futureC = (x: Int) => Future {
    println(">>>Start futureC")
    Thread.sleep(5000)
    println("<<<Finish futureC")
    x * 10
  }

  lazy val sum = (a: Int, b: Int) => {
    println(">>>Start out")
    Thread.sleep(5000)
    println("<<<Finish out")
    println(s"a = $a, b = $b")
    a + b
  }

  val graph = (ids: List[Int]) => Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    // prepare graph elements
    val in = Source(ids)
    val fa = Flow[Int].mapAsync[Int](3)(x => futureA(x))
    val fb = Flow[Int].mapAsync[Int](3)(x => futureB(x))
    val fc = Flow[Int].mapAsync[Int](3)(x => futureC(x))
    val zip = b.add(Zip[Int, Int]())

    // connect the graph
    in ~> fa ~> fb ~> zip.in0
    in ~> fc ~> zip.in1

    // expose port
    SourceShape(zip.out)
  })

  val sink: Sink[(Int, Int), Future[Int]] = Sink.fold(0) { (_, n) => sum(n._1, n._2) }

  // run
  graph(List(1, 2, 3)).runWith(sink) onComplete {
    case Success(r) => println(s"[Result] $r ###")
    case Failure(failure) => failure.printStackTrace()
  }
}