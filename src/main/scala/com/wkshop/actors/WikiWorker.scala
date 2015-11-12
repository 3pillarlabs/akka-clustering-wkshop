package com.wkshop.actors

import java.util.concurrent.TimeoutException

import akka.actor.{Props, ActorSystem, RootActorPath, Actor}

import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}

import scala.concurrent.{Await, ExecutionContext}

import com.typesafe.config.ConfigFactory

import com.wkshop.reader.{WikiJsonParser, Downloader}
import scala.concurrent.duration._
import scala.language.postfixOps




class WikiWorker extends Actor with Downloader with WikiJsonParser {

  import ExecutionContext.Implicits.global

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive = {
    case wp: WikiPage =>
      val who = sender()

      val deeper = links(content(wp.pageName))
      deeper.recover { case e => who ! JobFailed(e.getMessage, wp) }

      try {
        val nextLinks: List[String] = Await.result(deeper, 1 seconds)
        nextLinks.foreach { l =>
          val wp = WikiPage(l)
          println("sending back %s".format(wp))
          who ! wp
          Thread.sleep(500)
        }
      } catch {
        case _:TimeoutException => who ! JobFailed("timeout", wp)
      }


    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register

    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit =
    if (member.hasRole("gatherer"))
      context.actorSelection(RootActorPath(member.address) / "user" / "gatherer") ! WikiWorkerRegistration
}


object WikiWorker {
  def main(optPort: Option[Integer]): Unit = {

    var port:Integer = 0

    optPort match {
      case Some(p) => port = p
      case None => port = 0
    }

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [worker]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[WikiWorker])
  }


}
