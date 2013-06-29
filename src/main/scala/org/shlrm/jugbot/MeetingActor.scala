package org.shlrm.jugbot

import akka.actor.Actor
import spray.util.SprayActorLogging
import com.typesafe.config.ConfigFactory
import akka.pattern.AskSupport

class MeetingActor extends Actor with SprayActorLogging with AskSupport {
  implicit val config = ConfigFactory.load().getConfig("integrationTest") //TODO: get this from environment!

  import MeetingProtocol._

  lazy val ms = new MeetingsHandler()

  def receive = {
    //CASE CLASS PARENS MURDER ME
    case ListMeetings() => {
     log.info("Goign to try to list meetings")
      sender ! ms.listMeetings
    }
    case GetMeeting(meetingId) => {
      sender ! ms.getMeeting(meetingId)
    }
    case CreateMeeting(meeting) => {
      sender ! ms.createMeeting(meeting)
    }
    case UpdateResults(meetingId, answers) => {
      ms.updateResults(meetingId, answers)
    }
    case SurveyResults(meetingId) => {
      sender ! ms.surveyResults(meetingId)
    }
    case _ => {
      log.warning("How did I get a message that wasn't for me?!?!")
    }
  }
}
