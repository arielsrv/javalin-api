# syntax=docker/dockerfile:1
ARG JAVA_VERSION=21
FROM maven:3.9.12-eclipse-temurin-${JAVA_VERSION} AS build
WORKDIR /app

# Copy pom first for better layer caching
COPY pom.xml .

# Download dependencies - this layer gets cached by GHA
RUN mvn dependency:go-offline -B

# Copy source and build (uses cache mount for faster local builds)
COPY src ./src
RUN --mount=type=cache,id=maven-repo,target=/root/.m2/repository \
    mvn clean package -Dmaven.test.skip=true -DfinalName=app -B

# Runtime
FROM eclipse-temurin:${JAVA_VERSION}-jre AS runtime
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar
COPY src/main/resources/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
COPY src/main/resources/config/*.properties /config/

ENV JAVA_OPTS="-XX:-OmitStackTraceInFastThrow" \
    OTEL_SERVICE_NAME="javalin-api" \
    OTEL_RESOURCE_ATTRIBUTES="service.name=javalin-api" \
    OTEL_TRACES_EXPORTER="otlp" \
    OTEL_METRICS_EXPORTER="none" \
    OTEL_LOGS_EXPORTER="none" \
    OTEL_EXPORTER_OTLP_ENDPOINT="http://tempo.monitoring.svc.cluster.local:4317" \
    OTEL_EXPORTER_OTLP_PROTOCOL="grpc"

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
