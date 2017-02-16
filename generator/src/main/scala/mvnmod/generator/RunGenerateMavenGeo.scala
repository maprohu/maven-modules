package mvnmod.generator

import java.io.File
import java.net.URLEncoder

import org.jboss.shrinkwrap.resolver.api.maven.{Maven, ScopeType}
import sbt.io.IO

import scala.util.control.NonFatal

/**
  * Created by martonpapp on 28/08/16.
  */
object RunGenerateMavenGeo {

  val artifacts = Seq(
    "org.geotools:gt-render:jar:11.5",
    "org.geotools:gt-referencing:jar:11.5",
    "org.geotools:gt-coverage:jar:11.5",
    "org.geotools:gt-coverage-patched:jar:11.5",
    "org.geotools:gt-epsg-wkt:jar:11.5",
    "org.geotools:gt-epsg-hsql:jar:11.5",
    "org.geotools:gt-jdbc:jar:11.5"
  )

  val root = new File("../maven-modules/builder/src/main/scala/mvn")

  def process(canonical: String) : Unit = {
    val resolveds =
      Maven
        .configureResolver()
        .withRemoteRepo("geotools", "http://download.osgeo.org/webdav/geotools/", "default")
        .withRemoteRepo("boundles", "http://repo.boundlessgeo.com/main", "default")
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
