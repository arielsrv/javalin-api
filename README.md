# Javalin API

[![Java CI](https://github.com/arielsrv/javalin-api/actions/workflows/maven.yml/badge.svg)](https://github.com/arielsrv/javalin-api/actions/workflows/maven.yml)
[![Docker Image CI](https://github.com/arielsrv/javalin-api/actions/workflows/docker-image.yml/badge.svg)](https://github.com/arielsrv/javalin-api/actions/workflows/docker-image.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-25-blue.svg)](https://openjdk.org/projects/jdk/25/)

This project is a high-performance Java REST API built with [Javalin](https://javalin.io/). It leverages modern Java 25 features, dependency injection with Guice, and RxJava 3 for reactive programming.

Designed for cloud-native environments, it includes observability via OpenTelemetry, Prometheus metrics, and first-class Kubernetes support.

## 🚀 Features

- **Modern Java**: Utilizes Java 25 and Virtual Threads (Project Loom) for efficient concurrency.
- **Reactive Stack**: Asynchronous request handling with RxJava 3.
- **Dependency Injection**: Robust DI using Google Guice.
- **Observability**: Distributed tracing with OpenTelemetry and metrics with Micrometer/Prometheus.
- **Documentation**: Automated OpenAPI (Swagger/ReDoc) generation.
- **Resilient Clients**: Abstracted REST client with snake_case support and automatic mapping.
- **DevOps Ready**: Multi-stage Docker builds and Kustomize-based Kubernetes manifests.
- **Task Automation**: Simplified workflow using `Taskfile`.

## 🛠 Requirements

- **Java 25** (Temurin distribution recommended)
- **Maven 3.9+**
- **Docker** & **Buildx**
- **Task** (optional, but recommended: [Taskfile.dev](https://taskfile.dev/))
- **kubectl**, **kustomize**, **mkcert** (for Kubernetes deployment)

## 🏗 Build & Run

### Using Task (Recommended)

The project uses `Taskfile.yml` to automate common operations:

```sh
task build          # Build the project (skipping tests)
task docker:debug   # Build Docker image locally
task docker:run     # Run the application in Docker
```

### Manually

```sh
# Local build
./mvnw clean package

# Run app
java -jar target/app.jar

# Docker Build
docker build -t javalin-api:latest --build-arg JAVA_VERSION=25 .
```

## 🌐 API Endpoints

Once running, the API is available at `http://localhost:8081`.

- `GET /ping` — Health check (returns "pong")
- `GET /users` — Fetches users from external API (reactive)
- `GET /metrics` — Prometheus format metrics
- `GET /swagger` — Interactive API documentation
- `GET /redoc` — Alternative API documentation
- `GET /openapi` — OpenAPI 3.0 specification (JSON)

## 🚢 Kubernetes Support

Deploy the entire stack with a single command:

```sh
task k:run
```

This task performs:
1. Docker image build.
2. Manifest generation with Kustomize.
3. Namespace and Secret creation (including TLS with `mkcert`).
4. Deployment to the current cluster.
5. Rollout status verification.

### Key Kubernetes Tasks
- `task k:tls`: Generates and applies TLS secrets for Ingress.
- `task k:apply`: Applies manifests and restarts deployment.
- `task k:ping`: Pings the service through the Ingress.

## ⚙️ Configuration

Configuration is environment-based, loading files from `src/main/resources/config/config.{env}.properties`.

Key properties:
- `app.port`: API port (default 8081).
- `app.host`: Binding host.
- `rest.client.{name}.base.url`: Dynamic REST client configuration.

## 📊 Observability

The application includes the **OpenTelemetry Java Agent** for distributed tracing.
Configuration is managed via environment variables (see `Dockerfile` or `Taskfile.yml` for defaults):

- `OTEL_EXPORTER_OTLP_ENDPOINT`: Target collector (default: Tempo).
- `OTEL_SERVICE_NAME`: `javalin-api`.

## 🧪 Testing

The project maintains high standards with JUnit 5, Mockito, and AssertJ.

```sh
./mvnw test
```

## 📁 Project Structure

```text
├── .github/workflows   # CI/CD Pipelines (GHA)
├── src/main/java       # Source code
│   ├── controllers     # REST Endpoints
│   ├── core            # Server config, DI, Custom Mappers
│   ├── dto             # Data Transfer Objects
│   ├── services        # Business Logic
│   └── modules         # Guice Modules
├── src/main/resources  # Config and K8s manifests
├── Dockerfile          # Multi-stage build (Java 25)
├── Taskfile.yml        # Task automation
└── pom.xml             # Maven dependencies
```

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.