package mvnmod.builder



import scala.collection.immutable._

object ScalaModule {

  def deps(
    d: collection.Seq[Module]
  ) = {
    (d ++ Seq[Module](
      mvn.`org.scala-lang:scala-library:jar:2.11.8`
    ))
  }

}

class ScalaModule(
  name: String,
  deps: Module*
)(implicit
  container: ModuleContainer
) extends NamedModule (
  container,
  name,
  ScalaModule.deps(deps):_*
) {
  override def snapshot: NamedModule = super.snapshot

  class Release(
    deps: Module*
  ) extends super.Release(
    ScalaModule.deps(deps):_*
  )

}

