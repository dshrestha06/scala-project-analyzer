package edu.uiuc.scala.plugin

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import scala.reflect.internal.Phase
/**
 * Copied from scala compiler plugin example.
 */
class ScalaPropertyPlugin(val global: Global) extends Plugin {
  import global._

  val name = "ScalaPropertyPlugin"
  val description = "checks for various characteristics of the program"
  val components = List[PluginComponent](Component)

  private object Component extends PluginComponent {
    val global: ScalaPropertyPlugin.this.global.type = ScalaPropertyPlugin.this.global
    val runsAfter = List[String]("refchecks");

    val phaseName = ScalaPropertyPlugin.this.name
    def newPhase(_prev: Phase) = new ScalaPropertyPluginPhase(_prev)

    class ScalaPropertyPluginPhase(prev: Phase) extends StdPhase(prev) {
      override def name = ScalaPropertyPlugin.this.name
      def apply(unit: CompilationUnit) {
        //Anonymous function
        for ( tree <- unit.body;
        if tree.isInstanceOf[Function] && tree.symbol.rawname.toString().equals("$anonfun")) {
        		println("Found annonymous function:" + tree)
          }

        //traits
        for (tree <- unit.body;
          if tree.isInstanceOf[ClassDef] && tree.symbol.toString().startsWith("trait")) {
            println("Trait found:" + tree)
        }
      }
    }
  }
}