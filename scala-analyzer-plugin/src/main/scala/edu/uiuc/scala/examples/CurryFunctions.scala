package edu.uiuc.scala.examples

object CurryFunctions {

  def fullsum(a: Int, b: Int) = a + b

  def sum(a: Int)(b: Int) = a + b

  def add(a: Int): Int => Int = b => a + b

  def main(args: Array[String]) {
    val f = sum(5)_
    println(f(10)) //15

    println(sum(10)(2)) //12

    val g = add(5)
    println(g(5)) //10

    val addCurried = (fullsum(_,_)).curried

    println(fullsum(1, 2)) // 3
    println(addCurried(2)(2)) // 4
  }
}