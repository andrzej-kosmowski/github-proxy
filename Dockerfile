FROM amazoncorretto:21-alpine
MAINTAINER Andrzej
COPY target/github-proxy-0.0.1-SNAPSHOT.jar gh-app.jar
ENTRYPOINT ["java", "-jar", "gh-app.jar"]