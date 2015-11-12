package com.wkshop.reader

import com.wkshop.reader.Types.WikiPageName
import dispatch._, Defaults._

import scala.concurrent.Future


trait Downloader {

  case class WikiLinksRequest(pageName: WikiPageName) {
    val baseURL = "https://en.wikipedia.org/w/api.php"
    val params = Map(
      "action" -> "query",
      "format" -> "json",
      "prop" -> "links",
      "indexpageids" -> "y",
      "titles" -> pageName
    )
  }

  def content(pageName: WikiPageName): Future[String] = {
    val r = new WikiLinksRequest(pageName)
    Http(url(r.baseURL) <<? r.params OK as.String)
  }

}
