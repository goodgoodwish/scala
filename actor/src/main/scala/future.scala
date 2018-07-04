// Future, demo,

package actors.night
// package actors

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Futures1 extends App { 
  // def sleep(time: Long) { Thread.sleep(time) }

  // used by 'time' method
  implicit val baseTime = System.currentTimeMillis
      // 2 - create a Future
  val f = Future { 
    day.sleep(500) 
    1+1
  }
      // 3 - this is blocking (blocking is bad)
  val result = Await.result(f, 1 second) 
  println(result)
  
  // for any package ref combination,
  actors.Time.sleep2(1000)
  actors.night.sleepN(500)
  // for package actors
  // Time.sleep2(1000)
  // night.sleepN(500)
  // for package actors.night
  actors.Time.sleep2(1000)
  sleepN(500)
}

package object day {
  def sleep(time: Long) { 
    println("day, " + time)
    Thread.sleep(time)
  }
}
