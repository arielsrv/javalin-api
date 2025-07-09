# Javalin API

This project is a Java REST API built with [Javalin](https://javalin.io/) and uses modern Java features, dependency injection with Guice, and RxJava for reactive programming. It is designed for easy deployment in containerized environments and includes observability via OpenTelemetry.

## Features
- REST endpoints with Javalin
- Dependency injection with Guice
- Reactive programming with RxJava 3
- HTTP client abstraction (`RestClient` and `RestClientFactory`)
- Centralized configuration via properties files
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

### Centralized Properties
Configuration is loaded from files in `src/main/resources/config/config.{env}.properties` based on the `ENV` environment variable (default: `local`).

Example:
```properties
app.name=javalin-api
app.port=8081
app.host=127.0.0.1
rest.client.user.base.url=https://gorest.co.in
rest.client.other.base.url=https://api.example.com
```

### Defining REST Clients
You can define multiple REST clients by adding properties with the pattern:
```
rest.client.{name}.base.url=https://example.com
```

### Using RestClientFactory
To obtain a `RestClient` for a specific service:
```java
import com.iskaypet.core.RestClientFactory;

RestClientFactory factory = ... // injected by Guice
RestClient userClient = factory.get("user");
```
This will use the base URL defined as `rest.client.user.base.url` in your properties file.

### Base Client Class
You can create your own clients by extending the abstract `Client` class:
```java
public class UserApiClient extends Client {
    public UserApiClient(RestClient restClient) {
        super(restClient);
    }
    // ... your methods
}
```

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