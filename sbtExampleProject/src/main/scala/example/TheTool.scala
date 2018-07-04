package example

class TheTool {
  def printAll(strings: String*) {
    for (x <- strings) {
      printf("It is a %s \n", x)
    }
  }

  def reverseString(s: String): String = {
    def rev(s: String, revS: String): String = {
      println(s, s(0), revS)
      if (s.length <= 1) { s(0) + revS }
      else rev(s.substring(1), s(0) + revS)
    }
    rev(s, "")
  }
  def reverseStringO(s: String): String = {
    if (s.length <= 1) { s.substring(0,1) }
    else {
      println(s, s(0))
      reverseStringO(s.substring(1)) + s(0)
    }
  }
}
