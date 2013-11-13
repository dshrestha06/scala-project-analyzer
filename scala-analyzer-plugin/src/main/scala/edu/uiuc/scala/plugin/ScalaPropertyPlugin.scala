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
            report.increment("Anonymous Function", unit.source.file.name)
            //Trait
          if (tree.isInstanceOf[ClassDef] && tree.symbol.toString().startsWith("trait"))
            report.increment("Trait", unit.source.file.name)
          if (tree.isInstanceOf[ClassDef] && tree.symbol.toString().startsWith("class"))
            report.increment("Class", unit.source.file.name)
          if (tree.isInstanceOf[DefDef])
            report.increment("Def", unit.source.file.name)
          if (tree.isInstanceOf[TypeDef])
            report.increment("Type", unit.source.file.name)
          if (tree.isInstanceOf[CaseDef])
            report.increment("Case", unit.source.file.name)
          if (tree.isInstanceOf[ValDef])
            report.increment("Value", unit.source.file.name)
          if (tree.isInstanceOf[ModuleDef])
            report.increment("Module", unit.source.file.name)
          if (tree.isInstanceOf[Star])
            report.increment("Star", unit.source.file.name)

         }
        
      }
    }
  }
}
