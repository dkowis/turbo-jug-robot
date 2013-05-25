package spray.examples

import spray.routing.HttpService
import spray.http.MediaTypes
import MediaTypes._

trait JugService extends HttpService {
  val jugRoute = path("js") {
    getFromBrowseableDirectory("javascripts") //this *should* get the javascripts from teh war file...
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
