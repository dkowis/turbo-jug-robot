package org.shlrm.jugbot

import akka.actor.{ActorSystem, Props}
import com.googlecode.flyway.core.Flyway
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.io.IO

//TODO: would have to add back in SSL support if I wants to use it
object Main extends App {

  implicit val system  = ActorSystem("turboJugRobot")

  val service = system.actorOf(Props[JugServiceActor], "jug-service")

  //TODO: make environment variable stuff behave, or CLI Opts? Need a environment
  val config = ConfigFactory.load().getConfig("integrationTest") //TODO: have to get that from environment
  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
