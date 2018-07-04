package actors

object Time {

  def sleep2(time: Long) { 
    println("Time.sleep2, " + time)
    Thread.sleep(time)
  }

}

package object night {
  def sleepN(time: Long) { 
    println("sleep at night, " + time)
    Thread.sleep(time)
  }
}