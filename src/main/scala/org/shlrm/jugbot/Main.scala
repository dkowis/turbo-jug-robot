package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props
import com.googlecode.flyway.core.Flyway
import com.typesafe.config.ConfigFactory

//TODO: would have to add back in SSL support if I wants to use it
object Main extends App with SprayCanHttpServerApp {

  val handler = system.actorOf(Props[JugServiceActor], "jug-service")

  //TODO: make environment variable stuff behave, or CLI Opts? Need a environment
  val config = ConfigFactory.load().getConfig("integrationTest") //TODO: have to get that from environment
  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)
}
