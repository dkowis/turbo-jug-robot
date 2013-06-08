package org.shlrm.jugbot

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props
import com.typesafe.config.ConfigFactory
import com.googlecode.flyway.core.Flyway
import org.h2.tools.Server
import java.sql.DriverManager

/**
 * This is a terribly lame way to fire up all the things, but it does work.
 * Perhaps I can figure out how to get this into a SBT plugin
 */
object IntegrationMain extends App with SprayCanHttpServerApp {
  //TODO: make environment variable stuff behave, or CLI Opts? Need a environment
  val config = ConfigFactory.load().getConfig("integrationTest")

  //Establish a lame in memory database
  //Have to do this first before I can start the server, then I can connect to it via TCP.
  //Technically, I don't need to do this for the flyway stuff, but this is simple and easy.
  Class.forName("org.h2.Driver")
  val url = "jdbc:h2:mem:integrationTest"
  val inMemConn = DriverManager.getConnection(url, "sa", "")

  //Fire up teh database!
  val server = Server.createTcpServer(
    "-tcp"
  ).start()

  //Have to create the in-memory database first, then start the the connection, I think -- nope
  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  val handler = system.actorOf(Props[JugServiceActor], "jug-service")
  newHttpServer(handler) ! Bind(interface = "localhost", port = 8080)
  //Not sure what to do on death?
}