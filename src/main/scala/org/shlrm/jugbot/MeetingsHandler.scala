package org.shlrm.jugbot

import org.shlrm.jugbot.slick.{SurveyResult, DAL, Meeting}
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

  def updateResults(meetingId: Int, answers: SurveyAnswers):Unit = {
    db withSession {
      implicit session: Session => {
        //Query out and update the values for the survey results for that survey
        val count = Query(SurveyResults).filter(_.meetingId === meetingId).list.length
        if (count == 0) {
          //Insert a new one
          SurveyResults.insert(SurveyResult(count = 1, total = answers.q1 + answers.q2, meetingId = meetingId))
        } else {
          //Update the row
          val q = for {sr <- SurveyResults if sr.meetingId === meetingId} yield (sr.count, sr.total)
          q.mutate { r =>
            r.row = r.row.copy(_1 = r.row._1 + 1, _2 = r.row._2 + answers.q1 + answers.q2)
          }

        }
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
}
