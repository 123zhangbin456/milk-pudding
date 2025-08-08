# syntax=docker/dockerfile:1.6

############################
# Build stage
############################
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY pudding-user/pom.xml ./pudding-user/pom.xml
COPY pudding-gateway/pom.xml ./pudding-gateway/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn -q -Dmaven.test.skip=true -e -DskipTests dependency:go-offline

COPY pudding-user ./pudding-user
COPY pudding-gateway ./pudding-gateway

RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -pl pudding-gateway -am package

############################
# Runtime stage
############################
FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY --from=build /workspace/pudding-gateway/target/*.jar /app/app.jar

ENV JAVA_OPTS="" \
    NACOS_ADDR="127.0.0.1:8848" \
    NACOS_NAMESPACE="" \
    NACOS_GROUP="DEFAULT_GROUP"

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
