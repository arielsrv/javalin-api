# Javalin API

This project is a Java REST API built with [Javalin](https://javalin.io/) and uses modern Java features, dependency injection with Guice, and RxJava for reactive programming. It is designed for easy deployment in containerized environments and includes observability via OpenTelemetry.

## Features
- REST endpoints with Javalin
- Dependency injection with Guice
- Reactive programming with RxJava 3
- HTTP client abstraction (`RestClient`)
- Logging with Logback
- Metrics with Micrometer and Prometheus
- OpenTelemetry agent integration (for distributed tracing)
- Docker-ready (multi-stage build)

## Requirements
- Java 21 (for local build and runtime)
- Maven 3.9+
- Docker (for containerized builds and deployment)

## Build & Run

### Locally (Java 21 required)
```sh
mvn clean package
java -jar target/app.jar
```

### With Docker
Build the image:
```sh
docker build -t javalin-api:latest .
```
Run the container:
```sh
docker run -p 8081:8081 javalin-api:latest
```

### Endpoints
- `GET /ping` — Health check
- `GET /users` — Example endpoint (fetches users from external API)
- `GET /metrics` — Prometheus metrics

## Configuration

### Base URL for RestClient
By default, the `RestClient` is designed to allow injection of a base URL (see commented code in `RestClient.java` and `AppModule.java`). To enable this:
1. Add a constructor to `RestClient` that accepts a `@Named("BaseUrl") String baseUrl`.
2. Bind the base URL in your Guice module:
   ```java
   bind(String.class)
     .annotatedWith(Names.named("BaseUrl"))
     .toInstance("https://gorest.co.in/public/v2");
   ```
3. Use relative paths in your client code.

## Observability
The Docker image includes the OpenTelemetry Java agent. You can configure the OTEL endpoint and service name via environment variables or by editing the `ENTRYPOINT` in the Dockerfile.

## Testing
Run tests with:
```sh
mvn test
```

## Main Dependencies
- Javalin 6.x
- RxJava 3
- Guice
- Jackson (JSON)
- Logback (logging)
- Micrometer (metrics)
- OpenTelemetry (tracing)

## License
MIT or your company license. 