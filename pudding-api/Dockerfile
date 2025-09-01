FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/pudding-api-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
