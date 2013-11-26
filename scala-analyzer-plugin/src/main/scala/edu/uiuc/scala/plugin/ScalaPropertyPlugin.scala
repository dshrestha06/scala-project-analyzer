package edu.uiuc.scala.plugin

import java.util.HashSet
import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent
import scala.concurrent.Future

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
       * Takes in a tree block and recursively searches for a nested function declaration.
       */
      def checkForChildMethodDef(block: Block) {
        block.children.foreach { x =>
          if (x.isInstanceOf[DefDef])
            report.increment("Nested function")
          else if (x.isInstanceOf[Block])
            checkForChildMethodDef(x.asInstanceOf[Block])
        }
      }

      /**
       * Increment the report if there are any recursive calls in the method body
       */
      def findRecursiveMethodCalls(defDef: DefDef) {
    	  defDef.children.foreach { x => 
    	    if(x.isInstanceOf[Block] || x.isInstanceOf[If])
    	    	findRecursiveMethodCalls(x.asInstanceOf[Block], defDef)
    	      else 
    	        if(x.symbol == defDef.symbol) report.increment("Recursive Method Call")
    	  }
      }

      /**
       * Increment the report if there are any calls to parentDef method in the tree
       */
      def findRecursiveMethodCalls(tree: Tree, parentDef: DefDef) {
        tree.children.foreach { x =>
          if (x.symbol == parentDef.symbol) report.increment("Recursive Method Call")
          else
            findRecursiveMethodCalls(x, parentDef)
        }
      }
      
      /**
       * Increment if there are any inner class definition
       */
      def findNestedClasses(classDef: Tree) {
         classDef.children.foreach { x => 
    	    if(x.isInstanceOf[ClassDef]) report.increment("Nested Class")
    	  }
      }

      
      def apply(unit: CompilationUnit) {
        report.start(unit.toString)
        val abstractClassMap: HashSet[Int] = new HashSet[Int]()

        for (tree <- unit.body) {

          if (tree.isInstanceOf[ApplyToImplicitArgs]) {
            val implicitArgs = tree.asInstanceOf[ApplyToImplicitArgs]
            if (implicitArgs.symbol.name.toString() equals "future") report.increment("Future")
          }

          if (tree.symbol != null && tree.tpe != null 
              && (tree.symbol.toString() contains "trait") 
              && (tree.tpe.toString() contains "akka.actor")) 
            report.increment("Actor")

          //Anonymous function
          if (tree.isInstanceOf[Function] && tree.symbol.rawname.toString().equals("$anonfun"))
            report.increment("Anonymous Function")

          if (tree.isInstanceOf[ClassDef]) {
            report.increment("Class")

            val classDefTree: ClassDef = tree.asInstanceOf[ClassDef]

            if (classDefTree.mods.hasAbstractFlag) report.increment("Abstract Class")
            if (classDefTree.mods.isTrait) report.increment("Trait")
            if (classDefTree.mods.isCase) report.increment("Case Class")
            if (classDefTree.tparams.size > 0) report.increment("Generic Class")

            if (classDefTree.mods.isSealed) report.increment("Sealed Class")            
            findNestedClasses(classDefTree)
          }

          //requires packageobjects build phase
          if (tree.isInstanceOf[Match]) {
            val matchTree = tree.asInstanceOf[Match]
            report.increment("Match Pattern")
          }

          if (tree.isInstanceOf[DefDef]) {
            report.increment("Def")

            val defDefTree = tree.asInstanceOf[DefDef]
            if (defDefTree.mods.isOverride) report.increment("Override Method")
            if (defDefTree.symbol.annotations.mkString(",").contains("scala.annotation.tailrec")) report.increment("optimized tail recursion")
            if(defDefTree.mods.isDeferred) report.increment("Deferred Method")
            
            // if there are multiple parameters, it's a curry function
            if (defDefTree.vparamss.length > 1) report.increment("Curry Function")

            //if the tree is a type of DefDef search to see if its children has any DefDef declaration
            tree.children.foreach { x => if (x.isInstanceOf[Block]) checkForChildMethodDef(x.asInstanceOf[Block]) }

            //check for higher order function. function that takes in function as an argument
            tree.children.foreach { x =>
              if (x.isInstanceOf[ValDef]) {
                val argument = x.asInstanceOf[ValDef]
                //TODO: might need some extra check
                argument.children.foreach { y =>
                  if (y.symbol.toString() contains "Function") report.increment("Higher order function")
                }
              }
            }
            
            findRecursiveMethodCalls(defDefTree)
          }

          if (tree.isInstanceOf[MemberDef]) {
            val memberTree = tree.asInstanceOf[MemberDef]
            if (memberTree.symbol.isVal) report.increment("Member Value")
            if (memberTree.symbol.isVar) report.increment("Member Var")
            if (memberTree.symbol.isLazy) report.increment("Member Lazy Value")
          }

          if (tree.isInstanceOf[TypeDef])
            report.increment("Type")
          if (tree.isInstanceOf[CaseDef])
            report.increment("Case")
          if (tree.isInstanceOf[ValDef]) {
            report.increment("Value")

            //promise
            val valDef = tree.asInstanceOf[ValDef]
            if (valDef.rhs.toString() contains "scala.concurrent.`package`.promise") report.increment("Promise")

            // Another way of using curry functions
            if (valDef.rhs.toString() contains ".curried") report.increment("Curry Function")

          }
          
          if (tree.isInstanceOf[If]) report.increment("If Condition")

          if (tree.isInstanceOf[TypeApply]) {
            val valDef = tree.asInstanceOf[TypeApply]
            if (valDef.symbol.fullName contains "scala.collection.parallel") report.increment("Parallel collection")
          }

          if (tree.isInstanceOf[ModuleDef])
            report.increment("Module")
          if (tree.isInstanceOf[Star])
            report.increment("Star")

          if (!tree.freeTerms.isEmpty)
            println(tree.freeTerms.mkString(","))
          if (!tree.freeTypes.isEmpty)
            println(tree.freeTypes.mkString(","))
        }

        report.flush
      }
    }
  }
}
