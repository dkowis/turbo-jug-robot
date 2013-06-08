package org.shlrm.jugbot

import spray.routing.HttpService
import com.typesafe.config.ConfigFactory
import spray.http.{MediaTypes, StatusCodes, StatusCode}
import org.shlrm.jugbot.slick.Meeting
import MediaTypes._
import akka.event.slf4j.SLF4JLogging
import akka.event.LoggingAdapter

trait JugService extends HttpService {

  import spray.json._
  import MeetingsJsonProtocol._
  import SprayJsonSupport._

  //Should include the default types...

  implicit val config = ConfigFactory.load().getConfig("integrationTest") //TODO: get this from environment!

  val meetingsHandler = new MeetingsHandler

  //have to get this so that the thing can toJson stuff for me
  //import MeetingsJsonProtocol._
  //MAGIC SAUCE IS pathPrefix, to leave unmatched bits!
  //Stupid valuable google groups: https://groups.google.com/forum/#!msg/spray-user/3x9IkhM_W4Q/ckc9E6qOxgIJ
  // THANK YOU: https://github.com/ctcarrier/mycotrack-api/blob/master/src/main/scala/com/mycotrack/api/endpoint/WebAppEndpoint.scala
  val jugBot = path("meetings") {
    //TODO: wrap this in some kind of authenticated somehow
    post {
      //TODO: marshal or unmarshal?
      entity(as[String]) {
        data => {
          respondWithStatus(StatusCodes.Created) {
            complete {
              val meeting = (data asJson).convertTo[Meeting]
              meetingsHandler.createMeeting(meeting)
            }
          }
        }
      }
    } ~
      get {
        //TODO: could probably do caching
        respondWithMediaType(`application/json`) {
          complete {
            meetingsHandler.listMeetings
          }
        }
      }
  } ~
    pathPrefix("meetings" / JavaUUID) {
      meetingId =>
        path("") {
          get {
            complete {
              "I'm not sure"
            }
          }
        } ~
          path("/survey") {
            get {
              complete {
                "meeting survey"
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
