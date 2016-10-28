package maven.modules.builder

import java.io.File

import maven.modules.builder.Module.{ConfiguredModule, DeployableModule, Java7, Java8}
import maven.modules.utils.{MavenCentralModule, Repo}
import org.eclipse.aether.util.version.GenericVersionScheme
import org.eclipse.aether.version.Version
import sbt.io.IO

import scala.xml.{NodeSeq, PrettyPrinter}
import scala.collection.immutable._

/**
  * Created by pappmar on 29/08/2016.
  */

object Implicits extends ModulesLike
trait ModulesLike {
  implicit class MavenOps(mvn: MavenCentralModule) {
    def provided : Module = {
      val m = mvn:Module
      m.copy(provided = true)
    }
  }
}



case class ModuleId(
  groupId: String,
  artifactId: String,
  classifier: Option[String]
)

case class ModuleVersion(
  mavenModuleId: ModuleId,
  version: Version
) extends Comparable[ModuleVersion] {
  def compareTo(o: ModuleVersion): Int = {
    val mmv = o.asInstanceOf[ModuleVersion]
    require(moduleId == mmv.moduleId)
    version.compareTo(mmv.version)
  }
  def moduleId: ModuleId = mavenModuleId
  def isSnapshot = version.toString.endsWith("-SNAPSHOT")
}

case class Module(
  val version: ModuleVersion,
  val deps: Seq[Module],
  val repos: Seq[Repo],
  val source: Option[NamedModule],
  val provided : Boolean = false
) {
  def depsTransitive : Seq[Module] = {
    deps
      .flatMap(m => m +: m.depsTransitive)
  }

  def depsTransitiveSelfLast : Seq[Module] = {
    deps
      .flatMap(m => m.depsTransitive :+ m)
  }

  def filter(fn: Module => Boolean) : Module = {
    copy(
      deps = deps.filter(fn).map(_.filter(fn))
    )
  }

  def map[T](fn: Module => Module) : Module = {
    copy(
      deps = deps.map(fn)
    )
  }

  def flatten : Module = {
    copy(
      deps = depsTransitive.distinct.map(_.flatten)
    )
  }

  def asProvided : Module = copy(provided = true)

  def isSnapshot = version.isSnapshot

  def asString = s"${version.mavenModuleId.groupId}:${version.mavenModuleId.artifactId}:${version.version.toString}"

  def toSeq : Seq[Module] = {
    this +: deps.flatMap(_.toSeq)
  }
}



object Module {
  def provided(module: Module) : Module = module.copy(provided = true)

  val versionScheme = new GenericVersionScheme

  implicit def central2Module(clk: MavenCentralModule) : Module = {
    Module(
      ModuleVersion(
        ModuleId(
          groupId = clk.groupId,
          artifactId = clk.artifactId,
          classifier = clk.classifier
        ),
        versionScheme.parseVersion(clk.version)
      ),
      clk.dependencies.map(central2Module),
      repos = clk.dependencies.flatMap(_.repos).distinct,
      source = None
    )
  }

  implicit def namedModuleToModule(namedModule: NamedModule) : Module = {
    Module(
      ModuleVersion(
        ModuleId(
          namedModule.container.root.groupId,
          namedModule.path.mkString("-"),
          None
        ),
        versionScheme.parseVersion(namedModule.version)
      ),
      namedModule.deps.to[Seq],
      repos = namedModule.deps.to[Seq].flatMap(_.repos).distinct,
      source = Some(namedModule)
    )
  }

  type PlaceLookup = RootModuleContainer => File

  def projectDir(
    namedModule: NamedModule,
    placeLookup: PlaceLookup
  ) : File = {
    namedModule
      .path
      .tail
      .foldLeft(
        placeLookup(namedModule.container.root)
      )(
        new File(_, _)
      )
  }

  trait DeployableModule {
    def groupId: String
    def artifactId : String
    def version: String
  }

  case class DeployableModuleImpl(
    groupId: String,
    artifactId : String,
    version: String
  ) extends DeployableModule

  object DeployableModule {
    implicit class Ops(module: DeployableModule) {
      def toBundle : DeployableModule = {
        DeployableModuleImpl(
          groupId = module.groupId,
          artifactId = s"${module.artifactId}-bundle",
          version = module.version
        )
      }
    }

    implicit def key2deployable(m: MavenCentralModule) : DeployableModule =
      DeployableModuleImpl(
        m.groupId,
        m.artifactId,
        m.version
      )
  }

  def asPomCoordinates(
    module: DeployableModule
  ) = {
    <groupId>{module.groupId}</groupId>
    <artifactId>{module.artifactId}</artifactId>
    <version>{module.version}</version>
  }

  def asPomDependency(
    module: DeployableModule
  ) = {
    <dependency>
      {asPomCoordinates(module)}
    </dependency>
  }

  sealed class JavaVersion(
    val value : String
  )
  case object Java6 extends JavaVersion("1.6")
  case object Java7 extends JavaVersion("1.7")
  case object Java8 extends JavaVersion("1.8")

