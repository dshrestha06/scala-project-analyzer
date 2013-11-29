package edu.uiuc.scala.plugin

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import java.io.FileWriter

object StatsAggregator {

  // map of feature usage and count for all projects
  val statsCounter = collection.mutable.Map[String, Int]()

  val files = collection.mutable.Set[String]()
  var totalNumberOfProjects: Int = 0
  var fileName: String = ""

  def countPerProject(text: String) {
    val split = text.split(",")
    val key = fileName + ", " + split(1).trim
    files += fileName + split(0)
    if (statsCounter.contains(key)) {
      statsCounter(key) += split(2).trim.toInt
    } else {
      statsCounter(key) = split(2).trim.toInt
    }
  }

  def countAllProjects(text: String) {
    val split = text.split(",")
    val key = split(1).trim
    if (statsCounter.contains(key)) {
      statsCounter(key) += split(2).trim.toInt
    } else {
      statsCounter(key) = split(2).trim.toInt
    }
  }

  def flush(file: File) {
    val p = new PrintWriter(new FileWriter(file, true))

    p.println(statsCounter.mkString("\n").replaceAll(" -> ", ", "))
    p.flush
    p.close
  }

  def printPerProject {
    val outputFile = new File("output/project.csv")
    outputFile.delete()
    for (file <- new File(System.getProperty("user.home") + "/output/").listFiles) {
      totalNumberOfProjects += 1
      fileName = file.getName
      statsCounter.clear
      Source.fromFile(file).getLines.filter(_.split(",").length > 1).foreach(countPerProject)
      flush(outputFile)
    }
  }

  def printAllProjects {
    val outputFile = new File("output/allprojects.csv")
    outputFile.delete()
    statsCounter.clear
    Source.fromFile(new File("output/project.csv")).getLines.filter(_.split(",").length > 1).foreach(countAllProjects)
    flush(outputFile)
  }

  def main(args: Array[String]) {
    new File("output").mkdir();
    printPerProject
    printAllProjects
    println("Total number of projects: " + totalNumberOfProjects)
    println("Total number of files: " + files.size)
  }

}