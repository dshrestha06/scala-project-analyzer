package edu.uiuc.scala.examples

object Parallel {
  def main(args: Array[String]) {
    val list = (1 to 10000).toList;
    list.par.map(_ + 42);
  }
}
