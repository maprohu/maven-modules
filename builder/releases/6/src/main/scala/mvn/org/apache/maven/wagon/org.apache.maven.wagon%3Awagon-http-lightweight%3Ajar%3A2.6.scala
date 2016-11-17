
package mvn

import mvnmod.builder.MavenCentralModule

object `org.apache.maven.wagon:wagon-http-lightweight:jar:2.6` extends MavenCentralModule(
  "org.apache.maven.wagon:wagon-http-lightweight:jar:2.6",
  `org.apache.maven.wagon:wagon-http-shared:jar:2.6`,
  `org.jsoup:jsoup:jar:1.7.2`,
  `commons-lang:commons-lang:jar:2.6`,
  `commons-io:commons-io:jar:2.2`,
  `org.apache.maven.wagon:wagon-provider-api:jar:2.6`,
  `org.codehaus.plexus:plexus-utils:jar:3.0.8`
)
         