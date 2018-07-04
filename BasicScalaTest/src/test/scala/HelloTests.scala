package com.yi.testproject

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class HelloTests extends FunSuite with BeforeAndAfter {

  before {
    // setup work,
    val p1 = new Person("She")
  }

  test("the name is set correctlyin constructor") {
    val p = Person("Ma Lan")
    assert(p.name == "Ma Lan")
    assert(p.atAddr == "Xi An")
  }

  test("a Person name can be changes") {
    val p = Person("Zhang Ge")
    p.name = "Zhang Ge Canada"
    assert(p.name == "Zhang Ge Canada")
  }

  // mark that you want a test here in the future,
  test("test person knowledge") (pending)

  after {
    val p1: Person = Person("")
  }
}
