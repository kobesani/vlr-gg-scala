package scrapes
import utest._

object ExampleTests extends TestSuite {
  def tests = Tests{
    test("hello"){
      // val result = Example.hello()
      val result = "Hello World"
      assert(result == "Hello World")
      result
    }
  }
}
