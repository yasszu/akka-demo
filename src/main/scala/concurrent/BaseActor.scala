package concurrent

import akka.actor.Actor

case class Request(value: Int = 0)

trait BaseActor extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case Request => request()
  }

  def request(): Unit = {
    try {
      sender() ! exec()
    } catch {
      case e: Exception =>
        sender() ! akka.actor.Status.Failure(e)
        throw e
    }
  }

  def exec(): Any

}