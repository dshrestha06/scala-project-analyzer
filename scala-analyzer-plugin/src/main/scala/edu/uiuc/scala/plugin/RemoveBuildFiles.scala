package edu.uiuc.scala.plugin

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import scala.io.Source
import scala.collection.mutable.SortedSet

object RemoveBuildFiles {

  def removeBuildFiles {
    for (file <- new File("/home/ubuntu/output").listFiles) {
      if (!file.getName().equals(".DS_Store")) {
        var fileName = file.getName
        var count = 0;
        var buildCount = 0;
        Source.fromFile(file).getLines.filter(_.split(",").length > 1).foreach(line => {

          if (line.trim().length() > 0) {
            count = count + 1;

            if (line.toLowerCase() contains "build") buildCount = buildCount + 1
          }

        });

        if (buildCount == count) {
          println("removing " + file.getName +" with lines " + count)
          file.delete()
        }
      }
    }
  }

  def main(args: Array[String]) {
    removeBuildFiles
  }
}