package edu.uiuc.scala.examples

class Decorator(left: String, right: String) {
  def layout[A](x: A) = left + x.toString() + right
}

object FunTest extends Application {
  def apply(f: Int => String, v: Int) = f(v)
  val decorator = new Decorator("[", "]")
  println(apply(decorator.layout, 7))
}