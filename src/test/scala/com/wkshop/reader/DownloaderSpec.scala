package com.wkshop.reader

import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.Await

import scala.concurrent.duration._

import scala.language.postfixOps

class DownloaderSpec extends FlatSpec with Matchers {

  "downloader" should "download pages from wikipedia by name" in {

    val dloader = new Downloader {}

    val pageToCome = dloader.content("Redneck")
    val res = Await.result(pageToCome, 2 seconds)

    res should include ("Redneck")
  }


}
