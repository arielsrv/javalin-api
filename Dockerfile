# syntax=docker/dockerfile:1
ARG JAVA_VERSION=25
FROM maven:3.9.16-eclipse-temurin-${JAVA_VERSION} AS build
WORKDIR /app

# Copy pom and wrapper first for better layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Download ALL dependencies and plugins
# Cache mount works locally and in GHA with proper buildx setup
RUN --mount=type=cache,id=maven,target=/root/.m2/repository \
    ./mvnw dependency:go-offline dependency:resolve-plugins -B

# Copy source and build
COPY src ./src
RUN --mount=type=cache,id=maven,target=/root/.m2/repository \
    ./mvnw package -Dmaven.test.skip=true -DfinalName=app -B

# Runtime
FROM gcr.io/distroless/java25-debian13:nonroot AS runtime
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar
COPY src/main/resources/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
COPY src/main/resources/config/*.properties /config/

# JDK_JAVA_OPTIONS lo lee la JVM directamente (distroless no tiene shell para expandir JAVA_OPTS)
ENV JDK_JAVA_OPTIONS="-XX:-OmitStackTraceInFastThrow" \
    OTEL_SERVICE_NAME="javalin-api" \
    OTEL_RESOURCE_ATTRIBUTES="service.name=javalin-api" \
    OTEL_TRACES_EXPORTER="otlp" \
    OTEL_METRICS_EXPORTER="prometheus" \
    OTEL_METRICS_EXEMPLAR_FILTER="always_off" \
    OTEL_EXPORTER_PROMETHEUS_HOST="0.0.0.0" \
    OTEL_EXPORTER_PROMETHEUS_PORT="9464" \
    OTEL_LOGS_EXPORTER="none" \
    OTEL_EXPORTER_OTLP_ENDPOINT="http://alloy.monitoring:4317" \
    OTEL_EXPORTER_OTLP_PROTOCOL="grpc"

EXPOSE 8081 9464

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]