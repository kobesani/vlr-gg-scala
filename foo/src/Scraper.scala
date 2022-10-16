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
import requests.{Request, Response}
// import requests.Response
// import net.ruippeixotog.scalascraper.browser.JsoupBrowser
// import net.ruippeixotog.scalascraper.browser.Browser
// import net.ruippeixotog.scalascraper

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import org.jsoup.nodes.DocumentType

case class DateTime(
    text: String, inputFormat: String, outputFormat: String, parsePosition: Int
)
case class Transformer(function: String, argument: String)
case class ScraperSelector(
    attribute: String, parent: String, query: String, datetime: DateTime, transformers: List[Transformer]
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
            .map(s => document >> elementList(s.query) >> allText)
            .flatten
    }

    def applySelector(
        document: Browser#DocumentType,
        selector: Selector
    ): List[String] = {
        val initialResults = (document >> elementList(selector.query) >> allText)
        return List[String]()
    }

    // def transform(data: String, transformers: List[Transformer]): List[String] = {
    //     val transformFunctions = transformers.map(t => ScraperUtilities.getFunction(t.function))
    // }
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
            case "iso_date" => iso_date(data, argument)
        }
    }

    // def getFunction(method: String): String => String = {
    //     method match {
    //         case "iso_date" => iso_date
    //         case _ => do_nothing
    //     }
    // }

    // def getFunctions(methods: List[String]): List[Function] = {
    //     methods.map(m => getFunction(m))
    // }

    def iso_date(text: String, inputFormat: String): String = {
        LocalDate
            .from(
                DateTimeFormatter
                .ofPattern(inputFormat)
                .parse(text, new ParsePosition(0))
            )
            .format(DateTimeFormatter.ISO_DATE)
    }

    def do_nothing(text: String, dummy: String): String = {
        return text
    }
}
    // def apply(method: String, args: String*): String = {
    //     method match {
    //         case "parse_datetime_to_iso" => "SKDJskljLSKD"
    //         case _ => "dsjhfkdjhfsd"
    //     }
    // }

//     def parse_datetime_to_iso(text: String, format: String, parsePos: Int = 0) : String = {
//         val inputFormat = DateTimeFormatter.ofPattern(format)
//         val formatParse = inputFormat.parse(text, new ParsePosition(parsePos))
//         LocalDate.from(formatParse).format(DateTimeFormatter.ISO_DATE)
//     }
// }


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
