package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props

object Main extends App with SprayCanHttpServerApp with MySslConfiguration {

  val handler = system.actorOf(Props[DemoService])

  //create a new http server using our handler and tell it what to bind to
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)

}
