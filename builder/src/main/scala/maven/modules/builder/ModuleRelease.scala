package maven.modules.builder

import java.io.File

import scala.collection.immutable._
import ammonite.ops._

/**
  * Created by pappmar on 13/10/2016.
  */
object ModuleRelease {

  val ReleasesDirName = "releases"

  def name(n: String) : Path => Boolean = _.name == n
  val DontCopyFiles = Seq[(Path => Boolean)](
    name(ReleasesDirName),
    name("pom.xml"),
    name("target")
  )

  def copyProject(
    from: Path,
    to: Path
  ) = {
    mkdir! to

    ls! from |? (f => !DontCopyFiles.exists(_(f))) | (f => cp(f, to / f.name))
  }

  def release(
    roots: Seq[PlacedRoot],
    r: maven.modules.builder.NamedModule#Release
  ) = {
    val placeLookup : Map[RootModuleContainer, File] =
      roots
        .map(p => p.rootContainer -> p.rootDir)
        .toMap

    val rootDir = Path.apply(placeLookup(r.container.root).getAbsoluteFile.toPath)

    val snapshotDir = Path(
      r
       .path
        .tail
        .mkString("/"),
      rootDir
    )

    val releasesDir = snapshotDir / ReleasesDirName

    val releaseDir = releasesDir / r.releaseId

    if (exists(releaseDir)) {
      println(s"Release already exists: ${r.asModule.asString}")
    } else {
      r.asModule.depsTransitive.foreach({ m =>
        require(
          !m.isSnapshot,
          s"${r.asModule.asString} has snapshot dependency: ${m.asString}"
        )
      })

      copyProject(
        snapshotDir,
        releaseDir
      )


    }



  }

}
