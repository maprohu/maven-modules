package maven.modules.utils

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates

import scala.collection.immutable._

/**
  * Created by pappmar on 31/08/2016.
  */

case class GAV(
  groupId: String,
  artifactId: String,
  version: String,
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
      Option(mc.getClassifier).filterNot(_.isEmpty)
    )
  }

}

class MavenCentralModule(
  val groupId: String,
  val artifactId: String,
  val version: String,
  val classifier: Option[String] = None,
  val dependencies: Seq[MavenCentralModule] = Seq()
) {
  def this(
    gav: GAV,
    dependencies: Seq[MavenCentralModule]
  ) = this(
    groupId = gav.groupId,
    artifactId = gav.artifactId,
    version = gav.version,
    classifier = gav.classifier,
    dependencies = dependencies
  )

  def this(
    canonical: String,
    dependencies: MavenCentralModule*
  ) = this(
    GAV(canonical),
    dependencies.to[Seq]
  )

}


