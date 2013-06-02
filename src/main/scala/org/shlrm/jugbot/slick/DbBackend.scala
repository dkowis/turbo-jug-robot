package org.shlrm.jugbot.slick

import scala.slick.driver.ExtendedProfile
import java.sql.Date

trait Profile {
  val profile: ExtendedProfile
}

case class Meeting(title: String, date: Date, id: Option[Int] = None)

trait MeetingComponent {
  this: Profile =>
  //requires a profile to be mixed in

  import profile.simple._

  //... to be able import profile.simple._

  object Meetings extends Table[(String, Date, Option[Int])]("MEETINGS") {
    def id = column[Option[Int]]("MEETING_ID", O.PrimaryKey, O.AutoInc)

    def date = column[Date]("MEETING_DATE", O.NotNull)

    def title = column[String]("MEETING_TITLE", O.NotNull)

    def * = title ~ date ~ id

    //This seems really icky, but I think it's right...
    val autoInc = title ~ date returning id into {
      case (values, id) => Meeting(values._1, values._2, id)
    }

    def insert(meeting: Meeting)(implicit session: Session): Meeting = {
      autoInc.insert(meeting.title, meeting.date)
    }
  }

}

case class SurveyResult(count: Int, total: Int, meeting: Meeting, id: Option[Int] = None)

trait SurveyResultComponent {
  this:Profile with MeetingComponent =>
  import profile.simple._

  object SurveyResults extends Table[(Int, Int, Int, Option[Int])]("SURVEY_RESULTS") {
    def id = column[Option[Int]]("SURVEY_RESULTS_ID", O.PrimaryKey, O.AutoInc)
    def count = column[Int]("COUNT", O.NotNull)
    def total = column[Int]("TOTAL", O.NotNull)
    def meetingId = column[Int]("MEETING_ID", O.NotNull)

    def * = count ~ total ~ meetingId ~ id

    private def autoInc(implicit session:Session) = count ~ total ~ meetingId returning id into {
      //Hrm, how does this work?
      case (_,id) => id
    }

    def insert(result: SurveyResult)(implicit session:Session): SurveyResult = {
      val meeting = if (result.meeting.id.isEmpty) {
        Meetings.insert(meeting)
      } else {
        result.meeting
      }

      val id = autoInc.insert(result.count, result.total, meeting.id.get)
      result.copy(meeting = meeting, id = id)
    }
  }
}

//This Data Access Layer contains all components and a profile
class DAL(override val profile: ExtendedProfile) extends MeetingComponent with SurveyResultComponent with Profile {
  import profile.simple._
  def create(implicit session:Session):Unit = {
    //This sets up the tables, I want to use flyway, or liquibase instead later...
    (Meetings.ddl ++ SurveyResults.ddl).create
  }
}