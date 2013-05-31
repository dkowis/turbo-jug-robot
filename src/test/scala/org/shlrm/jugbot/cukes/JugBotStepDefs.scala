package org.shlrm.jugbot.cukes

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.PendingException
import dispatch._, Defaults._

class JugBotStepDefs extends ScalaDsl with EN {
  val server = "http://localhost:8080"
  val service = host("localhost", 8080)
  //TODO: should I just test this thing with jruby?

  When( """^I POST the JSON to "([^"]*)":$""") {
    (path: String, rawJson: String) =>
      //TODO: need to split up the path!
    //Make a POST to the path, sending the JSON
      throw new PendingException()
  }
  Then( """^the result should be (\d+) OK$""") {
    (arg0: Int) =>
      //TODO: check the result of the call
      throw new PendingException()
  }
  Given( """^the default meeting exists$""") {
    () =>
      println("Pretending this exists!")
  }
  When( """^I GET to "([^"]*)"$""") {
    (path: String) =>
      //Yay this works
      //Now lets do some database stuff
      def builtRequest = (service / "path").GET
      val request = Http(builtRequest)
      val output = request()

      println(s"data is: ${output}")
      //TODO: make a request against the right stuff?
  }
  Then( """^I receive the JSON:$""") {
    (rawJson: String) =>
    //TODO: match up the output of the call with the raw JSON
      throw new PendingException()
  }
}
