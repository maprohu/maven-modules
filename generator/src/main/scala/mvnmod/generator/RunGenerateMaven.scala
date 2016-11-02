package mvnmod.generator

import java.io.File
import java.net.URLEncoder

import org.jboss.shrinkwrap.resolver.api.maven.{Maven, ScopeType}
import sbt.io.IO

import scala.util.control.NonFatal

/**
  * Created by martonpapp on 28/08/16.
  */
object RunGenerateMaven {

  val artifacts = Seq(
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:jar:2.2.2",
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi:jar:2.2.2",
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:2.2.2",
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven:jar:2.2.2",
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:jar:2.2.2",
    "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-archive:jar:2.2.2",
    "me.chrons:boopickle_2.11:jar:1.2.4",
    "com.jcraft:jsch:jar:0.1.54",
    "com.typesafe.akka:akka-stream_2.11:jar:2.4.9",
    "com.typesafe.akka:akka-stream_2.11:jar:2.4.11",
    "com.typesafe.akka:akka-stream_2.11:jar:2.4.12",
    "com.typesafe.akka:akka-slf4j_2.11:jar:2.3.15",
    "com.typesafe.akka:akka-slf4j_2.11:jar:2.4.11",
    "com.typesafe.akka:akka-slf4j_2.11:jar:2.4.12",
    "javax.servlet:servlet-api:jar:2.5",
    "javax.servlet:javax.servlet-api:jar:3.1.0",
    "org.scala-lang:scala-library:jar:2.11.8",
    "com.lihaoyi:scalarx_2.11:jar:0.3.1",
    "commons-net:commons-net:jar:3.5",
    "commons-io:commons-io:jar:2.5",
    "commons-codec:commons-codec:jar:1.10",
//    "jartree:jartree-api:jar:1.0.0-SNAPSHOT",
//    "jartree:jartree-impl:jar:1.0.0-SNAPSHOT",
    "org.slf4j:slf4j-api:jar:1.7.21",
    "org.slf4j:slf4j-simple:jar:1.7.21",
    "com.typesafe.scala-logging:scala-logging_2.11:jar:3.4.0",
    "com.typesafe.akka:akka-stream-experimental_2.11:jar:2.0.4",
    "com.lihaoyi:upickle_2.11:jar:0.4.2",
    "org.scala-sbt:io_2.11:jar:1.0.0-M6",
    "org.scala-sbt:io_2.11:jar:1.0.0-M3",
    "com.jsuereth:scala-arm_2.11:jar:1.4",
    "org.scala-lang.modules:scala-pickling_2.11:jar:0.10.1",
    "com.typesafe.akka:akka-http-experimental_2.11:jar:2.4.9",
    "com.typesafe.akka:akka-http-experimental_2.11:jar:2.4.11",
    "com.typesafe.akka:akka-http-experimental_2.11:jar:2.0.4",
    "com.typesafe.akka:akka-http-experimental_2.11:jar:2.0.5",
    "com.typesafe.akka:akka-http-xml-experimental_2.11:jar:2.0.5",
    "org.webjars.bower:vis:jar:4.16.1",
    "org.scala-js:scalajs-library_2.11:jar:0.6.12",
    "org.scala-js:scalajs-dom_sjs0.6_2.11:jar:0.9.1",
    "com.github.wendykierp:JTransforms:jar:3.1",
    "org.scala-lang.modules:scala-swing_2.11:jar:2.0.0-M2",
    "io.monix:monix_2.11:jar:2.0.2",
    "io.monix:monix_2.11:jar:2.0.4",
    "io.monix:monix_2.11:jar:2.0.5",
    "de.heikoseeberger:akka-http-json4s_2.11:jar:1.9.0",
    "org.json4s:json4s-native_2.11:jar:3.4.0",
//    "emsa:wupdata-common-shared:jar:1.0.4-SNAPSHOT",
//    "emsa:wupdata-core:jar:1.0.5-SNAPSHOT",
    "com.github.nscala-time:nscala-time_2.11:jar:2.12.0",
    "com.github.nscala-time:nscala-time_2.11:jar:2.14.0",
    "io.github.lukehutch:fast-classpath-scanner:jar:2.0.3",
    "org.osgi:org.osgi.core:jar:5.0.0",
    "javax.jms:jms-api:jar:1.1-rev-1",
    "com.oracle:wljmxclient:jar:10.3.6.0",
    "com.oracle:wlfullclient:jar:10.3.6.0",
    "com.oracle:wlfullclient:jar:12.1.3.0",
    "com.oracle:wlthint3client:jar:10.3.6.0",
    "com.lihaoyi:ammonite-ops_2.11:jar:0.7.7",
    "com.lihaoyi:ammonite-ops_2.11:jar:0.7.8",
    "org.scala-lang.modules:scala-xml_2.11:jar:1.0.6",
    "com.sun.xml.bind:jaxb-xjc:jar:2.2.11",
    "com.sun.xml.bind:jaxb-impl:jar:2.2.11",
    "com.sun.xml.bind:jaxb-core:jar:2.2.11",
    "com.typesafe.slick:slick_2.11:jar:3.1.1",
    "mysql:mysql-connector-java:jar:6.0.4",
    "com.typesafe.slick:slick-codegen_2.11:jar:3.1.1",
    "com.typesafe.slick:slick-hikaricp_2.11:jar:3.1.1",
    "mysql:mysql-connector-java:jar:5.1.39",
    "com.vividsolutions:jts-io:jar:1.14.0",
    "com.h2database:h2:jar:1.4.192",
    "com.github.tototoshi:slick-joda-mapper_2.11:jar:2.2.0",
    "joda-time:joda-time:jar:2.9.4",
    "org.joda:joda-convert:jar:1.8.1",
    "com.badlogicgames.gdx:gdx-backend-lwjgl:jar:1.9.4",
    "com.badlogicgames.gdx:gdx-platform:jar:natives-desktop:1.9.4",
    "org.jgrapht:jgrapht-core:jar:1.0.0",
    "com.github.yannrichet:JMathPlot:jar:1.0.1",
    "ch.qos.logback:logback-classic:jar:1.1.7",
//    "osgi6:osgi6-api:jar:1.0.10",
    "org.scala-lang:scala-compiler:jar:2.11.8",
    "org.apache.maven.shared:maven-invoker:2.2",
    "emsa.ssn:ssn-vdm-support:jar:1.0.5.2",
    "emsa.ssn:ssn-spm-domain:jar:1.0.5.2",
    "emsa.ssn:ssn-vdm-support:jar:1.1.0.1",
    "emsa.ssn:ssn-spm-domain:jar:1.1.0.1",
    "emsa.ssn:ssn-ais:jar:1.0.5.2",
    "emsa.ssn:ssn-ais:jar:1.1.0.1",
    "it.acsys.imdate:imdate-ship-types:jar:1.0.2",
    "com.google.code.gson:gson:jar:2.7",
    "org.springframework:spring-messaging:jar:4.3.3.RELEASE",
    "com.google.guava:guava:jar:19.0",
    "org.springframework:spring-core:jar:3.2.17.RELEASE",
    "org.springframework.integration:spring-integration-core:jar:4.3.2.RELEASE",
    "org.springframework.integration:spring-integration-core:jar:4.3.4.RELEASE",
    "com.fasterxml.jackson.core:jackson-databind:jar:2.7.4",
    "javax.json:javax.json-api:jar:1.0",
    "org.glassfish:javax.json:jar:1.0.4",
    "org.apache.tomcat:tomcat-catalina:jar:8.5.5",
    "org.springframework:spring-test:jar:3.2.17.RELEASE",
    "org.apache.activemq:activemq-broker:jar:5.14.1",
    "org.springframework:spring-oxm:jar:4.3.3.RELEASE",
    "com.google.android:android:jar:4.1.1.4",
    "com.lihaoyi:scalatex-site_2.11:jar:0.3.6",
    "org.zeroturnaround:zt-zip:jar:1.9",
    "com.github.scopt:scopt_2.11:jar:3.5.0",
    "org.hid4java:hid4java:jar:0.4.0",
    "libmatthew-debug-java:hexdump:jar:0.2",
    "libmatthew-debug-java:debug-enable:jar:1.1",
    "libmatthew-debug-java:debug-disable:jar:1.1",
    "libunix-java:unix:jar:0.5",
    "libdbus-java:dbus:jar:2.8",
    "libdbus-java:dbus-bin:jar:2.8",
    "org.orbisgis:h2spatial:jar:1.2.4",
    "org.orbisgis:h2spatial-ext:jar:1.2.4",
    "commons-dbcp:commons-dbcp:jar:1.4",
    "net.sf.bluecove:bluecove:jar:2.1.0",
    "net.sf.bluecove:bluecove-gpl:jar:2.1.0",
    "com.lihaoyi:autowire_2.11:jar:0.2.5",
    "com.github.maprohu:scalajs-o3d_sjs0.6_2.11:jar:0.1.3",
    "com.eed3si9n:treehugger_2.11:jar:0.4.1",
    "com.jsyn:jsyn:jar:16.7.6",
    "org.eclipse.aether:aether-util:jar:1.1.0",
    "org.iq80.leveldb:leveldb:jar:0.9",
    "org.fusesource.leveldbjni:leveldbjni-all:jar:1.8",
    "org.fusesource.leveldbjni:leveldbjni-linux64:jar:1.8"





  //    "android-api:android-api:jar:22",
//    "org.macroid:macroid_2.11:aar:2.0.0-M5"
  )

