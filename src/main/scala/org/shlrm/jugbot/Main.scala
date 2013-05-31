package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp

object Main extends App with SprayCanHttpServerApp with MySslConfiguration {

  val handler = system.actorOf(Props[JugServiceActor])

  //create a new http server using our handler and tell it what to bind to
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)

}
