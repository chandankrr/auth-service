FROM amazoncorretto:21
MAINTAINER chandankrr
COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/auth-service-0.0.1-SNAPSHOT.jar"]