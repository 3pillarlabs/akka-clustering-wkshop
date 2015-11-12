package com.wkshop.actors

import akka.dispatch.{RequiresMessageQueue, BoundedMessageQueueSemantics}

import scala.language.postfixOps

import akka.actor._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

import ExecutionContext.Implicits.global

import com.typesafe.config.ConfigFactory



class WikiGatherer extends Actor {

  var workers = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  val requeueActor: ActorRef = self

  def selectWorker =
    workers.isEmpty match {
      case true => None
      case false => Some(workers(jobCounter % workers.size))
    }

  def receive = {
    case job: WikiPage =>

      selectWorker match {
        case None =>
          println("Service unavailable, try again in 2 sec", job)
          context.system.scheduler.scheduleOnce(2 seconds) {
            requeueActor ! job
          }

        case Some(w) =>
          jobCounter += 1
          println("diving into %s from %s".format(job, sender()))
          w ! job
      }

    case WikiWorkerRegistration if !workers.contains(sender()) =>
      println("registering wiki worker %s".format(sender()))
      context watch sender()
      workers = workers :+ sender()

    case JobFailed(r, wp) =>
      println("job for page %s failed, reason: %s".format(wp, r))

    case Terminated(a) =>
      println("worker %s died".format(a))
      workers = workers.filterNot(_ == a)
  }
}


object WikiGatherer {
  def main(optPort: Option[Integer]): Unit = {

    var port: Integer = 0

    optPort match {
      case Some(p) => port = p
      case None => port = 0
    }

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [gatherer]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    val gatherer = system.actorOf(Props[WikiGatherer].withMailbox("bounded-mailbox"), name = "gatherer")


    gatherer ! WikiPage("Redneck")
  }
}
