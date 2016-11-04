
package mvn

import mvnmod.builder.MavenCentralModule

object `org.apache.tomcat:tomcat-coyote:jar:8.5.5` extends MavenCentralModule(
  "org.apache.tomcat:tomcat-coyote:jar:8.5.5",
  `org.apache.tomcat:tomcat-servlet-api:jar:8.5.5`,
  `org.apache.tomcat:tomcat-jni:jar:8.5.5`,
  `org.apache.tomcat:tomcat-juli:jar:8.5.5`,
  `org.apache.tomcat:tomcat-util:jar:8.5.5`
)
         