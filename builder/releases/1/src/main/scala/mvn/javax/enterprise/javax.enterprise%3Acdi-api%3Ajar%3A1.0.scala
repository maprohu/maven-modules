
package mvn

import mvnmod.builder.MavenCentralModule

object `javax.enterprise:cdi-api:jar:1.0` extends MavenCentralModule(
  "javax.enterprise:cdi-api:jar:1.0",
  `org.jboss.interceptor:jboss-interceptor-api:jar:1.1`,
  `javax.annotation:jsr250-api:jar:1.0`,
  `javax.inject:javax.inject:jar:1`
)
         