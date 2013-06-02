package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props

//TODO: would have to add back in SSL support if I wants to use it
object Main extends App with SprayCanHttpServerApp {

  val handler = system.actorOf(Props[JugServiceActor], "jug-service")

  //create a new http server using our handler and tell it what to bind to
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)
}
