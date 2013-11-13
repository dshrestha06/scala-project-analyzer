package edu.uiuc.scala.plugin

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

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
    val report = new FileReport(new java.io.File(".").getAbsoluteFile.getParentFile.getName)

    class ScalaPropertyPluginPhase(prev: Phase) extends StdPhase(prev) {
    	override def name = ScalaPropertyPlugin.this.name
      def apply(unit: CompilationUnit) {
        for (tree <- unit.body) {

	//Anonymous function
          if (tree.isInstanceOf[Function] && tree.symbol.rawname.toString().equals("$anonfun"))
            println("Found annonymous function:" + tree.symbol.toString())
            //Trait
          if (tree.isInstanceOf[ClassDef] && tree.symbol.toString().startsWith("trait"))
            println("Trait found:" + tree.symbol.toString())
          if (tree.isInstanceOf[ClassDef] && tree.symbol.toString().startsWith("class"))
            println("Class found:" + tree.symbol.toString())
          if (tree.isInstanceOf[DefDef])
            println("Def found:" + tree.symbol.toString())
          if (tree.isInstanceOf[TypeDef])
            println("Type found:" + tree.symbol.toString())
          if (tree.isInstanceOf[CaseDef])
            println("Case found:" + tree)
          if (tree.isInstanceOf[ValDef])
            println("Value found:" + tree.symbol.toString())
          if (tree.isInstanceOf[ModuleDef])
            println("Module found:" + tree.symbol.toString())
          if (tree.isInstanceOf[Star])
            println("Star found:" + tree.symbol.toString())

         }
        
      }
    }
  }
}
