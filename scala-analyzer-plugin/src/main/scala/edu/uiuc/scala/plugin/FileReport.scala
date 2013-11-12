package edu.uiuc.scala.plugin

import java.io._

class FileReport(fileName : String) {
	def increment(key : String, source: String) {
		increment(key, source, 1)
	}

	def increment(key : String, source: String, count : Int) {
		printToFile(new File(System.getProperty( "user.home" ) + "/output/" + fileName))(p => {
		  p.println(List(source, key, count.toString).mkString(", "))
		})
	}

	def printToFile(f: java.io.File)(op: PrintWriter => Unit) {
	  val p = new PrintWriter(new FileWriter(f, true))
	  try { op(p) } finally { p.close() }
	}


}