  case class ConfiguredModule(
    module: NamedModule,
    repos: Seq[Repo],
    javaVersion : JavaVersion
  )

  object ConfiguredModule {
    implicit def named2configured(module: NamedModule) : ConfiguredModule = {
      ConfiguredModule(
        module,
        module.deps.to[Seq].flatMap(_.repos).distinct,
        Java6
      )
    }

    implicit class NamedOps(namedModule: NamedModule) {
      def java7 : ConfiguredModule = {
        named2configured(namedModule).copy(javaVersion = Java7)
      }
    }
  }


  val pretty = new PrettyPrinter(300, 4)

  def generate(
    roots: collection.Seq[PlacedRoot],
    configuredModules: collection.Seq[ConfiguredModule]
  ) : Unit = {
    val modules = configuredModules.map(_.module)

    val containedContainers =
      modules
        .flatMap(_.container.toContainedSeq)
        .distinct

    val containedModules =
      containedContainers ++ modules


    val childrenMap =
      containedModules
        .groupBy(_.parent)


    val placeLookup : Map[RootModuleContainer, File] =
      roots
        .map(p => p.rootContainer -> p.rootDir)
        .toMap

    val containerModules =
      modules
        .flatMap(_.container.toSeq)
        .distinct


    containerModules
      .foreach({ place =>
        val dir = place
          .toContainedSeq
          .map(_.name)
          .foldLeft(placeLookup(place.root))(new File(_, _))

        dir.mkdirs()

        val xml =
          <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <groupId>{place.root.name}</groupId>
            <artifactId>{place.artifactId}</artifactId>
            <version>1.0.0</version>
            <packaging>pom</packaging>

            <build>
              <plugins>
                <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-dependency-plugin</artifactId>
                  <version>2.10</version>
                </plugin>
              </plugins>
            </build>

            <modules>
              {
                childrenMap
                  .get(place)
                  .toSeq
                  .flatMap({ children =>
                    children.map({ child =>
                      <module>{child.name}</module>
                    })
                  })
              }
            </modules>
          </project>

        writeFile(
          new File(dir, "pom.xml"),
          pretty.format(xml)
        )
      })

    configuredModules
      .foreach({ configuredModule =>
        import configuredModule._
        val dir =
          new File(
            module
              .container
              .toContainedSeq
              .map(_.name)
              .foldLeft(placeLookup(module.container.root))(new File(_, _)),
            module.name
          )

        generateSingle(
          configuredModule,
          dir
        )

      })
  }

  def generateSingle(
    configuredModule: ConfiguredModule,
    dir: File
  ) = {
    import configuredModule._

    dir.mkdirs()

    def coords(dep: ModuleVersion) = (
      <groupId>{dep.moduleId.groupId}</groupId>
        <artifactId>{dep.moduleId.artifactId}</artifactId>
        <version>{dep.version.toString}</version> &+
        dep.moduleId.classifier.map(c => <classifier>{c}</classifier>).toSeq
      )

    val xml =
      <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>{module.container.root.groupId}</groupId>
        <artifactId>{module.container.artifactId}-{module.name}</artifactId>
        <version>{module.version}</version>
        <packaging>jar</packaging>
        <build>
          <finalName>product</finalName>
          <plugins>
            <plugin>
              <groupId>net.alchim31.maven</groupId>
              <artifactId>scala-maven-plugin</artifactId>
              <version>3.2.1</version>
              <executions>
                <execution>
                  <goals>
                    <goal>add-source</goal>
                    <goal>compile</goal>
                    <goal>testCompile</goal>
                  </goals>
                </execution>
              </executions>
            </plugin>
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-compiler-plugin</artifactId>
              <version>3.5.1</version>
              <configuration>
                <source>{javaVersion.value}</source>
                <target>{javaVersion.value}</target>
              </configuration>
            </plugin>
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-source-plugin</artifactId>
              <version>3.0.1</version>
              <executions>
                <execution>
                  <id>attach-sources</id>
                  <phase>package</phase>
                  <goals>
                    <goal>jar</goal>
                  </goals>
                </execution>
              </executions>
            </plugin>
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-dependency-plugin</artifactId>
              <version>2.10</version>
            </plugin>
            <plugin>
              <groupId>org.codehaus.mojo</groupId>
              <artifactId>build-helper-maven-plugin</artifactId>
              <version>1.12</version>
              <executions>
                <execution>
                  <id>add-source</id>
                  <phase>generate-sources</phase>
                  <goals>
                    <goal>add-source</goal>
                  </goals>
                  <configuration>
                    <sources>
                      <source>{"${project.build.directory}/generated-sources"}</source>
                    </sources>
                  </configuration>
                </execution>
              </executions>
            </plugin>
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-antrun-plugin</artifactId>
              <executions>
                <execution>
                  <phase>generate-resources</phase>
                  <goals>
                    <goal>run</goal>
                  </goals>
                  <configuration>
                    <tasks>
                      <mkdir dir="${basedir}/target/classes/META-INF"/>
                      <tstamp>
                        <format property="last.updated" pattern="yyyyMMddHHmmssSSS"/>
                      </tstamp>
                      <echo file="${basedir}/target/classes/META-INF/build.timestamp" message="${last.updated}"/>
                    </tasks>
                  </configuration>
                </execution>
              </executions>
            </plugin>
            <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-jar-plugin</artifactId>
              <version>3.0.2</version>
              <configuration>
                <archive>
                  <manifest>
                    <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                  </manifest>
                </archive>
              </configuration>
            </plugin>
          </plugins>
        </build>
        <dependencyManagement>
          <dependencies>
            {
            //                module
            //                  .deps
            //                  .flatMap(_.deps)
            //                  .map(_.version)
            //                  .groupBy(_.mavenModuleId)
            //                  .map(_._2.maxBy(_.version))
            //                  .map({ m =>
            //                    <dependency>
            //                      {coords(m)}
            //                      <scope>runtime</scope>
            //                    </dependency>
            //                  })
            }
          </dependencies>
        </dependencyManagement>
        <dependencies>
          {
          module
            .deps
            .map(m => (m.version, m.provided))
            .collect({ case (dep : ModuleVersion, provided) =>
              <dependency>
                {coords(dep)}
                {
                //                      if (provided) <scope>provided</scope> else <scope>compile</scope>
                }
              </dependency>
            })
          }
        </dependencies>
        <repositories>
          {
            configuredModule.repos.map { r =>
              <repository>
                <id>{r.id}</id>
                <url>{r.url}</url>
              </repository>
            }
          }
        </repositories>
      </project>

    module.path.foldLeft(new File(dir, "src/main/scala"))(new File(_, _)).mkdirs()

    writeFile(
      new File(dir, "pom.xml"),
      pretty.format(xml)
    )

  }

