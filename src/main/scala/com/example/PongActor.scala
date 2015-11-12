package com.example

import akka.actor.{Actor, ActorLogging, Props}

class PongActor extends Actor with ActorLogging {
  import PongActor._

  def receive = {
    ??? // send back a Pong upon receiving a Ping
  }	
}

object PongActor {
  val props = Props[PongActor]
  case class PongMessage(text: String)
}
