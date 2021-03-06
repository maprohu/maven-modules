package mvnmod.modules

import mvnmod.builder.{ModuleRelease, NamedModule}

/**
  * Created by pappmar on 13/10/2016.
  */
object RunMvnmodRelease {

  val Releases = Seq[NamedModule#Release](

    MvnmodModules.Modules.R7,
    MvnmodModules.Builder.R8,
    MvnmodModules.Common.R4,

    MvnmodModules.Modules.R6,
    MvnmodModules.Builder.R7,
    MvnmodModules.Common.R3,

    MvnmodModules.Modules.R5,
    MvnmodModules.Builder.R6,

    MvnmodModules.Modules.R4,
    MvnmodModules.Builder.R5,
    MvnmodModules.Common.R2,

    MvnmodModules.Modules.R3,
    MvnmodModules.Builder.R4,
    MvnmodModules.Common.R1,

    MvnmodModules.Modules.R2,
    MvnmodModules.Builder.R3,

    MvnmodModules.Modules.R1,
    MvnmodModules.Builder.R2,
    MvnmodModules.Builder.R1
  )

  def main(args: Array[String]): Unit = {

    Releases
      .reverse
      .foreach { r =>
        println(r.getClass.getName)

        ModuleRelease.release(
          RunMvnmod.Roots,
          r
        )
      }

  }

}

object RunMvnmodReleaseInstall {
  def main(args: Array[String]): Unit = {
    RunMvnmodRelease
      .Releases
      .take(5)
      .reverse
      .foreach { r =>
        println(r.getClass.getName)

        ModuleRelease.installRelease(
          RunMvnmod.Roots,
          r
        )
      }

  }
}
