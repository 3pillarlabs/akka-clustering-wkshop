package com.wkshop.reader

import com.wkshop.reader.Types._
import net.liftweb.json._

import scala.concurrent.Future

class InvalidPageContent extends Exception

trait WikiJsonParser {

  import scala.concurrent.ExecutionContext.Implicits.global

  def links(pageContent: Future[String]): Future[List[String]] = pageContent map parseLinks


  def parseLinks(pageContent: String): List[WikiPageName] = {
    val toJson = parse(pageContent)
    val pid = (toJson \ "query" \ "pageids" \ classOf[JString])(0)

    if ("-1".equals(pid)) throw new InvalidPageContent

    toJson \ "query" \ "pages" \ pid \ "links" \\ classOf[JString]
  }

}
