package mvnmod.builder

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates

import scala.collection.immutable._

/**
  * Created by pappmar on 31/08/2016.
  */

case class GAV(
  groupId: String,
  artifactId: String,
  version: String,
  packaging: Option[String],
  classifier: Option[String]
) {
}

object GAV {
  def apply(canonical: String) : GAV = {
    val mc = MavenCoordinates.createCoordinate(canonical)

    GAV(
      mc.getGroupId,
      mc.getArtifactId,
      mc.getVersion,
      Some(mc.getPackaging.getExtension),
      Option(mc.getClassifier).filterNot(_.isEmpty)
    )
  }

}

case class Repo(
  id: String,
  url: String
)

object MavenCentralModule {
  object Implicits {
    implicit class MavenCentralModuleOps(m: MavenCentralModule) {
      def exclude(
        modules: Module*
      ) : Module = {
        val excludes =
          modules
            .map(_.version.moduleId)
            .toSet

        Module
          .central2Module(m)
          .filter({ e =>
            !excludes.contains(e.version.moduleId)
          })
      }

    }
  }
}

class MavenCentralModule(
  val groupId: String,
  val artifactId: String,
  val version: String,
  val packaging: Option[String] = None,
  val classifier: Option[String] = None,
  val dependencies: Seq[MavenCentralModule] = Seq(),
  val repos: Seq[Repo] = Seq()
) {
  def this(
    gav: GAV,
    repos: Seq[Repo],
    dependencies: Seq[MavenCentralModule]
  ) = this(
    groupId = gav.groupId,
    artifactId = gav.artifactId,
    version = gav.version,
    classifier = gav.classifier,
    packaging = gav.packaging,
    dependencies = dependencies,
    repos = repos
  )

  def this(
    canonical: String,
    repos: Seq[Repo],
    dependencies: MavenCentralModule*
  ) = this(
    GAV(canonical),
    repos = repos,
    dependencies = dependencies.to[Seq]
  )

  def this(
    canonical: String,
    dependencies: MavenCentralModule*
  ) = this(
    GAV(canonical),
    repos = Seq(),
    dependencies = dependencies.to[Seq]
  )

  def canonical = {
    MavenCoordinates
      .createCoordinate(
        groupId,
        artifactId,
        version,
        packaging.map(p => PackagingType.of(p)).orNull,
        classifier.orNull
      )
      .toCanonicalForm
  }

}

object GeotoolsMoule {
  def Repos = Seq(
    Repo("geotools", "http://download.osgeo.org/webdav/geotools/"),
    Repo("boundles", "http://repo.boundlessgeo.com/main")
  )
}
class GeotoolsMoule(
  canonical: String,
  dependencies: MavenCentralModule*
) extends MavenCentralModule(
  canonical,
  GeotoolsMoule.Repos,
  dependencies:_*
)

