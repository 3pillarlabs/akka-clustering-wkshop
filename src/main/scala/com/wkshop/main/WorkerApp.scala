package com.wkshop.main

import com.wkshop.actors.WikiWorker


object WorkerApp extends App {
  WikiWorker.main(Some(2552))
}
