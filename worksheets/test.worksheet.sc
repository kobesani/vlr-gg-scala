import $ivy.`com.lihaoyi::requests:0.7.1`
import $ivy.`net.ruippeixotog::scala-scraper:3.0.0`
import scala.util.matching.Regex

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.text.ParsePosition


val url = "https://www.vlr.gg/matches/results/?page=1"
val resp: requests.Response = requests.get(url)

val browser = JsoupBrowser()
val doc = browser.parseString(resp.text())

val cards = doc >> elementList(".col.mod-1 .wf-card")
val dates = doc >> elementList(".col.mod-1 .wf-label.mod-large") >> allText


val datetime = "^[A-z]{3}, ([A-z]+) ([0-9]{1,2}), ([0-9]{4}).*$".r
val dateInputFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy")

def matchDateTime(str: String) : String = {
     str match {
        case datetime(month, day, year) => s"$month $day, $year"
    }
}

val startPageNum: Int = 1
val maxPageRange: Int = 2

val pages: Range = startPageNum to maxPageRange

def getDatesFromPages(
    pageStart: Int,
    pageEnd: Int,
    inclusive: Boolean = true
) : IndexedSeq[String] = {

    return (
        (if (inclusive) pageStart to pageEnd else pageStart until pageEnd)
        .map(page => requests.get(s"https://www.vlr.gg/matches/results/?page=$page"))
        .map(response => JsoupBrowser().parseString(response.text()))
        .map(_ >> elementList(".col.mod-1 .wf-label.mod-large") >> allText)
        .flatten
        .map(matchDateTime)
        .map(LocalDate.parse(_, dateInputFormat))
        .map(_.format(DateTimeFormatter.ISO_DATE))
    )
}

def getCardsFromPages(
    pageStart: Int,
    pageEnd: Int,
    inclusive: Boolean = true
) : IndexedSeq[List[net.ruippeixotog.scalascraper.model.Element]] = {
    return (
        (if (inclusive) pageStart to pageEnd else pageStart until pageEnd)
        .map(page => requests.get(s"https://www.vlr.gg/matches/results/?page=$page"))
        .map(response => JsoupBrowser().parseString(response.text()))
        .map(_ >> elementList(".col.mod-1 .wf-card"))
    )
}

val cards_results = getCardsFromPages(
    startPageNum, maxPageRange, inclusive = true
)


val results: IndexedSeq[String] = getDatesFromPages(
    startPageNum, maxPageRange, inclusive = true
)
results.foreach(println)

