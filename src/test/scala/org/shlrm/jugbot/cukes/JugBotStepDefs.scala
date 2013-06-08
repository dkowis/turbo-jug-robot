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
  var body = ""
  var insertedMeeting: Meeting = null

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
    //TODO: need to split up the path!
    //Make a POST to the path, sending the JSON
      throw new PendingException()
  }
  Then( """^the result should be (\d+) OK$""") {
    (arg0: Int) =>
    //TODO: check the result of the call
      throw new PendingException()
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
      def builtRequest = service.setUrl(server + path).GET

      val request = Http(builtRequest)
      val response = request()
      response.getStatusCode() should be(200)
      body = response.getResponseBody

    }
  }
  Then( """^I recieve a list containing my meeting:$""") {
    (rawJson: String) => {
      //Mutilate the rawJson, to replace magic stuff
      val parsed = rawJson.replaceAll("\\$meeting\\.id", insertedMeeting.id.get.toString)
      //TODO: match up the output of the call with the raw JSON
      val requiredJson = parsed.asJson
      val receivedJson = body.asJson
      receivedJson should be(requiredJson)
    }
  }

}
