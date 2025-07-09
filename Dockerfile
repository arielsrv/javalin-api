# syntax=docker/dockerfile
ARG JAVA_VERSION=21
FROM maven:3.9.10-eclipse-temurin-${JAVA_VERSION} AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar
COPY src/main/resources/opentelemetry-javaagent_v2.17.0.jar opentelemetry-javaagent.jar
COPY src/main/resources/config/*.properties /config/

ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-Dotel.resource.attributes=service.name=javalin-api","-Dotel.traces.exporter=otlp","-Dotel.metrics.exporter=none","-Dotel.logs.exporter=none","-Dotel.exporter.otlp.endpoint=http://tempo.monitoring.svc.cluster.local:4317","-Dotel.exporter.otlp.protocol=grpc","-jar","app.jar"]
