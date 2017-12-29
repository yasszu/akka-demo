package concurrent.actors


import akka.actor.Actor

/**
  * Receive a messages and reply result.
  *
  * @tparam T is result of actor
  */
trait Ask[T] extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case message => request(message)
  }

  def request(message: Any): Unit = {
    try {
      sender ! execute(message)
    } catch {
      case e: Exception =>
        sender ! akka.actor.Status.Failure(e)
        throw e
    }
  }

  /**
    * Handle message
    *
    * @return result
    */
  def execute(message: Any): T

}