package edu.uiuc.scala.examples

class LazyVal {
  trait Foo { val foo: Foo }
  case class Fee extends Foo { lazy val foo = Faa() }
  case class Faa extends Foo { lazy val foo = Fee() }

  println(Fee().foo)
}