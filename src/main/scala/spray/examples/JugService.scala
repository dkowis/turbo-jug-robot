package spray.examples

import spray.routing.HttpService
import spray.http.MediaTypes
import MediaTypes._

trait JugService extends HttpService {
  //MAGIC SAUCE IS pathPrefix, to leave unmatched bits!
  //Stupid valuable google groups: https://groups.google.com/forum/#!msg/spray-user/3x9IkhM_W4Q/ckc9E6qOxgIJ
  // THANK YOU: https://github.com/ctcarrier/mycotrack-api/blob/master/src/main/scala/com/mycotrack/api/endpoint/WebAppEndpoint.scala
  val jugRoute = pathPrefix("js") {
    getFromResourceDirectory("javascripts") //this *should* get the javascripts from teh war file...
  } ~
    pathPrefix("images") {
      getFromResourceDirectory("images")
    } ~
    path("/") {
      getFromResource("mainPage.html")
    } ~
    //Second route to get the index page
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Hello world</h1>
              </body>
            </html>
          }
        }
      }
    }

}
