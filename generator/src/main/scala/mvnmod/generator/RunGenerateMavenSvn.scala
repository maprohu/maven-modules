package mvnmod.generator

import java.io.File
import java.net.URLEncoder

import org.jboss.shrinkwrap.resolver.api.maven.{Maven, ScopeType}
import sbt.io.IO

import scala.util.control.NonFatal

/**
  * Created by martonpapp on 28/08/16.
  */
object RunGenerateMavenSvn {

  val artifacts = Seq(
    "org.tmatesoft.svnkit:svnkit:jar:1.8.14"
  )

  val root = new File("../maven-modules/builder/src/main/scala/mvn")

  def process(canonical: String) : Unit = {
    val resolveds =
      Maven
        .configureResolver()
        .withRemoteRepo("svn", "http://maven.tmatesoft.com/content/repositories/releases/", "default")
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
           |object `${canonical}` extends _root_.mvnmod.builder.GeotoolsMoule(
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
