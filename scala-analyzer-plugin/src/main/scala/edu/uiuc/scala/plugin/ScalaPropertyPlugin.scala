package edu.uiuc.scala.plugin

import java.util.HashSet

import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent

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
   
    //val runsAfter = List[String]("packageobjects");
     val runsAfter = List[String]("refchecks");

    val phaseName = ScalaPropertyPlugin.this.name
    def newPhase(_prev: Phase) = new ScalaPropertyPluginPhase(_prev)
    val report = new FileReport(new java.io.File(".").getAbsoluteFile.getParentFile.getName)

    class ScalaPropertyPluginPhase(prev: Phase) extends StdPhase(prev) {
    	override def name = ScalaPropertyPlugin.this.name
    	
      /**
       * Takes in a tree block and recursively searches for a nested funciton declaration. 
       */
      def checkForChildMethodDef(block: Block) {
    	  block.children.foreach { x => 
    	    if(x.isInstanceOf[DefDef]) 
    	    	report.increment("Nested function")
    	    else if(x.isInstanceOf[Block]) 
    	      checkForChildMethodDef(x.asInstanceOf[Block])}
      }
    	
      def apply(unit: CompilationUnit) {
    	report.start(unit.toString)
    	val abstractClassMap:HashSet[Int] = new HashSet[Int]()
    	
    	  for (tree <- unit.body) {
    	  
    	  //if the tree is a type of DefDef search to see if its children has any DefDef declaration
    	  if(tree.isInstanceOf[DefDef]) {
    		  tree.children.foreach { x => if(x.isInstanceOf[Block]) checkForChildMethodDef(x.asInstanceOf[Block]) }
    	  }
    	   
    	    //Anonymous function
          if (tree.isInstanceOf[Function] && tree.symbol.rawname.toString().equals("$anonfun"))
        	  report.increment("Anonymous Function")

          if (tree.isInstanceOf[ClassDef]) {
            report.increment("Class")
            
            val classDefTree : ClassDef = tree.asInstanceOf[ClassDef]
            
            if(classDefTree.mods.hasAbstractFlag) report.increment("Abstract Class")
            if(classDefTree.mods.isTrait) report.increment("Trait")
            if(classDefTree.mods.isCase) report.increment("Case Class")
            
            if(classDefTree.tparams.size > 0) report.increment("Generic Class")
          }
    	
         //requires packageobjects build phase
    	 if (tree.isInstanceOf[Match]) {
    	   val matchTree = tree.asInstanceOf[Match]
    	    report.increment("Match Pattern")
    	 }
    	 
          if (tree.isInstanceOf[DefDef]) {
            report.increment("Def")
            
            val defDefTree = tree.asInstanceOf[DefDef]
            if(defDefTree.mods.isOverride) report.increment("Override Method")
          }
          
          if (tree.isInstanceOf[TypeDef])
            report.increment("Type")
          if (tree.isInstanceOf[CaseDef])
            report.increment("Case")
          if (tree.isInstanceOf[ValDef])
            report.increment("Value")
          if (tree.isInstanceOf[ModuleDef])
            report.increment("Module")
          if (tree.isInstanceOf[Star])
            report.increment("Star")
         }
    	
    	report.flush
      }
    }
  }
}
