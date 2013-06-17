package org.shlrm.jugbot.cukes

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.{DataTable, PendingException}
import dispatch._, Defaults._
import scala.slick.driver.H2Driver
import org.shlrm.jugbot.slick.{SurveyResponse, Meeting, DAL}
import scala.slick.session.Database
import java.sql.Date
import org.scalatest.junit.ShouldMatchersForJUnit
import com.typesafe.config.ConfigFactory
import com.ning.http.client.Response

class JugBotStepDefs extends ScalaDsl with EN with ShouldMatchersForJUnit {

  import spray.json._

  val server = "http://localhost:8080"
  val service = host("localhost", 8080)

  val config = ConfigFactory.load().getConfig("integrationTest")

  val dal = new DAL(H2Driver)
  val db = Database.forURL(config.getString("db.url"),
    user = config.getString("db.user"),
    password = config.getString("db.pass"),
    driver = config.getString("db.driver"))


  //OH NOES MUTABLE!
  var defaultMeeting: Meeting = null
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

  def postDefaultSurveyResults(a1: Int, a2: Int) = {
    val payload =
      s"""
        |{
        | "q1": ${a1},
        | "q2": ${a2}
        |}
      """.stripMargin
    def builtRequest = url(server + "/meetings/" + defaultMeeting.id.get.toString + "/survey").
      setHeader("content-type", "application/json").
      setBody(payload).
      POST

    val request = Http(builtRequest)
    response = request()
  }

  When( """^I POST the JSON to "([^"]*)":$""") {
    (path: String, rawJson: String) =>
      def builtRequest = url(server + path).
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
            meetings.count(m => {
              m.title == title && m.date == date
            }) should be(1)
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
          Query(SurveyResponses).delete
          Query(Meetings).delete
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
          defaultMeeting = Meetings.insert(Meeting("test meeting", Date.valueOf("2013-02-17")))
      }
    }
  }
  When( """^I GET to "([^"]*)"$""") {
    (path: String) => {
      //Yay this works
      //Now lets do some database stuff
      def builtRequest = url(server + path).
        setHeader("accept", "application/json").
        GET

      val request = Http(builtRequest)
      response = request()
    }
  }
  Then( """^I recieve a list containing my meeting:$""") {
    (rawJson: String) => {
      //Mutilate the rawJson, to replace magic stuff
      val parsed = rawJson.replaceAll("\\$meeting\\.id", defaultMeeting.id.get.toString)
      response.getContentType should startWith("application/json") //Good enough!
      //TODO: match up the output of the call with the raw JSON
      val requiredJson = parsed.asJson
      val receivedJson = response.getResponseBody.asJson
      receivedJson should be(requiredJson)
    }
  }

  When( """^I GET to the default meeting's ID$""") {
    () =>
    //TODO: probably no way to call a step from another step, like I used to
    //Sweet jesus, dispatch is painful to use...
      def builtRequest = url(server + "/meetings/" + defaultMeeting.id.get.toString)
      val request = Http(builtRequest)
      response = request()
      response.getStatusCode should be(200)
  }
  Then( """^I receive a JSON representation of the meeting:$""") {
    (arg0: String) =>
      val parsed = arg0.replaceAll("\\$meeting\\.id", defaultMeeting.id.get.toString)
      val requiredJson = parsed.asJson
      val receivedJson = response.getResponseBody.asJson

      receivedJson should be(requiredJson)
  }

  When( """^I POST to the default meeting's ID's survey:$""") {
    (payload: String) =>
      def builtRequest = url(server + "/meetings/" + defaultMeeting.id.get.toString + "/survey").
        setHeader("content-type", "application/json").
        setBody(payload).
        POST

      val request = Http(builtRequest)
      response = request()
  }

  When( """^I POST some survey results to the default meeting ID:$""") {
    (results: DataTable) =>
      import scala.collection.JavaConversions._
      val answersMap = results.asMaps().toList
      answersMap.map(map => {
        val a1 = map("Q1").toInt
        val a2 = map("Q2").toInt

        postDefaultSurveyResults(a1, a2)
        response.getStatusCode should be(200)
      })
  }
  When( """^I GET to the default meeting's survey$""") {
    () =>
      def builtRequest = url(server + "/meetings/" + defaultMeeting.id.get.toString + "/survey")
      val request = Http(builtRequest)
      response = request()
      response.getStatusCode should be(200)
  }

  Then( """^I receive a JSON representation of the meeting results:$""") {
    (payload: String) => {
      val parsed = payload.replaceAll("\\$meeting\\.id", defaultMeeting.id.get.toString)

      val requiredJson = parsed.asJson
      //TODO: convert the IDs? or ignore those checks
      val receivedJson = response.getResponseBody.asJson

      object SurveyResponseProtocol extends DefaultJsonProtocol {
        implicit val surveyResponseJson = jsonFormat4(SurveyResponse)
      }
      import SurveyResponseProtocol._

      //Cant quite just simply compare the two things...
      val surveyResponses = receivedJson.convertTo[List[SurveyResponse]]

      val requiredResponses = requiredJson.convertTo[List[SurveyResponse]]

      requiredResponses.map(req => {
        surveyResponses.filter(i => {
          i.answer1 == req.answer1 && i.answer2 == req.answer2 && i.meetingId == req.meetingId
        }).length should be(1)
      })
    }
  }

}
