package spray.examples

import spray.routing.HttpService
import spray.http.MediaTypes
import MediaTypes._

trait JugService extends HttpService {
  val jugRoute = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          <html>
            <body>
              <h1>Say hello to
                <i>spray-servlet</i>
                !</h1>
              <p>Defined resources:</p>
              <ul>
                <li>
                  <a href="/ping">/ping</a>
                </li>
                <li>
                  <a href="/stream">/stream</a>
                </li>
                <li>
                  <a href="/crash">/crash</a>
                </li>
                <li>
                  <a href="/timeout">/timeout</a>
                </li>
                <li>
                  <a href="/timeout/timeout">/timeout/timeout</a>
                </li>
              </ul>
            </body>
          </html>
        }
      }
    }
  }
}
