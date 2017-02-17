package mvnmod.builder



import mvnmod.builder.Module.{Java6, Java8, JavaVersion}

import scala.collection.immutable._

object ScalaModule {

  def deps(
    d: collection.Seq[Module]
  ) = {
    (d ++ Seq[Module](
      mvn.`org.scala-lang:scala-library:jar:2.11.8`
    ))
  }

  def deps212(
    d: collection.Seq[Module]
  ) = {
    (d ++ Seq[Module](
      mvn.`org.scala-lang:scala-library:jar:2.12.1`
    ))
  }

}

class ScalaModule(
  name: String,
  javaVersion: JavaVersion,
  deps: Module*
)(implicit
  container: ModuleContainer
) extends NamedModule (
  container,
  name,
  javaVersion,
  ScalaModule.deps(deps):_*
) {
  def this(
    name: String,
    deps: Module*
  )(implicit
    container: ModuleContainer
  ) = this(
    name,
    Java6,
    deps:_*
  )

  override def snapshot: NamedModule = super.snapshot

  class Release(
    deps: Module*
  ) extends super.Release(
    ScalaModule.deps(deps):_*
  )

}

class Scala212Module(
  name: String,
  javaVersion: JavaVersion,
  deps: Module*
)(implicit
  container: ModuleContainer
) extends NamedModule (
  container,
  name,
  javaVersion,
  ScalaModule.deps212(deps):_*
) {
  def this(
    name: String,
    deps: Module*
  )(implicit
    container: ModuleContainer
  ) = this(
    name,
    Java8,
    deps:_*
  )

  override def snapshot: NamedModule = super.snapshot

  class Release(
    deps: Module*
  ) extends super.Release(
    ScalaModule.deps212(deps):_*
  )

}

