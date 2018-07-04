package otherscope {
  class Foo {
    // a method that takes a function and a string, and passes the string into
    // the function, and then executes the function
    def printResult(f:(Int) => Boolean, x: Int) {
      println(f(x))
    }
  }
}

object ClosureExample extends App {
  var votingAge = 18
  val isOfVotingAge = (age: Int) => {
    println(s"age $age, votingAge $votingAge")
    age >= votingAge
  }

  val foo = new otherscope.Foo
  foo.printResult(isOfVotingAge, 17)
  foo.printResult(isOfVotingAge, 19)

  votingAge = 20
  foo.printResult(isOfVotingAge, 17)
  foo.printResult(isOfVotingAge, 19)

}
