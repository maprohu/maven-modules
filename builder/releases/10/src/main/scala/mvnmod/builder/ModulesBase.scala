package mvnmod.builder

/**
  * Created by maprohu on 08-12-2016.
  */
class ModulesBase(
  groupId: String,
  deps: Module*
) {
  implicit val Root = RootModuleContainer(groupId)

  class MetaModule extends ScalaModule(
    "modules",
    deps:_*
  )
  object MetaModule extends MetaModule

  object Builders extends ScalaModule(
    "builders",
    MetaModule
  )



}
