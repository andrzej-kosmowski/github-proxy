# GitHub Proxy

REST service that fetches repository data from the GitHub API and lets authenticated users keep local copies of it in PostgreSQL.
The project is a multi-module Maven build in **hexagonal architecture**, secured as an **OAuth2 resource server** (JWT issued by Keycloak) and shipped with a **CI/CD pipeline** that includes quality and security scanning.

## Features

- Read a repository from GitHub (`full name`, `description`, `clone URL`, `stars`, `creation date`)
- Save it locally, read the local copy, update it and delete it
- Role-based access: `USER` can read, save and update, `ADMIN` can also delete
- Calls to GitHub go through a Feign client with a circuit breaker, fallback and error decoder
- Liquibase migrations, structured logging for ELK, Docker Compose environment

## Architecture

Multi-module Maven project (hexagonal / ports and adapters):

| Module | Responsibility |
| --- | --- |
| `model` | Domain model and domain exceptions |
| `public` | Ports (in and out) and DTOs, the contract between the domain and the outside world |
| `domain` | Business logic (`RepositoryService`), depends only on ports |
| `adapter` | REST controller, GitHub client, JPA persistence, security configuration, exception handling |
| `bootstrap` | Spring Boot application that wires everything together |

The domain does not know about the database, GitHub or HTTP; adapters implement the outgoing ports (`GithubClientPort`, `RepositoryProviderPort`).

## Tech stack

Java 21, Spring Boot, Spring Security (OAuth2 resource server, JWT), Keycloak, Spring Data JPA, PostgreSQL, Liquibase, Spring Cloud OpenFeign, Resilience4j, MapStruct, Lombok, Docker.
Tests: JUnit 5, Mockito, MockMvc, Spring Security Test.

## Security

- Stateless: every request needs a `Bearer` JWT issued by Keycloak (realm `github-proxy-app`)
- Roles are read from the token (`realm_access` and `resource_access` claims) and mapped to `ROLE_USER` and `ROLE_ADMIN`
- Method-level authorization with `@PreAuthorize`; custom 401 and 403 responses
- Every endpoint requires authentication

## Getting started

Requirements: JDK 21, Docker.

```bash
# 1. Build the jar (the Dockerfile copies it from bootstrap/target/)
./mvnw clean package -DskipTests

# 2. Create the external network used by docker-compose (once)
docker network create elk-network

# 3. Start the application, PostgreSQL, pgAdmin and Keycloak
docker compose up --build
```

| Service | URL |
| --- | --- |
| API | http://localhost:8080 |
| Keycloak admin console | http://localhost:8180 (admin / admin) |
| pgAdmin | http://localhost:8888 (demo@example.com / secret) |

### Keycloak setup

The realm is not imported automatically. After the first start, in the Keycloak admin console:

1. Create the realm `github-proxy-app`
2. Create a client `github-proxy-app` (enable direct access grants if you want to use the password flow below)
3. Create the realm roles `USER` and `ADMIN`
4. Create a user and assign a role to it

### Getting a token and calling the API

```bash
TOKEN=$(curl -s -X POST http://localhost:8180/realms/github-proxy-app/protocol/openid-connect/token \
  -d grant_type=password -d client_id=github-proxy-app \
  -d username=<user> -d password=<password> | jq -r .access_token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/repositories/spring-projects/spring-boot
```

## Endpoints

| Method | Path | Role | Description |
| --- | --- | --- | --- |
| `GET` | `/repositories/{owner}/{repositoryName}` | USER | Fetch a repository from GitHub |
| `POST` | `/repositories/{owner}/{repositoryName}` | USER | Fetch a repository from GitHub and save it locally |
| `GET` | `/local/repositories/{owner}/{repositoryName}` | USER | Read the local copy |
| `PUT` | `/repositories/{owner}/{repositoryName}` | USER | Update the local copy (`fullName`, `description`, `cloneUrl`, `stars`) |
| `DELETE` | `/repositories/{owner}/{repositoryName}` | ADMIN | Delete the local copy |
| `GET` | `/me` | any authenticated | Information from the current token |

## Tests

```bash
./mvnw verify
```

## CI/CD

GitHub Actions workflow (`.github/workflows/maven.yml`), run on pushes and pull requests:

1. **Build and test**: `mvn verify`, upload of the jar
2. **SonarQube Cloud**: static analysis
3. **Trivy repository scan**: vulnerabilities, secrets and misconfigurations (fails on HIGH and CRITICAL)
4. **Docker**: image build, Trivy image scan, SBOM (CycloneDX), push to GHCR and image signing with Cosign (on `main`)
5. **Artifact publishing demo**: simulated upload of the jar to an artifact repository

## Branches

Work is done on ticket-based feature branches (for example `feature/GH-01`) merged through `stage`.
