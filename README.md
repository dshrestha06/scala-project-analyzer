scala-project-analyzer
======================

Set of tools to analyze scala projects

## Usage

Once you clone the project use mvn to compile it and build a jar. The jar includes a xml file called scalac-plugin.xml that
defines what plugins are available.

mvn clean compile assembly:single

Now, use the jar plugin to compile a scala file. There is a Test.scala file

scalac -Xplugin:target/scala-analyzer-plugin-0.0.1-SNAPSHOT-jar-with-dependencies.jar src/main/scala/edu/uiuc/scala/examples/Test.scala
