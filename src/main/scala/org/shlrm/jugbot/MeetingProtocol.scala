package org.shlrm.jugbot

import org.shlrm.jugbot.slick.Meeting

object MeetingProtocol {

  case class ListMeetings()

  case class GetMeeting(meetingId: Int)

  case class CreateMeeting(meeting: Meeting)

  case class UpdateResults(meetingId: Int, answers: SurveyAnswers)

  case class SurveyResults(meetingId: Int)

}
