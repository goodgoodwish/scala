package example

object Main extends App {
  val ages = Seq(42, 75, 188, 99, 129)
  println(s"The oldest person is ${ages.max}")

  val fruits = Array("apple", "banana", "cherry")

  val aTool = new TheTool
  aTool.printAll(fruits: _*)
  println(aTool.reverseString("abc"))
  println(aTool.reverseStringO("012"))

}
