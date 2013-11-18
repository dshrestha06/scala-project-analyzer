package edu.uiuc.scala.examples

object Closure {

  def foo(g: Int => Int) = {
    val n = 100
    g(1)
  }

  def main(args: Array[String]) {
    var n = 20
    val f = (x: Int) => x + n
    println(foo(f)) // 21
  }
}