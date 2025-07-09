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

FROM gcr.io/distroless/java${JAVA_VERSION}-debian12 AS runtime
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar
COPY src/main/resources/opentelemetry-javaagent_v2.17.0.jar opentelemetry-javaagent.jar
COPY src/main/resources/config/*.properties /config/
ENV JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow"
ENV OTEL_RESOURCE_ATTRIBUTES="service.name=javalin-api" \
    OTEL_TRACES_EXPORTER=otlp \
    OTEL_METRICS_EXPORTER=none \
    OTEL_LOGS_EXPORTER=none \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://tempo.monitoring.svc.cluster.local:4317 \
    OTEL_EXPORTER_OTLP_PROTOCOL=grpc

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]

