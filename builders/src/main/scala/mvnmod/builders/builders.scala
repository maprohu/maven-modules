package mvnmod.builders

object build_mvnmod extends mvnmod.builder.ModuleBuilder(
  mvnmod.modules.Place.RootPath,
  "."
)
           
object build_mvnmod_common extends mvnmod.builder.ModuleBuilder(
  mvnmod.modules.Place.RootPath,
  "./common"
)
           
object build_mvnmod_modules extends mvnmod.builder.ModuleBuilder(
  mvnmod.modules.Place.RootPath,
  "./modules"
)
           
object build_mvnmod_builder extends mvnmod.builder.ModuleBuilder(
  mvnmod.modules.Place.RootPath,
  "./builder"
)
           
object build_mvnmod_generator extends mvnmod.builder.ModuleBuilder(
  mvnmod.modules.Place.RootPath,
  "./generator"
)
           
       