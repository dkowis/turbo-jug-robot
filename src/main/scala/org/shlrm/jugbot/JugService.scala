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
import reflect.ClassTag
import scala.concurrent.Future
import spray.httpx.marshalling.Marshaller

trait JugService extends HttpService {

  import spray.json._
  import MeetingsJsonProtocol._


  //Moving to the routed actor!
  val meetingActor = actorRefFactory.actorOf(Props[MeetingActor].withRouter(RoundRobinRouter(nrOfInstances = 5)))
  implicit val timeout = Timeout(10 seconds)
  implicit def executionContext = actorRefFactory.dispatcher


  //have to get this so that the thing can toJson stuff for me
  //import MeetingsJsonProtocol._
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
              implicitly[Marshaller[Future[List[SurveyResponse]]]]

              (meetingActor ? SurveyResults(meetingId)).mapTo[List[SurveyResponse]]
            }
          }
      } ~
        path("") {
          get {
            complete {
              meetingActor ! GetMeeting(meetingId)
            }
          }
        }
  } ~
    path("meetings") {
      //TODO: wrap this in some kind of authenticated somehow
      post {
        //TODO: marshal or unmarshal?
        entity(as[String]) {
          data => {
            respondWithStatus(StatusCodes.Created) {
              complete {
                val meeting = (data asJson).convertTo[Meeting]
                meetingActor ! CreateMeeting(meeting)
              }
            }
          }
        }
      } ~
        get {
          //TODO: could probably do caching
          respondWithMediaType(`application/json`) {
            complete {
              meetingActor ! ListMeetings()
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
