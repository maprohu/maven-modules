package mvnmod.modules

import mvnmod.builder.{MavenCentralModule, RootModuleContainer, ScalaModule}


/**
  * Created by martonpapp on 29/08/16.
  */

object MvnmodModules {
  implicit val Root = RootModuleContainer("mvnmod")


  object Common extends ScalaModule(
    "common",
    mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
    mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
    mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.8.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
    mvn.`org.apache.maven.shared:maven-invoker:2.2`
  ) {
    val Snapshot = snapshot

    object R6 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )
    object R5 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )
    object R4 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )
    object R3 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )
    object R2 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )
    object R1 extends Release(
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`
    )

  }
  object Builder extends ScalaModule(
    "builder",
    Common.Snapshot
  ) {
    val Snapshot = snapshot

    object R10 extends Release(
      Common.R6
    )
    object R9 extends Release(
      Common.R5
    )
    object R8 extends Release(
      Common.R4
    )
    object R7 extends Release(
      Common.R3
    )
    object R6 extends Release(
      Common.R2
    )
    object R5 extends Release(
      Common.R2
    )
    object R4 extends Release(
      Common.R1
    )

    object R3 extends Release(
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`
    )
    object R2 extends Release(
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`
    )
    object R1 extends Release(
      mvn.`org.eclipse.aether:aether-util:jar:1.1.0`,
      mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
      mvn.`org.scala-lang.modules:scala-xml_2.11:jar:1.0.6`,
      mvn.`com.lihaoyi:ammonite-ops_2.11:jar:0.7.8`,
      mvn.`org.apache.maven.shared:maven-invoker:2.2`,
      mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`
    )


  }

  object Modules extends ScalaModule(
    "modules",
    Builder.Snapshot
  ) {
    val Snapshot = snapshot

    object R9 extends Release(
      Builder.R10
    )
    object R8 extends Release(
      Builder.R9
    )
    object R7 extends Release(
      Builder.R8
    )
    object R6 extends Release(
      Builder.R7
    )
    object R5 extends Release(
      Builder.R6
    )
    object R4 extends Release(
      Builder.R5
    )
    object R3 extends Release(
      Builder.R4
    )
    object R2 extends Release(
      Builder.R3
    )
    object R1 extends Release(
      Builder.R2
    )
  }

  object Generator extends ScalaModule(
    "generator",
    mvn.`org.scala-sbt:io_2.11:jar:1.0.0-M3`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:jar:2.2.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven:jar:2.2.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-archive:jar:2.2.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:jar:2.2.2`,
    mvn.`org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-archive:jar:2.2.2`
  )



}
