package org.shlrm.jugbot.cukes

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.{DataTable, PendingException}
import dispatch._, Defaults._
import scala.slick.driver.H2Driver
import org.shlrm.jugbot.slick.{Meeting, DAL}
import scala.slick.session.Database
import java.sql.Date
import org.scalatest.junit.ShouldMatchersForJUnit
import com.typesafe.config.ConfigFactory
import com.ning.http.client.Response

class JugBotStepDefs extends ScalaDsl with EN with ShouldMatchersForJUnit {

  import spray.json._
  import DefaultJsonProtocol._

  val server = "http://localhost:8080"
  val service = host("localhost", 8080)

  val config = ConfigFactory.load().getConfig("integrationTest")

  val dal = new DAL(H2Driver)
  val db = Database.forURL(config.getString("db.url"),
    user = config.getString("db.user"),
    password = config.getString("db.pass"),
    driver = config.getString("db.driver"))


  //OH NOES MUTABLE!
  var insertedMeeting: Meeting = null
  var response: Response = null

  /**
   * I'm doing this part wrong, I'm not sure how to create a function to wrap stuff the way I want.
   * @param logic
   */
  def database(logic: => Unit) = {
    //Put a default meeting into the database
    val dal = new DAL(H2Driver)
    val db = Database.forURL(config.getString("db.url"),
      user = config.getString("db.user"),
      password = config.getString("db.pass"),
      driver = config.getString("db.driver"))
    import dal.profile.simple._
    db withSession {
      implicit session: Session =>
        logic
    }
  }

  When( """^I POST the JSON to "([^"]*)":$""") {
    (path: String, rawJson: String) =>
      def builtRequest = service.setUrl(server + path).
        setBody(rawJson).POST
      val request = Http(builtRequest)
      response = request()
  }
  Then( """^the result should be (\d+) $""") {
    (arg0: Int) => {
    }
  }

  Then( """^the response status is (\d+) "([^"]*)"$""") {
    (code: Int, codeText: String) =>
      response.getStatusCode should be(code)
  }

  Then( """^the backend contains a? ?meetings?:$""") {
    (dt: DataTable) => {
      import scala.collection.JavaConversions._
      val tableMap = dt.asMaps().toList
      import dal.profile.simple._
      db withSession {
        implicit session: Session =>
          import dal._
          val meetings = Query(Meetings).list.toList
          meetings.length should be(tableMap.length)
          tableMap.map(map => {
            //Each row is a map: Date and Title
            val title = map("Title")
            val date = Date.valueOf(map("Date"))

            //Ensure we have only one of this combination in the list
            meetings.filter(m => {
              m.title == title && m.date == date
            }).length should be(1)
          })

      }
    }
  }


  Given( """^the database is empty$""") {
    () => {
      import dal.profile.simple._
      db withSession {
        implicit session: Session =>
          import dal._
          //Delete ALL! ERMAGHERDS
          Query(Meetings).delete
          Query(SurveyResults).delete
      }
    }
  }

  Given( """^the default meeting exists$""") {
    () => {
      //Put a default meeting into the database
      import dal.profile.simple._
      db withSession {
        implicit session: Session =>
          import dal._
          insertedMeeting = Meetings.insert(Meeting("test meeting", Date.valueOf("2013-02-17")))

      }
    }
  }
  When( """^I GET to "([^"]*)"$""") {
    (path: String) => {
      //Yay this works
      //Now lets do some database stuff
      def builtRequest = service.setUrl(server + path).
        setHeader("accept", "application/json").
        GET

      val request = Http(builtRequest)
      response = request()
    }
  }
  Then( """^I recieve a list containing my meeting:$""") {
    (rawJson: String) => {
      //Mutilate the rawJson, to replace magic stuff
      val parsed = rawJson.replaceAll("\\$meeting\\.id", insertedMeeting.id.get.toString)
      response.getContentType should startWith("application/json") //Good enough!
      //TODO: match up the output of the call with the raw JSON
      val requiredJson = parsed.asJson
      val receivedJson = response.getResponseBody.asJson
      receivedJson should be(requiredJson)
    }
  }

}
