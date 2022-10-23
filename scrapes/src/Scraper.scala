package scrapes

import scala.collection.mutable.Map
import scala.io.Source

import java.text.ParsePosition
import java.time.format.DateTimeFormatter
import java.time.LocalDate

import net.liftweb.json.{DefaultFormats, parse}

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._


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
            case 200 => Some(get(JsoupBrowser().parseString(response.text())))
            case _ => None
        }

        document.getOrElse(List[String]())
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
            case _ => data
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
}
