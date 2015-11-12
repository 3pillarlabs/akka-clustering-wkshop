package com.wkshop.actors

import com.wkshop.reader.Types._

case class WikiPage(val pageName: WikiPageName)

case object WikiWorkerRegistration

case class JobFailed(val reason: String, wp: WikiPage)

