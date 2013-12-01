package edu.uiuc.scala.examples

object Parallel {
  def main(args: Array[String]) {
    val list = (1 to 10000).toList;
    //not supported by 2.8.1
    //list.par.map(_ + 42);
  }
}
