# syntax=docker/dockerfile:1.6

############################
# Build stage
############################
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace

# Copy only necessary files to leverage Docker layer caching
COPY pom.xml ./
COPY pudding-user/pom.xml ./pudding-user/pom.xml
COPY pudding-gateway/pom.xml ./pudding-gateway/pom.xml

# pre-fetch dependencies (optional but speeds up builds)
RUN --mount=type=cache,target=/root/.m2 mvn -q -Dmaven.test.skip=true -e -DskipTests dependency:go-offline

# now copy sources
COPY pudding-user ./pudding-user
COPY pudding-gateway ./pudding-gateway

# build only this module (and required modules)
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -pl pudding-user -am package

############################
# Runtime stage
############################
FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY --from=build /workspace/pudding-user/target/*.jar /app/app.jar

ENV JAVA_OPTS="" \
    NACOS_ADDR="127.0.0.1:8848" \
    NACOS_NAMESPACE="" \
    NACOS_GROUP="DEFAULT_GROUP"

EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
