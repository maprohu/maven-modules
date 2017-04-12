package mvnmod.builder

import java.io.File
import java.nio.file.Files

import org.apache.maven.shared.invoker.{DefaultInvocationRequest, DefaultInvoker}

import scala.collection.JavaConversions._
import scala.xml._

/**
  * Created by martonpapp on 01/10/16.
  */
object MavenTools {

  lazy val tmpRoot : File = {
    val file = new File("../maven-modules/target/mvntmproot")
    file.mkdirs()

    val dir = Files.createTempDirectory(
      file.toPath,
      "root"
    ).toFile

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        import ammonite.ops._
        rm(Path(dir))
      }
    })

    dir
  }

  def inTempDir[T](fn: File => T) : T = {
    val dir = Files.createTempDirectory(
      tmpRoot.toPath,
      "mvn"
    )

    fn(dir.toFile)
  }

  def runMaven[T](
    pomFileString: Node,
    goal : String,
    preBuild: File => Unit = _ => ()
  )( andThen : File => T ) : T = {
    runMavens(
      pomFileString,
      Seq(goal),
      preBuild
    )(
      andThen
    )
  }

  def runMavens[T](
    pomFileString: Node,
    goals : Seq[String],
    preBuild: File => Unit = _ => ()
  )( andThen : File => T ) : T = {
    runMavens(
      ProjectDef(
        null,
        pomFileString,
        preBuild
      ),
      goals
    )(andThen)

  }


  def runMavens[T](
    projectDef: ProjectDef,
    goals : Seq[String]
  )( andThen : File => T ) : T = {
    try {
      inTempDir { dir =>
        try {
          val pomFile = createProject(projectDef, dir)

          val request = new DefaultInvocationRequest
          request.setPomFile(pomFile)
          request.setGoals( goals )
          val invoker = new DefaultInvoker

          val result = invoker.execute(request)

          require(result.getExitCode == 0, "maven exited with error")

          println("starting postprocessing...")
          andThen(dir)

        } finally {
          println("leaving project dir")
        }

      }
    } finally {
      println("project dir left")
    }
  }

  def runMavenProject(dir: File, goals : Seq[String]) = {
    val pomFile = new File(dir, "pom.xml")

    val request = new DefaultInvocationRequest
    request.setPomFile(pomFile)
    request.setGoals( goals )
    val invoker = new DefaultInvoker

    val result = invoker.execute(request)

    require(result.getExitCode == 0)
  }

  def pom(content: NodeSeq) : Elem = {
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>

      <groupId>toolbox6</groupId>
      <artifactId>maven-tools-temp</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      {content}
    </project>
  }

  def pom(
    hasMavenCoordinates: HasMavenCoordinates,
    content: NodeSeq
  ) : Node = {
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      {hasMavenCoordinates.asPomCoordinates}
      {content}
    </project>
  }

  case class ProjectDef(
    coordinates: MavenCoordinatesImpl,
    pom: Node,
    preBuild: File => Unit
  )

  object ProjectDef {
    def multi(
      coordinates: MavenCoordinatesImpl,
      modules: ProjectDef*
    ) : ProjectDef = {
      namedMulti(
        coordinates,
        modules
          .map(d => d.coordinates.artifactId -> d):_*
      )
    }

    def namedMulti(
      coordinates: MavenCoordinatesImpl,
      modules: (String, ProjectDef)*
    ) : ProjectDef = {
      ProjectDef.apply(
        coordinates = coordinates,
        pom = pom(
          coordinates,
          <packaging>pom</packaging>
          <modules>
            {
            modules.map({ case (id, _) =>
              <module>{id}</module>
            })
            }
          </modules>
        ),
        preBuild = { dir =>
          modules.foreach({
            case (id, pd) =>
              createProject(
                pd,
                new File(dir, id)
              )
          })
        }
      )

    }
  }

  lazy val pp = new PrettyPrinter(1000, 2)
  def createProject(
    projectDef: ProjectDef,
    dir: File
  ) = {
    dir.mkdirs()

    val pomFile = new File(dir, "pom.xml")
    val pomString = pp.format(projectDef.pom)
    println(pomString)
    import ammonite.ops._
    write.over(
      Path(pomFile),
      pomString
    )

    projectDef.preBuild(dir)

    pomFile
  }


}
