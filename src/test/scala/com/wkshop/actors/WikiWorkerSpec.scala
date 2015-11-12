package com.wkshop.actors

import akka.actor.{PoisonPill, ActorSystem}
import org.scalatest.{WordSpecLike, BeforeAndAfterAll, Matchers}

import akka.testkit.{ImplicitSender, TestKit, TestActorRef}

import scala.language.postfixOps


class WikiWorkerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    system.terminate()
  }


  "a wiki actor" must {

    "dive into wiki pages" in {
      val waRef = TestActorRef[WikiWorker]
      waRef ! WikiPage("Redneck")
      for(i <- Range(0,10)) expectMsgClass(classOf[WikiPage])
      waRef ! PoisonPill
    }

    "send back an error message if it fails to download" in {
      val waRef = TestActorRef[WikiWorker]
      waRef ! WikiPage("thispagedoesnotexist")
      expectMsgClass(classOf[JobFailed])
      waRef ! PoisonPill
    }

  }

}
