package org.shlrm.jugbot

import spray.util.SprayActorLogging
import akka.actor.{Props, Actor}
import com.typesafe.config.Config

class JugServiceActor(config:Config) extends Actor with SprayActorLogging with JugService {

  def actorRefFactory = context

  def receive = runRoute(jugBot)

}