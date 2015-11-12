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
    case wp: WikiPage => ??? // call wiki page's api to extract links and send them back one by one

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
