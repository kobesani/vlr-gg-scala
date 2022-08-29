package foo

object Example{
  def main(args: Array[String]): Unit = {
    println(hello())
    Array.tabulate(args(0).toInt)(n => s"Hello $n").foreach(println)
  }
  def hello(): String = "Hello World"
}