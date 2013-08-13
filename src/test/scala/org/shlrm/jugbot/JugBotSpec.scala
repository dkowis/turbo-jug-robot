package org.shlrm.jugbot

import java.util.UUID
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import spray.testkit.ScalatestRouteTest

class JugBotSpec extends FunSpec with ScalatestRouteTest with ShouldMatchers with JugService {

  def actorRefFactory = system

  describe("Static routes for the jugbot") {
    it("returns the primary javascript") {
      Get("/js/app.js") ~> jugBot ~> check {
        handled should be(true)
      }
    }
    it("returns the bootstrap stylesheet") {
      Get("/stylesheets/bootstrap.css") ~> jugBot ~> check {
        handled should be(true)
      }
    }
    it("returns one of the images") {
      Get("/web/images/glyphicons-halflings.png") ~> jugBot ~> check {
        handled should be(true)
      }
    }
  }

  describe("Routes for the jugbot") {
    describe("handles") {
      describe("functionality for the survey takers") {
        it("handles a get to /meetings") {
          Get("/meetings") ~> jugBot ~> check {
            handled should be(true)
          }
        }

        it("handles a get to /meetings/:id") {
          Get(s"/meetings/1") ~> jugBot ~> check {
            handled should be(true)
          }
        }
        it("handles a get to /meetings/:id/survey") {
          Get("/meetings/1/survey") ~> jugBot ~> check {
            handled should be(true)
          }
        }

        it("handles a post to /meetings/:id/survey") {
          Post("/meetings/1/survey") ~> jugBot ~> check {
            handled should be(true)
          }
        }
      }
      describe("functionality for the backend") {
        it("handles a get to /meetings/:id/results")(pending)
        it("handles a get to /meetings/jugmaster")(pending)
        it("handles a post to /meetings")(pending)
      }
    }
  }

}
