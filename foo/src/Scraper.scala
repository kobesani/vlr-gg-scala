package foo

import scala.jdk.CollectionConverters._
import scala.collection.mutable.Map
import scala.util.matching.Regex

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.format.DateTimeFormatter
import java.time.LocalDate

import net.liftweb.json.{DefaultFormats, parse, JValue}
import scala.io.Source
import java.text.ParsePosition


class Scraper(val baseUrl: String, val configPath: String) {
    override def toString(): String = 
        s"This is a scraper with ${baseUrl} and a configuration at ${configPath}"

    def parseVlrMatches(page: Int = 1) = {
        val datetime = "^[A-z]{3}, ([A-z]+) ([0-9]{1,2}), ([0-9]{4}).*$".r
        val dateInputFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy")

        val doc = Jsoup.connect(s"${baseUrl}/matches/results").get()
        val dates = (
            doc
            .select(".col.mod-1 .wf-label.mod-large")
            .eachText()
            .asScala
            .toList
            .map(matchDateTime(_, datetime))
            .map(LocalDate.parse(_, dateInputFormat))
            .map(_.format(DateTimeFormatter.ISO_DATE))
        )
        dates.foreach(println)

        val cards = (
            doc
            .select(".col.mod-1 .wf-card")
            
        )
    }

    def matchDateTime(str: String, regex: Regex) : String = {
         str match {
            case regex(month, day, year) => s"$month $day, $year"
        }
    }
}


case class Selector(attribute: String, parent: String, query: String)

case class ScraperCfg(base_url: String, selectors: List[Selector])

class ScraperConfigFactory(val configPath: String) {
    override def toString(): String = s"${configPath}"

    def buildConfig : ScraperCfg = {
        implicit val formats = DefaultFormats
        val jsonString = Source.fromFile(configPath).getLines().mkString
        parse(jsonString).extract[ScraperCfg]
    }
}

object Utilities {
    def parse_datetime_to_iso(text: String, format: String, parsePos: Int = 0) : String = {
        val inputFormat = DateTimeFormatter.ofPattern(format)
        val formatParse = inputFormat.parse(text, new ParsePosition(parsePos))
        LocalDate.from(formatParse).format(DateTimeFormatter.ISO_DATE)
    }
}
