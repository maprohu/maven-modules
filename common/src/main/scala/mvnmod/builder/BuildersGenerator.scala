package mvnmod.builder

import java.io.File

import mvnmod.builder.Module.ConfiguredModule

/**
  * Created by maprohu on 04-12-2016.
  */
object BuildersGenerator {

  def run(
    placedRoot: PlacedRoot,
    declaredModules: collection.Seq[ConfiguredModule]
  ) = {
    val scalaDir = new File(placedRoot.rootDir, s"${Module.BuildersModuleName}/src/main/scala")
    val packageElements =
      placedRoot.rootContainer.path :+ Module.BuildersModuleName

    val packageDir = new File(scalaDir, packageElements.mkString("/"))
    packageDir.mkdirs()

    val scalaFile = new File(packageDir, "builders.scala")

    val modulesUnderRoot =
      declaredModules
        .filter(_.module.container.root == placedRoot.rootContainer)


    val containersPaths =
      modulesUnderRoot
        .flatMap(_.module.container.toSeq)
        .distinct
        .map({ c =>
          c.path
        })

    val modulesPath =
      placedRoot.rootContainer.path.:+(Module.ModulesModuleName).mkString(".")

    val modulesPaths =
      modulesUnderRoot
        .map(_.module.path)

    val buildersCode =
      (containersPaths ++ modulesPaths)
        .map({ path =>
          s"""object build_${path.mkString("_")} extends ${classOf[ModuleBuilder].getName}(
             |  ${modulesPath}.Place.RootPath,
             |  "${("." +: path.tail).mkString("/")}"
             |)
           """.stripMargin
        })
        .mkString("\n")

    import ammonite.ops._
    write.over(
      Path(scalaFile, pwd),
      s"""package ${packageElements.mkString(".")}
         |
         |${buildersCode}
       """.stripMargin
    )



  }

}

class ModuleBuilder(
  rootPath: Seq[String],
  pathFromRoot: String
) {
  def main(args: Array[String]): Unit = {
    MavenTools
      .runMavenProject(
        new File(s"${(rootPath :+ pathFromRoot).mkString("/")}"),
        Seq("install")
      )
  }
}
