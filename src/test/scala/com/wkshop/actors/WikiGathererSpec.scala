package com.wkshop.actors

import akka.actor.{PoisonPill, ActorSystem}
import akka.testkit.{TestProbe, TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, Matchers, WordSpecLike}

class WikiGathererSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfter {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    system.terminate()
  }

  "wiki gatherer" must {

    "retry wikipages when there are no workers" in {
      val probe = TestProbe()
      val gaRef = TestActorRef(new WikiGatherer { override val requeueActor = probe.ref })
      gaRef ! WikiPage("Redneck")
      probe.expectMsg(WikiPage("Redneck"))
      gaRef ! PoisonPill
    }

    "send wikipages to workers" in {
      val probe = TestProbe()
      val gaRef = TestActorRef(new WikiGatherer { override def selectWorker = Some(probe.ref) })
      gaRef ! WikiPage("Redneck")
      probe.expectMsg(WikiPage("Redneck"))
      gaRef ! PoisonPill
    }


  }

}
