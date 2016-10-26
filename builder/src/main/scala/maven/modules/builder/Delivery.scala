package maven.modules.builder

import java.io.File
import ammonite.ops._

/**
  * Created by pappmar on 26/10/2016.
  */
object Delivery {

  val skips = Set(
    "target",
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
    what: Seq[NamedModule],
    roots: Map[RootModuleContainer, File],
    where: File
  ) = {
    where.mkdirs()


    what
      .flatMap({ nm =>
        nm
          .asModule
          .depsTransitive
          .flatMap(_.source)
      })
      .map(_.container.root)
      .distinct
      .foreach({ rmc =>
        copySource()
      })


  }

}
