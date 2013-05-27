import java.util.UUID
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import spray.examples.JugService
import spray.testkit.ScalatestRouteTest

class JugBotSpec extends FunSpec with ScalatestRouteTest with ShouldMatchers with JugService {

  def actorRefFactory = system

  describe("Static routes for the jugbot") {
    it("returns the primary javascript") (pending)
    it("returns the bootstrap stylesheet") (pending)
    it("returns one of the images") (pending)
  }

  describe("Routes for the jugbot") {
    describe("handles") {
      describe("functionality for the survey takers") {
        it("handles a get to /meetings") {
          Get("/meetings") ~> jugBot ~> check {
            handled should be (true)
          }
        }

        it("handles a get to /meetings/:id") {
          val uuid = UUID.randomUUID()
          Get(s"/meetings/${uuid}") ~> jugBot ~> check {
            handled should be (true)
          }
        }
        it("handles a post to /meetings/:id")(pending)
      }
      describe("functionality for the backend") {
        it("handles a get to /meetings/:id/results")(pending)
        it("handles a get to /meetings/jugmaster")(pending)
        it("handles a post to /meetings")(pending)
      }
    }
  }

}
