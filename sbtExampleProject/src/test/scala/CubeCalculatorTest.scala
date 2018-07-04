//import example.CubeCalculator
import org.scalatest.FunSuite

class CubeCalculatorTest extends FunSuite {
  test("CubeCalculator.cube") {
    assert(CubeCalculator.cube(2) === 8)
  }
  test("CubeCalculator.cube2") {
    assert(CubeCalculator.cube(0) === 0)
  }

}
