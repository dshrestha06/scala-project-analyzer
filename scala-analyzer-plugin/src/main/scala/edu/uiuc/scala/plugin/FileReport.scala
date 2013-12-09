package edu.uiuc.scala.plugin

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.HashMap
import scala.collection.JavaConversions._

class FileReport(fileName : String) {
	var source: String = null
	val map:HashMap[String, Int] =  new HashMap[String, Int]()
	
	
  def increment(key : String) {
		increment(key, 1)
	}

	def start(fileName: String) {
	  source = fileName
	  map.clear
	}
	
	def increment(key : String, count : Int) {
		if (!map.containsKey(key)) map.put(key, 0)
		map.put(key, count + map.get(key))
	}
	
	def flush() {
	  val f = new File("/home/ubuntu" + "/output/" + fileName)
	  val p = new PrintWriter(new FileWriter(f, true))
	  
	  //use java iterator to support older scala version
	 val iter = map.entrySet().iterator()
	  while(iter.hasNext) {
	    val entry = iter.next
	    p.println(List(source, entry.getKey, entry.getValue.toString).mkString(", "))
	  }
	  p.flush
	  p.close
	}
}