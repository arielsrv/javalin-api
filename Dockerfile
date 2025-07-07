# syntax=docker/dockerfile
ARG JAVA_VERSION=21
FROM maven:3.9.10-eclipse-temurin-${JAVA_VERSION} AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN mvn clean install

FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
COPY src/main/resources/opentelemetry-javaagent_v2.17.0.jar opentelemetry-javaagent.jar

ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-Dotel.resource.attributes=service.name=mi-javalin","-Dotel.traces.exporter=otlp","-Dotel.exporter.otlp.endpoint=http://tempo.monitoring.svc.cluster.local:4317","-Dotel.exporter.otlp.protocol=grpc","-jar","app.jar"]
