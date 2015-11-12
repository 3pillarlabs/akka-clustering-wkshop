package com.wkshop.reader

import org.scalatest.{Matchers, FlatSpec}

class WikiJsonParserSpec extends FlatSpec with Matchers {

  "wiki json parser" should "output links to other pages" in {

    val toParse =
      """
        |{"continue":{"plcontinue":"30664|0|Ancient_Greek","continue":"||"},"query":{"pageids":["30664"],"pages":{"30664":{"pageid":30664,"ns":0,"title":"Telescopium","links":[{"ns":0,"title":"Algol variable"},{"ns":0,"title":"Alpha Telescopii"}]}}}}
      """.stripMargin

    val pag = new WikiJsonParser{}.parseLinks(toParse)

    pag should be (List("Algol variable", "Alpha Telescopii"))
  }

  "wiki json parser" should "throw on invalid pages" in {

    val toParse =
      """
        |{"continue":{"plcontinue":"30664|0|Ancient_Greek","continue":"||"},"query":{"pageids":["-1"],"pages":{"30664":{"pageid":30664,"ns":0,"title":"Telescopium","links":[{"ns":0,"title":"Algol variable"},{"ns":0,"title":"Alpha Telescopii"}]}}}}
      """.stripMargin

    intercept[InvalidPageContent] {
      val pag = new WikiJsonParser{}.parseLinks(toParse)
    }

  }


}
