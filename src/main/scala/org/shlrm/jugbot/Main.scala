package org.shlrm.jugbot

import akka.actor.{ActorSystem, Props}
import com.googlecode.flyway.core.Flyway
import com.typesafe.config.{Config, ConfigFactory}
import spray.can.Http
import akka.io.IO
import akka.pattern.AskSupport
import org.shlrm.jugbot.ConfigProtocol.GetConfig
import scala.concurrent.Await

//TODO: would have to add back in SSL support if I wants to use it
object Main extends App with AskSupport {

  import scala.concurrent.duration._

  implicit val system  = ActorSystem("turboJugRobot")

  val environment = System.getenv("APP_ENV")
  val configActor = system.actorOf(Props(new ConfigActor(environment)), "ConfigActor")

  val config = ConfigGetter.config(system)

  //Has to be new each time
  val service = system.actorOf(Props(new JugServiceActor()), "jug-service")

  val flyway = new Flyway()
  flyway.setDataSource(config.getString("db.url"), config.getString("db.user"), config.getString("db.pass"))
  flyway.migrate()

  //create a new http server using our handler and tell it what to bind to
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}
