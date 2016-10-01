package jartree.util

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates

import scala.collection.immutable._

/**
  * Created by pappmar on 31/08/2016.
  */

sealed trait CaseJarKey


case class HashJarKeyImpl(
  hashSeq : Seq[Byte]
) extends CaseJarKey

case class MavenJarKeyImpl(
  groupId: String,
  artifactId: String,
  version: String,
  classifierOpt: Option[String] = None
) extends CaseJarKey

object MavenJarKeyImpl {

  def apply(canonical: String) : MavenJarKeyImpl = {
    val mc = MavenCoordinates.createCoordinate(canonical)

    MavenJarKeyImpl(
      mc.getGroupId,
      mc.getArtifactId,
      mc.getVersion,
      Option(mc.getClassifier).filterNot(_.isEmpty)
    )
  }

  implicit def key2loaderKey(key: MavenJarKeyImpl) : CaseClassLoaderKey = {
    CaseClassLoaderKey(key)
  }

}

case class CaseClassLoaderKey(
  jar: CaseJarKey,
  dependenciesSeq: Seq[CaseClassLoaderKey] = Seq()
)


