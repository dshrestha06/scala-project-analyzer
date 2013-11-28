package edu.uiuc.scala.plugin

import scala.io.Source
import java.io.File

object StatsAggregator {

  val statsCounter = collection.mutable.Map[String, Int]()
  val files = collection.mutable.Set[String]()
  var totalNumberOfProjects:Int = 0
  var fileName:String = ""

  def count(text: String) {
    val split = text.split(",")
    val key = split(1).trim
    files += fileName + split(0)
    if (statsCounter.contains(key)) {
      statsCounter(key) += split(2).trim.toInt
    } else {
      statsCounter(key) = split(2).trim.toInt
    }
  }

  def main(args: Array[String]) {
    for (file <- new File(System.getProperty("user.home") + "/output/").listFiles) {
      totalNumberOfProjects += 1
      fileName = file.getName
      Source.fromFile(file).getLines.filter(_.split(",").length > 1).foreach(count)
    }
    println("Total number of projects: " + totalNumberOfProjects)
    println("Total number of files: " + files.size)
    println("Feature Usage:")
    println(statsCounter.mkString("\n").replaceAll(" -> ", ": "))
  }

}