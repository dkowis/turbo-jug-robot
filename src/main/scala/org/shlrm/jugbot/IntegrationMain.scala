package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props
import com.typesafe.config.ConfigFactory
import com.googlecode.flyway.core.Flyway
import org.h2.tools.Server

/**
 * This is a terribly lame way to fire up all the things, but it does work.
 * Perhaps I can figure out how to get this into a SBT plugin
 */
object IntegrationMain extends App with SprayCanHttpServerApp {
  //TODO: make environment variable stuff behave, or CLI Opts? Need a environment
  val config = ConfigFactory.load().getConfig("integrationTest")

  //Fire up teh database!
  val server = Server.createTcpServer(
    "-tcp"
  ).start()

  //TODO: have to get that from environment
  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  val handler = system.actorOf(Props[JugServiceActor], "jug-service")
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)
  //Not sure what to do on death?
}