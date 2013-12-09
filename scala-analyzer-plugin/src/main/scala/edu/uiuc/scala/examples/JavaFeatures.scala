package edu.uiuc.scala.examples

import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong


class JavaFeatures {
  def javaFeatures() {
    //1 java list
    val list = new ArrayList[String]()
    
    //1 java map
    val map = new HashMap[String, String]()
    
    val atomicInt = new AtomicInteger();
    val atomicLong = new AtomicLong();
    
    //1 scala mutable list
    val mutableList = new scala.collection.mutable.LinkedList[String]()
    
    //1 scala mutable map
    val mutableHashMap = new scala.collection.mutable.HashMap[String, String]()
   
    //1 scala immutable list
    val immutableList = List("s")
    println(immutableList.getClass())
 
    //2 scala immutable map
    val immutableMap = Map(3 -> 0.6, 1 ->0.5)
    val immutableMap1 = new scala.collection.mutable.HashMap[String, String]().toMap
  }
}

