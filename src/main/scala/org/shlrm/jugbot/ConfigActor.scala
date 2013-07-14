package org.shlrm.jugbot

import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.{Config, ConfigFactory}
import org.shlrm.jugbot.ConfigProtocol.GetConfig

class ConfigActor(configEnv:String) extends Actor with ActorLogging {
  log.debug(s"Creating a config actor using ${configEnv}")

  val config:Config = ConfigFactory.load().getConfig(configEnv)

  def receive = {
    case GetConfig =>  {
      log.debug("Received a request for config!")
      sender ! config
    }
  }

}

object ConfigProtocol {
  case object GetConfig
}