FROM amazoncorretto:21
LABEL maintainer="chandankrr"
WORKDIR /app
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service-0.0.1-SNAPSHOT.jar
EXPOSE 9810
ENTRYPOINT ["java", "-jar", "/app/auth-service-0.0.1-SNAPSHOT.jar"]
