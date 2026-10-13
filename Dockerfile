FROM amazoncorretto:21-alpine
COPY target/github-proxy-0.0.1-SNAPSHOT.jar gh-app.jar
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
ENTRYPOINT ["java", "-jar", "gh-app.jar"]