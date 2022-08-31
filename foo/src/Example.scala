package foo

import scala.jdk.CollectionConverters._
import scala.collection.mutable.Map
import scala.util.matching.Regex

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.format.DateTimeFormatter
import java.time.LocalDate

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
        val s = new Scraper(baseUrl="https://vlr.gg", configPath="/home/kobi/dev/example")
        println(s)
        val resp: requests.Response = requests.get(s.baseUrl)
        println(s"URL: ${s.baseUrl} - [${resp.statusCode}] ${resp.statusMessage}")
        s.parseVlrMatches()
    }
    def hello(): String = "Hello World"
}

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
    }

    def matchDateTime(str: String, regex: Regex) : String = {
         str match {
            case regex(month, day, year) => s"$month $day, $year"
        }
    }
}
