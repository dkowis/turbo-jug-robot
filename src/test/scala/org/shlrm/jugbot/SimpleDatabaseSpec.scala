package org.shlrm.jugbot

import org.scalatest.{BeforeAndAfter, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import org.shlrm.jugbot.slick.{SurveyResult, Meeting, DAL}
import scala.slick.driver.H2Driver
import scala.slick.session.Database
import java.sql.Date

/**
 * This test class is less about testing my application, and more about figuring out how to use Slick
 * Then I'll take what I've prototyped out here, and make an actor for it, and stick it in the application
 */
class SimpleDatabaseSpec extends FunSpec with ShouldMatchers with BeforeAndAfter {

  def runTests(dal: DAL, db: Database) = {
    import dal._
    import dal.profile.simple._


    describe("the database access layer") {
      it("supports inserting meetings") {
        //Needed to get at the various thingies
        db withSession {
          implicit session: Session =>
          //create our database
            dal.create


            val meeting = Meetings.insert(Meeting("A test meeting", new java.sql.Date(new java.util.Date().getTime())))

            meeting.id.get should be(1)
        }

      }
      it("supports getting a list of all meetings") {
        db withSession {
          implicit session: Session =>
            dal.create
            //Stick a pile of meetings in there
            (10 to 19).map {
              n =>
                Meetings.insert(Meeting(s"Meeting ${n}", Date.valueOf(s"2013-01-${n}")))
            }

            val q = Query(Meetings)
            val allMeetings = q.list.toList

            allMeetings.length should be(10)

        }
      }
      it("supports getting a single meeting") {
        db withSession {
          implicit session: Session =>
            dal.create

            //Stick a pile of meetings in there
            (10 to 19).map {
              n =>
                Meetings.insert(Meeting(s"Meeting ${n}", Date.valueOf(s"2013-01-${n}")))
            }

            val q = Query(Meetings)
            val results = q.filter(_.title === "Meeting 14").list
            results.size should be(1)

            val mtg = results.head
            mtg.title should be("Meeting 14")
            mtg.date should be(Date.valueOf("2013-01-14"))
        }
      }
      it("supports getting a survey result from a meeting") {
        db withSession {
          implicit session: Session =>
            dal.create
            val meeting = Meetings.insert(Meeting("Meeting the best", Date.valueOf("2013-02-17")))
            //inserts the item into the database, and there's logic to return the whole item, not just the affected rows
            val newItem = SurveyResults.insert(SurveyResult(1, 0, meeting.id.get, None))

            //See http://slick.typesafe.com/doc/1.0.1/lifted-embedding.html#querying
            //get all the items in the database to see how many there are
            val all = Query(SurveyResults).list
            all.length should be(1)

            //construct a query filtering only for the one we want.
            val query = Query(SurveyResults).filter(_.meetingId === meeting.id.get)
            query.list.length should be(1)
            //get a query result, and stick it into a SurveyResult, which is more meaningful
            val qr = SurveyResult.tupled(query.first)
            qr should be(newItem)

        }
      }
    }
  }

  describe("For H2") {
    val dal = new DAL(H2Driver)
    val db = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")
    runTests(dal, db)
  }

}
