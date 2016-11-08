package mvnmod.builder

/**
  * Created by pappmar on 08/11/2016.
  */
object PackagingTools {

  val IgnoreVersionMismatch =
    Set
      .apply(
        mvn.`org.slf4j:slf4j-api:jar:1.7.21`,
        mvn.`org.scala-lang:scala-library:jar:2.11.8`,
        mvn.`com.typesafe:config:jar:1.2.1`,
        mvn.`emsa.ssn:ssn-spm-domain:jar:1.0.5.2`,
        mvn.`emsa.ssn:ssn-vdm-support:jar:1.0.5.2`
      )
      .map(Module.central2Module)
      .map(_.version.moduleId)

  def verifyVersions(modules: Seq[Module]) = {
    val mismatching =
      modules
        .flatMap(_.toSeq)
        .filterNot(m => IgnoreVersionMismatch.contains(m.version.moduleId))
        .to[Set]
        .groupBy(_.version.moduleId)
        .collect({ case (v, s) if s.size > 1 => (v, s.map(_.version.mavenVersion)) })

    val stoppers = mismatching.keySet

    def follow(
      module: Module,
      path: Seq[ModuleVersion]
    ) : Seq[(ModuleVersion, Seq[ModuleVersion])] = {
      val p2 = path :+ module.version
      val v = module.version
      val id = v.moduleId
      if (stoppers.contains(id)) {
        Seq((v, p2))
      } else {
        module
          .deps
          .flatMap({ m =>
            follow(
              m,
              p2
            )
          })
      }
    }

    println(
      mismatching
        .mkString("\n")
    )

    println("\n")

    println(
      modules
        .flatMap(m => follow(m, Seq.empty))
        .groupBy(_._1)
        .toSeq
        .sortBy(_._1.moduleId)
        .map({
          case (id, paths) =>
            s"${id}\n${
              paths
                .map(_._2)
                .sortBy(_.size)
                .map({ p =>
                  s"  ${p.mkString(" -> ")}"
                })
                .mkString("\n")
            }"
        })
        .mkString("\n")
    )






  }

}
