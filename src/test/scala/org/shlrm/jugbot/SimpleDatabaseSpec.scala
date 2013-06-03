package org.shlrm.jugbot

import org.scalatest.{BeforeAndAfter, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import org.shlrm.jugbot.slick.{Meeting, DAL}
import scala.slick.driver.H2Driver
import scala.slick.session.Database
import java.sql.Date

/**
 * This test class is less about testing my application, and more about figuring out how to use Slick
 * Then I'll take what I've prototyped out here, and make an actor for it, and stick it in the application
 */
class SimpleDatabaseSpec extends FunSpec with ShouldMatchers with BeforeAndAfter {
  describe("For H2") {
    val dal = new DAL(H2Driver)
    val db = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")
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

            val q = tableToQuery(Meetings)
            val allMeetings = q.list.toList

            allMeetings.length should be(10)

        }
      }
      it("supports getting a single meeting") {
        fail("your mom is too fat")
      }
      it("supports getting a survey result from a meeting")(pending)
    }
  }

}
