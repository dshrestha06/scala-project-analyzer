package edu.uiuc.scala.plugin

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import scala.io.Source
import scala.collection.mutable.SortedSet

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
      files.clear
      statsCounter.clear
      Source.fromFile(file).getLines.filter(_.split(",").length > 1).foreach(countPerProject)
      statsCounter(fileName + ", Total number of Scala files") = files.size
      flush(outputFile)
    }
  }
  
  
  def printPerProjectCompact {
    val file = new File("output/project-compact.csv")
    file.delete()
    val p = new PrintWriter(new FileWriter(file, true))
	  
    val stats = collection.mutable.Map[String, collection.mutable.Map[String, String]]()
    var features = collection.mutable.HashSet[String]()
    var projectName = ""
    
    Source.fromFile(new File("output/project.csv")).getLines.filter(_.split(",").length > 1).foreach(line => {
      val split = line.split(",")
      val projectName= split(0)
      if (!stats.contains(projectName)) {
        val featuresMap = collection.mutable.Map[String, String]()
        stats.put(projectName, featuresMap)
      }
      stats.get(projectName).get.put(split(1), split(2))
      features.add(split(1))    
    });
    
    val sortedFeatures = features.to[SortedSet]
    p.print("project,")
    p.println(sortedFeatures.mkString(","))
    
    if(stats.contains(".DS_Store")) stats.remove(".DS_Store")
    stats.keySet.to[SortedSet].foreach(project => {
      p.print(project)
      p.print(",")
      
      val projectFeatureMap = stats.get(project).get
      
      sortedFeatures.foreach(feature => {
        if (projectFeatureMap.contains(feature)) {
          p.print(projectFeatureMap.get(feature).get)
          p.print(",")
        } else {
          p.print("0,")
        }
      });
      p.println("")
    });
    
    p.flush()
     
  }

  
  def printAllProjects {
    val outputFile = new File("output/allprojects.csv")
    outputFile.delete()
    statsCounter.clear
    Source.fromFile(new File("output/project.csv")).getLines.filter(_.split(",").length > 1).foreach(countAllProjects)
    statsCounter("Total number of Scala projects") = totalNumberOfProjects
    flush(outputFile)
  }

  
  def main(args: Array[String]) {
    new File("output").mkdir();
    printPerProject
    printAllProjects
    printPerProjectCompact
  }

}