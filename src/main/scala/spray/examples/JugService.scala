package spray.examples

import spray.routing.HttpService

trait JugService extends HttpService {
  //MAGIC SAUCE IS pathPrefix, to leave unmatched bits!
  //Stupid valuable google groups: https://groups.google.com/forum/#!msg/spray-user/3x9IkhM_W4Q/ckc9E6qOxgIJ
  // THANK YOU: https://github.com/ctcarrier/mycotrack-api/blob/master/src/main/scala/com/mycotrack/api/endpoint/WebAppEndpoint.scala
  val jugBot = path("meetings") {
    //TODO: wrap this in some kind of authenticated somehow
    post {
      //TODO: marshal or unmarshal?
      complete {
        //create a meeting
        "meeting id"
      }
    } ~
      get {
        //TODO: could probably do caching
        complete {
          "meeting list"
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
