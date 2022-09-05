import $ivy.`net.liftweb::lift-json:3.4.3`
import net.liftweb.json.{DefaultFormats, parse}

import scala.language.dynamics
import scala.reflect.runtime.universe

import scala.io.Source

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.text.ParsePosition

implicit val formats = DefaultFormats

val regex = "^[A-z]{3}, ([A-z]+) ([0-9]{1,2}), ([0-9]{4}).*$".r
val datestring = "Fri, August 8, 2022"

val patternMatch = regex.findAllIn(datestring)
println(s"key: ${patternMatch.group(1)} value: ${patternMatch.group(2)}")


val anotherDS = "Fri, September 2, 2022 Yesterday"


val dateInputFormatX = DateTimeFormatter.ofPattern("E, MMMM d, yyyy")
val testFormatter = dateInputFormatX.parse(anotherDS, new ParsePosition(0))
// dateInputFormatX.parse(anotherDS)
// LocalDate.parse(anotherDS, dateInputFormatX)

LocalDate.from(testFormatter).format(DateTimeFormatter.ISO_DATE)