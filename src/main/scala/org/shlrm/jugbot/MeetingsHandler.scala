package org.shlrm.jugbot

import org.shlrm.jugbot.slick.{SurveyResponse, DAL, Meeting}
import com.typesafe.config.Config
import scala.slick.driver.H2Driver
import scala.slick.session.Database
import spray.json._
import java.sql.Date
import akka.event.slf4j.SLF4JLogging

case class SurveyAnswers(q1: Int, q2: Int)

class MeetingsHandler(implicit config: Config) extends SLF4JLogging {

  //TODO: make it handle multiple databases, pgsql in prod, h2 in local/test
  val dal = new DAL(H2Driver)
  val db = Database.forURL(url = config.getString("db.url"),
    user = config.getString("db.user"),
    password = config.getString("db.pass")
  )

  //Import the stuff from the DAL to make it easier to write database logics!

  import dal._
  import dal.profile.simple._

  def listMeetings: List[Meeting] = {
    db withSession {
      implicit session: Session => {
        log.debug("Getting list of meetings!")
        Query(Meetings).list.toList
      }
    }
  }

  def getMeeting(meetingId: Int): Meeting = {
    db withSession {
      implicit session: Session => {
        log.debug("Getting a meeting!")
        Query(Meetings).filter(_.id === meetingId).first
      }
    }
  }

  def createMeeting(meeting: Meeting) = {
    db withSession {
      implicit session: Session => {
        log.debug("Inserting a meeting!")
        Meetings.insert(meeting)
      }
    }
  }

  def updateResults(meetingId: Int, answers: SurveyAnswers): Unit = {
    db withSession {
      implicit session: Session => {
        //Now just inserting another response into the database
        val response = SurveyResponse(answers.q1, answers.q2, meetingId)
        SurveyResponses.insert(response)
      }
    }
  }

  def surveyResults(meetingId: Int):List[SurveyResponse] = {
    db withSession {
      implicit session: Session => {
        //just query out a list of survey Responses
        Query(SurveyResponses).filter(_.meetingId === meetingId).list
      }
    }
  }
}

object MeetingsJsonProtocol extends DefaultJsonProtocol {

  //TODO: handle properly parsing the date

  implicit object SqlDateJsonFormat extends RootJsonFormat[Date] {
    def write(d: Date) = {
      JsString(d.toString())
    }

    def read(value: JsValue) = value match {
      case JsString(dateString) => Date.valueOf(dateString) //Probably not the right way to do this
      case _ => throw new DeserializationException("Date Expected")
    }
  }

  implicit val meetingFormat = jsonFormat3(Meeting)
  implicit val answersFormat = jsonFormat2(SurveyAnswers)
  implicit val surveyResultsFormat = jsonFormat4(SurveyResponse)
}
