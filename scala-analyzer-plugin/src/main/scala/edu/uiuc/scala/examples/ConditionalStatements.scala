package edu.uiuc.scala.examples

class ConditionalStatements {
  def main(args: Array[String]) {
    var x = 30;

    if (x == 10) {
      println("Value of X is 10");
    } else if (x == 20) {
      println("Value of X is 20");
    } else if (x == 30) {
      println("Value of X is 30");
    } else {
      println("This is else statement");
    }
  }
}