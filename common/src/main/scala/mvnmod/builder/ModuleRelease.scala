package mvnmod.builder

import java.io.File

import ammonite.ops.{Path, _}

import scala.collection.immutable._

/**
  * Created by pappmar on 13/10/2016.
  */
object ModuleRelease {

  val ReleasesDirName = "releases"

  def name(n: String) : Path => Boolean = _.name == n
  def ext(n: String) : Path => Boolean = _.ext == n
  val DontCopyFiles = Seq[(Path => Boolean)](
    name(ReleasesDirName),
    name("pom.xml"),
    name("target"),
    ext("iml")
  )

  def copyProject(
    from: Path,
    to: Path
  ) = {
    mkdir! to

    ls! from |? (f => !DontCopyFiles.exists(_(f))) | (f => cp(f, to / f.name))
  }

  def releaseDirFor(
    roots: Seq[PlacedRoot],
    r: NamedModule#Release
  ) : (Path, Path) = {
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

    (snapshotDir, releaseDir)
  }

  def release(
    roots: Seq[PlacedRoot],
    r: NamedModule#Release
  ) = {
    val (snapshotDir, releaseDir) = releaseDirFor(roots, r)
//    val placeLookup : Map[RootModuleContainer, File] =
//      roots
//        .map(p => p.rootContainer -> p.rootDir)
//        .toMap
//
//    val rootDir = Path.apply(placeLookup(r.container.root).getAbsoluteFile.toPath)
//
//    val snapshotDir = Path(
//      r
//       .path
//        .tail
//        .mkString("/"),
//      rootDir
//    )
//
//    val releasesDir = snapshotDir / ReleasesDirName
//
//    val releaseDir = releasesDir / r.releaseId

    if (exists(releaseDir)) {
      println(s"Release already exists: ${r.asModule.asString}")
    } else {
      r.asModule.depsTransitive.foreach({ m =>
        require(
          !m.isSnapshot,
          s"${r.asModule.asString} has snapshot dependency: ${m.asString}"
        )
      })

      try {
        copyProject(
          snapshotDir,
          releaseDir
        )

        Module.generateSingle(
          r,
          releaseDir.toIO
        )

        MavenTools.runMavenProject(
          releaseDir.toIO,
          Seq("install")
        )


      } catch {
        case ex: Throwable =>
          ex.printStackTrace()
          rm(releaseDir)
          throw ex
      }


    }



  }

  def installRelease(
    roots: Seq[PlacedRoot],
    r: NamedModule#Release
  ) = {
    val (_, releaseDir) = releaseDirFor(roots, r)

    MavenTools.runMavenProject(
      releaseDir.toIO,
      Seq("install")
    )

  }

}
