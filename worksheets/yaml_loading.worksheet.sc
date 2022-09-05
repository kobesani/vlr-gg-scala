// import scala.beans.BeanProperty
// import $ivy.`org.yaml:snakeyaml:1.23`
import $ivy.`net.liftweb::lift-json:3.4.3`
// import scala.jdk.CollectionConverters._

import scala.io.Source
// import java.io.{File, FileInputStream}
// import scala.collection.Map
import net.liftweb.json.{DefaultFormats, parse}

// import org.yaml.snakeyaml.Yaml
// // val f: String = Source.fromFile("./valorant-matches.yml").getLines().mkString

// val fis = new FileInputStream(new File("./valorant-matches.yml"))
// val x = new Yaml()
// val parsed_yaml = x.load(fis).asInstanceOf[java.util.Map[String, Any]]
// // val parsed_yaml = x.load(fis).asInstanceOf[Map[String, ]

// val test = parsed_yaml.get("selectors").asInstanceOf[java.util.List[java.util.LinkedHashMap[String, String]]].asScala.toList

// // test.get(1)

// parsed_yaml.get("base_url").toString()
// parsed_yaml.get("selectors")

// class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }

// object M extends CC[List[Map[String, String]]]

// val y = parsed_yaml.get("selectors")

case class Selector(attribute: String, parent: String, query: String)

case class ScraperCfg(base_url: String, selectors: List[Selector])

// // def toElement(obj: Option[Any]) : Option[Selector] = {
// //     obj match {
// //         case Some(e) => Some(Selector())
// //     }
// // }

// parsed_yaml.get("selectors")

// // test.get(0).get("attribute")
// test(0)

// val f: String = Source.fromFile("./valorant-matches.json").getLines().mkString
implicit val formats = DefaultFormats
// val json = parse(f)
// // val cfg = json.extract[ScraperCfg]

case class Child(name: String, age: Int, birthdate: Option[java.util.Date])
case class Address(street: String, city: String)
case class Person(name: String, address: Address, children: List[Child])

val json_2 = parse("""
         { "name": "joe",
           "address": {
             "street": "Bulevard",
             "city": "Helsinki"
           },
           "children": [
             {
               "name": "Mary",
               "age": 5
               "birthdate": "2004-09-04T18:06:22Z"
             },
             {
               "name": "Mazy",
               "age": 3
             }
           ]
         }
""")


json_2.extract[Person]