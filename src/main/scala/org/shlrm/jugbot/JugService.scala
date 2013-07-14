package org.shlrm.jugbot

import spray.routing.HttpService
import spray.http._
import org.shlrm.jugbot.slick.{SurveyResponse, Meeting}
import MediaTypes._
import akka.routing.RoundRobinRouter
import akka.actor.Props
import org.shlrm.jugbot.MeetingProtocol._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

trait JugService extends HttpService {

  import spray.json._
  import spray.httpx.SprayJsonSupport._

  import MeetingsJsonProtocol._

  //Moving to the routed actor!
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val meetingActor = actorRefFactory.actorOf(Props[MeetingActor].withRouter(RoundRobinRouter(nrOfInstances = 5)))


  //MAGIC SAUCE IS pathPrefix, to leave unmatched bits!
  //Stupid valuable google groups: https://groups.google.com/forum/#!msg/spray-user/3x9IkhM_W4Q/ckc9E6qOxgIJ
  // THANK YOU: https://github.com/ctcarrier/mycotrack-api/blob/master/src/main/scala/com/mycotrack/api/endpoint/WebAppEndpoint.scala
  val jugBot = pathPrefix("meetings" / IntNumber) {
    meetingId =>
      path("survey") {
        post {
          entity(as[String]) {
            answersJson =>
              complete {
                val answers = (answersJson asJson).convertTo[SurveyAnswers]
                meetingActor ! UpdateResults(meetingId, answers)
                StatusCodes.OK
              }
          }
        } ~
          get {
            complete {
              (meetingActor ? SurveyResults(meetingId)).mapTo[List[SurveyResponse]]
            }
          }
      } ~
        path("") {
          get {
            complete {
              (meetingActor ? GetMeeting(meetingId)).mapTo[Meeting]
            }
          }
        }
  } ~
    path("meetings") {
      //TODO: wrap this in some kind of authenticated somehow
      post {
        entity(as[String]) {
          data => {
            respondWithStatus(StatusCodes.Created) {
              complete {
                val meeting = (data asJson).convertTo[Meeting]
                (meetingActor ? CreateMeeting(meeting)).mapTo[Meeting]
              }
            }
          }
        }
      } ~
        get {
          //TODO: could probably do caching
          respondWithMediaType(`application/json`) {
            complete {
              (meetingActor ? ListMeetings()).mapTo[List[Meeting]]
            }
          }
        }
    } ~ pathPrefix("js") {
    getFromResourceDirectory("javascripts") //this *should* get the javascripts from teh war file...
  } ~
    pathPrefix("stylesheets") {
      getFromResourceDirectory("css")
    } ~
    pathPrefix("images") {
      getFromResourceDirectory("images")
    } ~
    path("backend") {
      getFromResource("backend.html")
    } ~
    path("") {
      //Primary resource, has to be at the bottom
      getFromResource("mainPage.html")
    }
}
