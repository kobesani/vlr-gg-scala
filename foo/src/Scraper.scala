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

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import org.jsoup.nodes.DocumentType


case class Transformer(function: String, argument: String)


case class ScraperSelector(
    attribute: String,
    parent: String,
    query: String,
    transformers: List[Transformer]
)


case class ScraperConfig(base_url: String, selectors: List[ScraperSelector])


class WebScraper(val configuration: ScraperConfig) {
    def request(params: Map[String, String]): List[String] = {
        val response = requests.get(url = configuration.base_url, params = params)

        val document = response.statusCode match {
            case 200 => Some(JsoupBrowser().parseString(response.text()))
            case _ => None
        }
        document.map(get(_)).getOrElse(List[String]())
    }

    def get(document: Browser#DocumentType): List[String] = {
        configuration
            .selectors
            .map(s => applySelector(document, selector = s))
            .flatten
    }

    def applySelector(
        document: Browser#DocumentType,
        selector: ScraperSelector
    ): List[String] = {
        (document >> elementList(selector.query) >> allText) map {
            res => transform(res, selector.transformers)
        }
    }

    def transform(data: String, transformers: List[Transformer]): String = {
        var transformedData: String = data

        transformers foreach {
            t => transformedData = ScraperUtilities.apply(
                transformedData, t.function, t.argument
            )
        }
        transformedData
    }
}


object WebScraper {
    def apply(configPath: String): WebScraper = {
        implicit val formats = DefaultFormats
        val jsonString = Source.fromFile(configPath).getLines().mkString
        val config = parse(jsonString).extract[ScraperConfig]
        new WebScraper(config)
    } 
}


object ScraperUtilities {
    def apply(data: String, method: String, argument: String): String = {
        method match {
            case "isoDate" => isoDate(data, argument)
            case "isoWeekDate" => isoWeekDate(data, argument)
            case "doNothing" => doNothing(data, "")
            case _ => doNothing(data, "")
        }
    }

    def isoDate(text: String, inputFormat: String): String = {
        LocalDate
            .from(
                DateTimeFormatter
                .ofPattern(inputFormat)
                .parse(text, new ParsePosition(0))
            )
            .format(DateTimeFormatter.ISO_DATE)
    }

    def isoWeekDate(text: String, inputFormat: String): String = {
        LocalDate
            .from(
                DateTimeFormatter
                .ofPattern(inputFormat)
                .parse(text, new ParsePosition(0))
            )
            .format(DateTimeFormatter.ISO_WEEK_DATE)
    }

    def doNothing(text: String, dummy: String): String = {
        return text
    }
}


class Scraper(val baseUrl: String, val configPath: String) {
    override def toString(): String = 
        s"This is a scraper with ${baseUrl} and a configuration at ${configPath}"

    // def request(): Document = {
    //     val response = requests.get(s"${baseUrl}")
    //     val statusCode = response.statusCode
    //     // val browser = JsoupBrowser()
    //     val browser = Jsoup.parse(response.text)
    //     // val x = browser.parseString(response.text)
    //     val doc = statusCode match {
    //         case 200 => (
    //             Some(JsoupBrowser().parseString(response.text()))
    //         )
    //         case _ => None
    //     }
    //     browser.

    //     return doc
    // }

    def request(params: Map[String, String]): Option[String] = {
        val response = requests.get(url = baseUrl, params = params)
        response.statusCode match {
            case 200 => Some(response.text())
            case _ => None
        }
    }

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
