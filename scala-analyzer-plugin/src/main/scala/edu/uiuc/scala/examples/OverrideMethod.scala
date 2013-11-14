package edu.uiuc.scala.examples

class Parent {
  def move() {
    println("moved slowly");
  }
}

class FastMover() extends Parent {
  override def move() {
    println("moved very fast");
  }
}
