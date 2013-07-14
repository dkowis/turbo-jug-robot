package org.shlrm.jugbot

import akka.actor.{ActorSystem, ActorContext}
import akka.pattern.AskSupport
import org.shlrm.jugbot.ConfigProtocol.GetConfig
import com.typesafe.config.Config
import scala.concurrent.Await

object ConfigGetter extends AskSupport {

  import scala.concurrent.duration._

  def config(system: ActorSystem): Config = {
    val configActor = system.actorFor("/user/ConfigActor")
    println(s"config actor is ${configActor}")
    val configFuture = configActor.ask(GetConfig)(10 seconds).mapTo[Config]
    val result = Await.result(configFuture, 10 seconds)
    result
  }

}
