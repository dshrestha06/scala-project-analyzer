package edu.uiuc.scala.examples

import collection.parallel.mutable.ParTrieMap
import collection.parallel.ForkJoinTaskSupport
object TestingParallelMap {
  val length = sys.props("length").toInt
  val par = sys.props("par").toInt
  val partrie = ParTrieMap((0 until length) zip (0 until length): _*)
  
  partrie.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(par))
  
  def run = {
    partrie map {
      kv => kv
    }
  }
}

