package mvnmod.builder

import java.io.File

import ammonite.ops._
import sbt.io.IO

import scala.xml.PrettyPrinter

/**
  * Created by pappmar on 26/10/2016.
  */
object Delivery {

  val skips = Set(
    "target",
    ".idea",
    ".git"
  )

  def copySource(
    from: Path,
    to: Path
  ) : Unit = {
    val target = to / from.name
    if (from.isFile) {
      cp(from, target)
    } else if (from.isDir) {
      if (!skips.contains(from.name)) {
        mkdir(target)
        ls(from).foreach(f => copySource(f, target))
      }
    }
  }

  def run(
    name: String,
    version: String,
    what: Seq[Module],
    roots: Seq[(RootModuleContainer, File)],
    where: File,
    firstModules: Seq[String],
    lastModules: Seq[String]
  ) = {
    println(s"delivering ${name} - ${version} to ${where}")
    val rootMap = roots.toMap

    val modules =
      what
        .flatMap({ nm =>
          val m = nm

          (m.depsTransitive :+ m)
            .flatMap(_.source)
        })
        .distinct

//    modules
//      .map(_.container.root)
//      .distinct
//      .foreach({ rmc =>
//        copySource(
//          Path(rootMap(rmc).getAbsoluteFile),
//          Path(where.getAbsoluteFile)
//        )
//      })

    val modulePaths =
      modules
        .map({ m =>
          val root = rootMap(m.container.root)
          val pathFromRoot = m.pathFromRoot.mkString("/")
          val sourceFile = new File(root, pathFromRoot)
          val targetDir = new File(new File(where, root.getName), pathFromRoot).getParentFile

          copySource(
            Path(sourceFile.getCanonicalFile.getAbsoluteFile),
            Path(targetDir.getCanonicalFile.getAbsoluteFile)
          )

          (root.getName +: m.pathFromRoot).mkString("/")
        })


    val pp = new PrettyPrinter(1000, 2)
    val pom =
      <project xmlns="http://maven.apache.org/POM/4.0.0"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>{name}</groupId>
        <artifactId>{name}</artifactId>
        <version>{version}</version>
        <packaging>pom</packaging>
        <modules>
          {
          firstModules.map(m => <module>{m}</module>) ++
          modulePaths.map({ m =>
            <module>{m}</module>
          }) ++
          lastModules.map(m => <module>{m}</module>)
          }
        </modules>
      </project>

    IO.write(
      new File(where, "pom.xml"),
      pp.format(pom)
    )

//    val productDir = new File(where, "product")
//    productDir.mkdirs()
//
//    val productPom =
//      <project xmlns="http://maven.apache.org/POM/4.0.0"
//               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//               xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
//        <modelVersion>4.0.0</modelVersion>
//
//        <groupId>{name}</groupId>
//        <artifactId>{name}-product</artifactId>
//        <version>{version}</version>
//        <packaging>jar</packaging>
//        <dependencies>
//          {
//          what.map({ w =>
//            w.pomDependency
//          })
//          }
//        </dependencies>
//        {product}
//      </project>
//
//    IO.write(
//      new File(productDir, "pom.xml"),
//      pp.format(productPom)
//    )

  }

  def listSnapshots(
    what: Seq[NamedModule]
  ) = {
    println(
      what
        .flatMap(_.asModule.toSeq)
        .map(_.version)
        .distinct
        .filter(_.isSnapshot)
        .map(_.toString)
        .sorted
        .mkString("\n")
    )
  }

}