  def writeFile(
    file: File,
    content: String
  ) : Unit = {
    if (!file.exists() || IO.read(file) != content) {
      println(s"Writing: ${file}")
      IO.write(file, content)
    } else {
      println(s"Skipping: ${file}")
    }
  }
}

sealed trait ContainedModule {
  def parent: ModuleContainer
  def name : String
}

class NamedModule(
  val container: ModuleContainer,
  val name: String,
  val deps: Module*
) extends ContainedModule with DeployableModule {
  def path : Seq[String] = container.path :+ name
  def parent: ModuleContainer = container
  def groupId = container.root.groupId
  def artifactId = path.mkString("-")
  def pkg = path.mkString(".")
  def java8 = ConfiguredModule.named2configured(this).copy(javaVersion = Java8)
  def version = "2-SNAPSHOT"
  def asModule : Module = this
  def pathFromRoot = path.tail


  def pomCoordinates = {
    <groupId>{groupId}</groupId>
    <artifactId>{artifactId}</artifactId>
    <version>{version}</version>
  }

  def pomDependency = {
    <dependency>
      {pomCoordinates}
    </dependency>
  }


  class Release(
    deps: Module*
  ) extends NamedModule(
    container,
    name,
    deps:_*
  ) { self =>
    lazy val releaseId = self.getClass.getName.reverse.drop(1).takeWhile(_ != '$').reverse.filter(_.isDigit)
    override def pathFromRoot = super.pathFromRoot ++ Seq(ModuleRelease.ReleasesDirName, releaseId)
    override lazy val version = s"1.${releaseId}"
  }
}

class JavaModule(
  name: String,
  deps: Module*
)(implicit
  container: ModuleContainer
) extends NamedModule (
  container,
  name,
  (deps ++ Seq[Module](
    Module.provided(mvn.`org.scala-lang:scala-library:jar:2.11.8`)
  )):_*
)

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

  class Release(
    deps: Module*
  ) extends super.Release(
    ScalaModule.deps(deps):_*
  )

}

class PlacedRoot(
  val rootContainer: RootModuleContainer,
  val rootDir: File
)

object PlacedRoot {
  implicit def fromTuple(
    tuple: (RootModuleContainer, File)
  ) = tuple match {
    case (root, dir) =>
      new PlacedRoot(root, dir)
  }
}

trait Artifact {
  def artifactId : String
}

sealed trait ModuleContainer extends Artifact {
  def name : String
  def root : RootModuleContainer
  def path : Seq[String] = toSeq.map(_.name)
  def toSeq : Seq[ModuleContainer]
  def toContainedSeq : Seq[ContainedModule]
  def parentOpt: Option[ModuleContainer]
  def artifactId: String = path.mkString("-")
}

case class RootModuleContainer(
  groupId : String
) extends ModuleContainer {
  def name = groupId
  def root = this
  def toSeq: Seq[ModuleContainer] = Seq(this)
  def toContainedSeq: Seq[ContainedModule] = Seq()
  def parentOpt: Option[ModuleContainer] = None
}

case class SubModuleContainer(
  parent: ModuleContainer,
  name: String
) extends ModuleContainer with ContainedModule {
  def root = parent.root
  def toSeq: Seq[ModuleContainer] = parent.toSeq :+ this
  def toContainedSeq: Seq[ContainedModule] = parent.toContainedSeq :+ this
  def parentOpt: Option[ModuleContainer] = Some(parent)
}


