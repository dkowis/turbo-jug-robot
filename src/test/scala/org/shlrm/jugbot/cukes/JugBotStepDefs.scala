package org.shlrm.jugbot.cukes

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.PendingException

class JugBotStepDefs extends ScalaDsl with EN {
  val server = "http://localhost:8080"
  //TODO: should I just test this thing with jruby?

  When( """^I POST the JSON to "([^"]*)":$""") {
    (path: String, rawJson: String) =>
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
      //TODO: create a meeting in something somehow?
      throw new PendingException()
  }
  When( """^I GET to "([^"]*)"$""") {
    (path: String) =>
      //TODO: make a request against the right stuff?
      throw new PendingException()
  }
  Then( """^I receive the JSON:$""") {
    (rawJson: String) =>
    //TODO: match up the output of the call with the raw JSON
      throw new PendingException()
  }
}
