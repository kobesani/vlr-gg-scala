import org.apache.xpath.operations.Bool
import net.sourceforge.htmlunit.corejs.javascript.json.JsonParser
import $ivy.`com.lihaoyi::scalatags:0.7.0`
import $ivy.`com.lihaoyi::requests:0.7.1`
import $ivy.`net.ruippeixotog::scala-scraper:3.0.0`
import $ivy.`com.github.nscala-time::nscala-time:2.32.0`
import scala.util.matching.Regex

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import java.time.format.DateTimeFormatter
import java.time.LocalDate


val url = "https://www.vlr.gg/matches/results/?page=4"
val resp: requests.Response = requests.get(url)

val browser = JsoupBrowser()
val doc = browser.parseString(resp.text())

val cards = doc >> elementList(".col.mod-1 .wf-card")
val dates = doc >> elementList(".col.mod-1 .wf-label.mod-large") >> allText


val datetime = "^([A-z]{3}), ([A-z]+) ([0-9]{1,2}), ([0-9]{4}).*$".r
val dateInputFormat = DateTimeFormatter.ofPattern("EEE, MMMM d, yyyy")

def matchDateTime(str: String) : String = {
     str match {
        case datetime(day_of_week, month, day, year) => s"$day_of_week, $month $day, $year"
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

val fizzbuzz = for (i <- Range.inclusive(1, 100)) yield {
    if (i % 3 == 0 && i % 5 == 0) "FizzBuzz"
    else if (i % 3 == 0) "Fizz"
    else if (i % 5 == 0) "Buzz"
    else i.toString
}

def flexFizzBuzz(f: String => Unit) : Unit = {
    val fizzbuzz = for (i <- Range.inclusive(1, 100)) yield {
        if (i % 3 == 0 && i % 5 == 0) f("FizzBuzz")
        else if (i % 3 == 0) f("Fizz")
        else if (i % 5 == 0) f("Buzz")
        else f(i.toString)
    }
}

flexFizzBuzz(s => {})
flexFizzBuzz(s => println(s))

var i = 0
val output = new Array[String](100)

flexFizzBuzz(
    {
        s => output(i) = s
        i+=1
    }
)

class Msg(val id: Int, val parent: Option[Int], val text: String)

val test = new Msg(1, None, "test")
