package com.yi.testproject

object Hello extends App {
  val p = Person("Charlie")
  println("Hello from " + p.name + " at " + p.atAddr)
}

case class Person(var name:String) {
  val atAddr:String = "Xi An"
}
