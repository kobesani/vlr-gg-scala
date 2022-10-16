package foo

import scala.collection.mutable.Map
import scala.io.Source

import net.liftweb.json.{DefaultFormats, parse, JValue}

object Example{
    val MonthMap = Map(
        "January" -> 1,
        "February" -> 2,
        "March" -> 3,
        "April" -> 4,
        "May" -> 5,
        "June" -> 6,
        "July" -> 7,
        "August" -> 8,
        "September" -> 9,
        "October" -> 10,
        "November" -> 11,
        "December" -> 12,
    )

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
