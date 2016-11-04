package mvnmod.modules

import maven.modules.builder.{ModuleRelease, NamedModule}

/**
  * Created by pappmar on 13/10/2016.
  */
object RunMvnmodRelease {

  val Releases = Seq[NamedModule#Release](
    MvnmodModules.Poms.R1









  )

  def main(args: Array[String]): Unit = {

    Releases.foreach { r =>
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
    RunMvnmodRelease.Releases.foreach { r =>
      println(r.getClass.getName)

      ModuleRelease.installRelease(
        RunMvnmod.Roots,
        r
      )
    }

  }
}
