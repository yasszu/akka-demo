package app

import scala.io.StdIn

object Main extends App {

  val consumer = ConsumerImpl()
  consumer.run()

//  try {
//    // Start a kafka consumer
//    consumer.run()
//
//    println(">>> Input any words <<<")
//    while (true) {
//      val line = StdIn.readLine()
//      println(s"Hello, $line!\n")
//    }
//  } catch {
//    case e: Throwable => e.printStackTrace()
//  }

}