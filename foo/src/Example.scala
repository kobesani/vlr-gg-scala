package foo

import scala.collection.mutable.Map

object ScraperApp {

    def main(args: Array[String]): Unit = {
        // default to page 1 if not given as first argument
        val page = args.lift(0).getOrElse("1")
        val webScraper: WebScraper = WebScraper("./valorant-matches.json")
        val scrape = webScraper.request(Map("page" -> args.lift(0).getOrElse("1")))
        scrape foreach println
    }
}
