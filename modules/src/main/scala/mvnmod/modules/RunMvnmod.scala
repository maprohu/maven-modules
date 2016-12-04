package mvnmod.modules

import java.io.File

import mvnmod.builder.Module.ConfiguredModule
import mvnmod.builder.{Module, ModuleContainer, NamedModule, PlacedRoot}

import scala.collection.immutable._

object Place {

  val RootPath = Seq("..", "maven-modules")

}
/**
  * Created by pappmar on 29/08/2016.
  */
object RunMvnmod {

  val RootDir = new File(Place.RootPath.mkString("/"))

  val Roots = Seq[PlacedRoot](
    MvnmodModules.Root -> RootDir
  )

  val Modules = Seq[ConfiguredModule](
    MvnmodModules.Common,
    MvnmodModules.Modules,
    MvnmodModules.Builder,
    MvnmodModules.Generator
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
