package mvnmod.modules

import java.io.File

import maven.modules.builder.Module.ConfiguredModule
import maven.modules.builder.{Module, ModuleContainer, NamedModule, PlacedRoot}

import scala.collection.immutable._

/**
  * Created by pappmar on 29/08/2016.
  */
object RunMvnmod {

  val RootDir = new File("../maven-modules")

  val Roots = Seq[PlacedRoot](
    MvnmodModules.Root -> RootDir
  )

  val Modules = Seq[ConfiguredModule](
    MvnmodModules.Modules
  )

  def main(args: Array[String]): Unit = {

    Module.generate(
      Roots,
      Modules
    )

  }

  def projectDir(module: ModuleContainer) : File = {
    module.path.tail.foldLeft(RootDir)(new File(_, _))
  }

  def projectDir(module: NamedModule) : File = {
    new File(projectDir(module.container), module.name)
  }

}
