package scrapes

import scala.collection.mutable.Map
import java.sql.{Connection, DriverManager, ResultSet}
import org.postgresql.Driver

object ScraperApp {

    def main(args: Array[String]): Unit = {
        // default to page 1 if not given as first argument
        val page = args.lift(0).getOrElse("1")
        val webScraper: WebScraper = WebScraper("./valorant-matches.json")
        val scrape = webScraper.request(Map("page" -> args.lift(0).getOrElse("1")))
        scrape foreach println

        val con_st = "jdbc:postgresql://localhost:5432/kobi?user=kobi&password=melampodium00"
        classOf[Driver]
        val conn = DriverManager.getConnection(con_st)
        val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        val rs = statement.executeQuery("SELECT * FROM accounts;")
        while (rs.next) {
            println(rs.getString("email"))
        }
    }
}
