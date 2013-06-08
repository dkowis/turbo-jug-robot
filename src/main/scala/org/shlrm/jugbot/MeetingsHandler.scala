package org.shlrm.jugbot

import org.shlrm.jugbot.slick.DAL
import com.typesafe.config.Config
import scala.slick.driver.H2Driver
import scala.slick.session.Database
import spray.json._
import java.sql.Date
import org.shlrm.jugbot.slick.Meeting

class MeetingsHandler(implicit config: Config) {

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
        Query(Meetings).list.toList
      }
    }
  }
}

object MeetingsJsonProtocol extends DefaultJsonProtocol {
  //TODO: handle properly parsing the date

  implicit object SqlDateJsonFormat extends RootJsonFormat[Date] {
    def write(d:Date) = {
      JsString(d.toString())
    }
    def read(value: JsValue) = value match {
      case JsString(dateString) => Date.valueOf(dateString) //Probably not the right way to do this
      case _ => throw new DeserializationException("Date Expected")
    }
  }

  implicit val meetingFormat = jsonFormat3(Meeting)
}
