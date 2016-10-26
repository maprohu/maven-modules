package maven.modules.builder

import java.net.URLEncoder

import maven.modules.utils.MavenCentralModule

import scala.xml.NodeBuffer

/**
  * Created by pappmar on 26/10/2016.
  */
class MavenCoordinates {

}

final case class MavenCoordinatesImpl(
  groupId: String,
  artifactId: String,
  version: String,
  override val classifier : Option[String] = None
) extends HasMavenCoordinates {

}

object MavenCoordinatesImpl extends HasMavenCoordinatesImplicits {

  //  implicit def fromCJR(cjk: CaseJarKey) : MavenCoordinatesImpl = {
  //    cjk match {
  //      case m :MavenJarKeyImpl
  //    }
  //
  //  }

}
object HasMavenCoordinates extends HasMavenCoordinatesImplicits{


}

trait HasMavenCoordinates {
  def groupId: String
  def artifactId : String
  def version: String
  def classifier : Option[String] = None

  def asPomCoordinates : NodeBuffer = {
    val gav =
      <groupId>{groupId}</groupId>
        <artifactId>{artifactId}</artifactId>
        <version>{version}</version>

    gav &+
      classifier.map(c => <classifier>{c}</classifier>).getOrElse()
  }

  def asPomDependency = {
    <dependency>
      {asPomCoordinates}
    </dependency>
  }

  def toCanonical = {
    s"${groupId}:${artifactId}:jar:${version}"
  }

  def toFileName = {
    s"${URLEncoder.encode(toCanonical, "UTF-8")}.jar"
  }

  def isSnapshot = version.endsWith("SNAPSHOT")
}


trait HasMavenCoordinatesImplicits {
  implicit def toImpl(coords: HasMavenCoordinates) : MavenCoordinatesImpl = {
    MavenCoordinatesImpl(
      coords.groupId,
      coords.artifactId,
      coords.version
    )
  }


  implicit def mavenModuleVersion2coords(module: ModuleVersion) : MavenCoordinatesImpl = {
    MavenCoordinatesImpl(
      module.mavenModuleId.groupId,
      module.mavenModuleId.artifactId,
      module.version.toString
    )
  }

  implicit def module2coords(module: Module) : MavenCoordinatesImpl = {
    module.version match {
      case m : ModuleVersion =>
        m
      case _ => ???
    }
  }

  implicit def namedModule2coords(module: NamedModule) : MavenCoordinatesImpl = {
    MavenCoordinatesImpl(
      module.groupId,
      module.artifactId,
      module.version
    )
  }

  implicit def key2maven(module: MavenCentralModule) : MavenCoordinatesImpl = {
    MavenCoordinatesImpl(
      module.groupId,
      module.artifactId,
      module.version
    )
  }

}
