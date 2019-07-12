package app

import scala.io.StdIn

object Main extends App {

   try {
     println(">>> Input any words <<<")
     while (true) {
       val line = StdIn.readLine()
       println(s"Hello, $line!\n")
     }
   } catch {
     case e: Throwable => e.printStackTrace()
   }

}