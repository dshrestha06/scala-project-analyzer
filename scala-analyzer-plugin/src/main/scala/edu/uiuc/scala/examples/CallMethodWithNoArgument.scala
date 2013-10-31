package edu.uiuc.scala.examples

class TestClass(xc: Int) {
  var x: Int = xc
  def value() :Int = {
     x+1
  }
}

object Main {
  def main(args: Array[String]) {
    var obj = new TestClass(3)
    println(obj.value)
    println("--")
    println(obj.value())
  }
}

