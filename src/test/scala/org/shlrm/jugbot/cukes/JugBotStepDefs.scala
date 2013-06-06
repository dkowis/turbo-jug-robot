package org.shlrm.jugbot.cukes

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.PendingException
import dispatch._, Defaults._
import scala.slick.driver.H2Driver
import org.shlrm.jugbot.slick.{Meeting, DAL}
import scala.slick.session.Database
import java.sql.Date
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.ShouldMatchersForJUnit

class JugBotStepDefs extends ScalaDsl with EN with ShouldMatchersForJUnit {
  val server = "http://localhost:8080"
  val service = host("localhost", 8080)

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
  Given( """^the default meeting exists$""") {
    () => {
      //Put a default meeting into the database
      val dal = new DAL(H2Driver)
      val db = Database.forURL("jdbc:h2:mem:jugbot1", driver = "org.h2.Driver") //TODO: this might have to be a file backed db
      import dal.profile.simple._
      db withSession {
        implicit session: Session =>
        //TODO: need to do flyway or something to make sure this exists first
          dal.create

          import dal._
          Meetings.insert(Meeting("TEST MEETING", Date.valueOf("2013-02-17")))
      }
    }
  }
  When( """^I GET to "([^"]*)"$""") {
    (path: String) => {
      //Yay this works
      //Now lets do some database stuff
      def builtRequest = (service / path).GET
      val request = Http(builtRequest)
      val response = request()
      response.getStatusCode() should be(200)

      //TODO: make a request against the right stuff?
    }
  }
  Then( """^I receive the JSON:$""") {
    (rawJson: String) =>
    //TODO: match up the output of the call with the raw JSON
      throw new PendingException()
  }
}
