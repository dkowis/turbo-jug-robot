package org.shlrm.jugbot

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.googlecode.flyway.core.Flyway
import org.h2.tools.Server
import java.sql.DriverManager
import spray.can.Http
import akka.io.IO

/**
 * This is a terribly lame way to fire up all the things, but it does work.
 * Perhaps I can figure out how to get this into a SBT plugin
 */
object IntegrationMain extends App {

  implicit val system = ActorSystem("turboJugRobotIntegration")

  //Create a config actor for integration testing
  val environment = "integrationTest"
  val configActor = system.actorOf(Props(new ConfigActor(environment)), "ConfigActor")

  //Grab the configuration
  val config = ConfigGetter.config(system)

  //Start up an in-memory database that has a TCP connection available.
  //This way, my integration tests can also talk to it
  Class.forName("org.h2.Driver")
  val url = "jdbc:h2:mem:integrationTest"
  val inMemConn = DriverManager.getConnection(url, "sa", "")
  //Fire up teh database!
  val server = Server.createTcpServer("-tcp").start()

  //Database fired up and established, now flyway it
  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  val service = system.actorOf(Props(new JugServiceActor), "jug-service")

  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
  //Not sure what to do on death?
}