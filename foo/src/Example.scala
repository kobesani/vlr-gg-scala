package foo

import scala.collection.mutable.Map
import scala.io.Source

import net.liftweb.json.{DefaultFormats, parse, JValue}

object ScraperApp {

    def main(args: Array[String]): Unit = {
        println(hello())
        Array.tabulate(args(0).toInt)(n => s"Hello $n").foreach(println)
        val s = new Scraper(
            baseUrl="https://vlr.gg",
            configPath="/home/kobi/dev/example"
        )
        s.parseVlrMatches()
        println("Trying ScalaScraper")
        val scraperCfgFact = new ScraperConfigFactory("./valorant-matches.json")
        val scraperCfg = scraperCfgFact.buildConfig
        scraperCfg.selectors.foreach(println)
        val webScraper: WebScraper = WebScraper("./valorant-matches.json")
        webScraper.request(Map("page" -> "1")).foreach(println)
    }
    def hello(): String = "Hello World"
}