  val root = new File("../maven-modules/poms/src/main/scala/mvn")

  def process(canonical: String) : Unit = {
    val resolveds =
      Maven
        .resolver()
        .resolve(canonical)
        .withTransitivity()
        .asResolvedArtifact()
        .toSeq


    val resolved +: resolvedDeps = resolveds

    val dir = new File(
      root,
      s"${resolved.getCoordinate.getGroupId.replace('.', '/')}"
    )
    dir.mkdirs()

    val fileName = s"${URLEncoder.encode(canonical, "UTF-8")}.scala"
    val file = new File(dir, fileName)

    val deps = resolvedDeps.filter(d => d.getScope != ScopeType.TEST && !d.isOptional)

    if (!file.exists()) {

      val canonicalArg = s""""${canonical}""""
      val depsArgs = deps.map(d => s"`${d.getCoordinate.toCanonicalForm}`")

      val args =
        (canonicalArg +: depsArgs)
          .map(a => s"  ${a}")
          .mkString(",\n")


      val content =
        s"""
           |package mvn
           |
           |object `${canonical}` extends _root_.maven.modules.utils.MavenCentralModule(
           |${args}
           |)
         """.stripMargin

      IO.write(
        file,
        content
      )

    }

    deps.foreach { dep =>
      process(dep.getCoordinate.toCanonicalForm)
    }


  }

  def main(args: Array[String]): Unit = {
    root.mkdirs()
//    IO.delete(root)
//    root.mkdirs()




    artifacts.foreach({ canonical =>
      try {
        process(canonical)
      } catch {
        case NonFatal(ex) =>
          ex.printStackTrace()
      }
    })

  }

}
