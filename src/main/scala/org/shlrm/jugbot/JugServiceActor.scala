package org.shlrm.jugbot

import spray.util.SprayActorLogging
import akka.actor.Actor

class JugServiceActor extends Actor with SprayActorLogging with JugService {

  def actorRefFactory = context

  def receive = runRoute(jugBot)

}
