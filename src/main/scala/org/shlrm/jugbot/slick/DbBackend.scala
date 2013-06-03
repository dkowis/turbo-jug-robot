package org.shlrm.jugbot.slick

import scala.slick.driver.ExtendedProfile
import java.sql.Date

/*
Basically, stole everything from here:
https://github.com/slick/slick-examples/blob/master/src/main/scala/com/typesafe/slick/examples/lifted/MultiDBCakeExample.scala

So far, I think I understand most of it
 */

trait Profile {
  val profile: ExtendedProfile
}

case class Meeting(title: String, date: Date, id: Option[Int] = None) {
  def surveyResults = {
    ???
  }
}

trait MeetingComponent {
  this: Profile =>
  //requires a profile to be mixed in

  import profile.simple._

  //... to be able import profile.simple._

  object Meetings extends Table[Meeting]("MEETINGS") {
    def id = column[Option[Int]]("MEETING_ID", O.PrimaryKey, O.AutoInc)

    def date = column[Date]("MEETING_DATE", O.NotNull)

    def title = column[String]("MEETING_TITLE", O.NotNull)

    def * = title ~ date ~ id <> (Meeting, Meeting.unapply _)

    //This seems really icky, but I think it's right...
    val autoInc = title ~ date returning id into {
      case (values, id) => Meeting(values._1, values._2, id)
    }

    def insert(meeting: Meeting)(implicit session: Session): Meeting = {
      autoInc.insert(meeting.title, meeting.date)
    }
  }

}

case class SurveyResult(count: Int, total: Int, meeting:Meeting, id: Option[Int] = None)

trait SurveyResultComponent {
  this:Profile with MeetingComponent =>
  import profile.simple._

  //TODO: how do I do the type mapping when I have nested thingies
  object SurveyResults extends Table[(Int, Int, Int, Option[Int])]("SURVEY_RESULTS") {

    def id = column[Option[Int]]("SURVEY_RESULTS_ID", O.PrimaryKey, O.AutoInc)
    def count = column[Int]("COUNT", O.NotNull)
    def total = column[Int]("TOTAL", O.NotNull)
    //TODO: use a foreign key
    def meetingId = column[Int]("MEETING_ID", O.NotNull)
    def meeting = foreignKey("MEETING_FK", meetingId, Meetings)(_.id.get)

    def * = count ~ total ~ meetingId ~ id

    private def autoInc(implicit session:Session) = count ~ total ~ meetingId returning id into {
      //Hrm, how does this work?
      case (_,id) => id
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