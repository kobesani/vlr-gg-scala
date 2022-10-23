// build.sc
import mill._, scalalib._

object scrapes extends ScalaModule{
  def scalaVersion = "2.13.8"
  def ivyDeps = Agg(
    ivy"com.lihaoyi::scalatags:0.7.0",
    ivy"com.lihaoyi::requests:0.7.1",
    ivy"net.ruippeixotog::scala-scraper:3.0.0",
    ivy"org.jsoup:jsoup:1.12.1",
    ivy"org.yaml:snakeyaml:1.23",
    ivy"net.liftweb::lift-json:3.4.3"
  )
  object test extends Tests{
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.7.4",
    )
    def testFrameworks = Seq("utest.runner.Framework")
  }
}
