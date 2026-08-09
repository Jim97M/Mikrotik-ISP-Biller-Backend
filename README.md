# Microservices Architecture — Documentation

A Spring Boot microservices system with service discovery, centralized configuration, synchronous (gRPC) and asynchronous (Kafka) communication, and isolated per-service databases.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Services](#services)
3. [Communication Patterns](#communication-patterns)
4. [Data Layer](#data-layer)
5. [Infrastructure Components](#infrastructure-components)
6. [Project Structure](#project-structure)
7. [Running the System](#running-the-system)
8. [Environment Variables](#environment-variables)
9. [Troubleshooting](#troubleshooting)
10. [Roadmap / Future Improvements](#roadmap--future-improvements)

---

## Architecture Overview

This system follows a standard microservices pattern:

- **Service discovery** via Eureka — services find each other by name, not hardcoded addresses.
- **Centralized configuration** via Spring Cloud Config Server — config lives in one place instead of duplicated across services.
- **Synchronous communication** via gRPC — for fast, blocking calls like permission checks.
- **Asynchronous communication** via Kafka — for decoupled, event-driven workflows like payments and notifications.
- **Database-per-service** — each service owns its data exclusively; no service queries another's database directly.
- **Single entry point** via an API Gateway — external clients talk to one address, which routes internally.

```
                        ┌─────────────────┐
                        │   API Gateway    │  ← external traffic enters here
                        └────────┬─────────┘
                                 │
                 ┌───────────────┼───────────────┐
                 │               │               │
          ┌──────▼─────┐  ┌──────▼──────┐  ┌─────▼──────────┐
          │ auth-service│  │payment-svc  │  │notification-svc│
          │  (gRPC+REST)│  │  (REST)     │  │ (Kafka+REST)   │
          └──────┬─────┘  └──────┬──────┘  └─────┬──────────┘
                 │               │               │
             gRPC│           Kafka events    Kafka events
                 │               │               │
                 └───────────────┴───────────────┘
                                 │
                        ┌────────▼────────┐
                        │  Eureka Server   │  ← all services register here
                        └──────────────────┘
                        ┌──────────────────┐
                        │  Config Server   │  ← all services pull config here
                        └──────────────────┘
```

Each service also has its **own dedicated Postgres database** (not shown above for clarity) — see [Data Layer](#data-layer).

---

## Services

| Service | Responsibility | Protocol(s) | Datastore |
|---|---|---|---|
| **eureka-server** | Service registry / discovery | HTTP (dashboard + registration API) | none |
| **config-server** | Centralized configuration | HTTP | Git-backed config repo |
| **auth-service** | User roles & permission checks | gRPC (server) + REST | Postgres (`auth_db`) |
| **payment-service** | Payment processing | REST (client of auth-service gRPC), Kafka (producer) | Postgres (`payment_db`) |
| **notification-service** | OTPs, email/SMS notifications | Kafka (consumer), REST (OTP verify) | Redis (OTP/TTL) + Postgres (notification log) |
| **api-gateway** | Single entry point, routing | REST | none (stateless router) |

---

## Communication Patterns

### Why mixed sync + async?

| Need | Pattern used | Reason |
|---|---|---|
| "Is this user allowed to do X?" | **gRPC** (auth-service) | Caller needs an immediate answer before proceeding; low-latency binary protocol |
| "Payment was made, notify + update ledger" | **Kafka** (payment-service → notification-service) | Multi-step, shouldn't block the caller, needs retry/durability |
| "Send this OTP" | **Kafka** (event → notification-service) | Fire-and-forget from the caller's perspective |
| "Check if the OTP the user typed is correct" | **REST/sync** (notification-service exposes an endpoint) | User is waiting on-screen for a yes/no |

### gRPC (auth-service)

- Contract defined in a shared `.proto` file (`auth.proto`)
- `auth-service` implements the gRPC server (`AuthServiceGrpc.AuthServiceImplBase`)
- Client services (e.g., `payment-service`) use a generated blocking stub via `@GrpcClient`
- In Docker/Eureka setups, the client resolves the server via `discovery:///auth-service` instead of a hardcoded host:port

### Kafka (payment-service, notification-service)

- Kafka runs in **KRaft mode** — no Zookeeper dependency
- Event-driven flow example:
  ```
  payment-service publishes "PaymentConfirmed"
        → notification-service consumes it → sends email/SMS
  ```
- Consumers are idempotent (safe to process the same message twice, since Kafka guarantees at-least-once delivery)
- Failed messages route to a dead-letter topic rather than being silently dropped

---

## Data Layer

**Database-per-service** — no service ever directly queries another service's database.

| Service | Database | Why this choice |
|---|---|---|
| auth-service | Postgres (`auth_db`) | Relational integrity for users/roles/permissions |
| payment-service | Postgres (`payment_db`) | ACID transactions required for financial data |
| notification-service | Redis (OTPs) + Postgres (notification log) | Redis gives native TTL for short-lived OTPs; Postgres gives durable audit history |

**Cross-service consistency** is handled through:
- **Event-driven sync** — services publish events (Kafka) when their data changes; interested services keep a local, denormalized copy of only what they need.
- **Saga pattern** — multi-service transactions are broken into local transactions + compensating actions on failure, since a single ACID transaction can't span multiple databases.
- **Transactional Outbox pattern** (recommended, not yet implemented) — write business data and the outgoing event to the same local transaction, then a separate process publishes to Kafka reliably.

---

## Infrastructure Components

### Eureka Server
Service registry. Every service registers itself under a name (`spring.application.name`) on startup, and other services look each other up by that name instead of a fixed address.

- Dashboard: `http://localhost:8761`

### Config Server
Centralizes `application.yml` values across all services in one Git-backed repository, so config changes don't require touching every service individually.

- Endpoint: `http://localhost:8888`
- Secured with Basic Auth in production (see [Environment Variables](#environment-variables))
- Optional: Spring Cloud Bus (Kafka-backed) to broadcast config refresh to all services at once via `/actuator/refresh`

### Kafka (KRaft mode)
Message broker for async communication. Runs without Zookeeper (KRaft mode — Kafka 3.x+), reducing operational complexity.

- Broker: `kafka:29092` (internal), `localhost:9092` (host access)
- Kafka UI (topic/message browser): `http://localhost:8089`

### Redis
Used by `notification-service` for OTP storage with native TTL (auto-expiry, no cleanup job needed) and for OTP request rate limiting.

- `localhost:6379`

### Postgres (one instance per service)
Each service gets its own isolated Postgres container — separate credentials, separate volume, separate connection pool.

| Service DB | Host port |
|---|---|
| auth-db | 5433 |
| payment-db | 5434 |
| notification-db | 5435 |

### API Gateway
Single external entry point (`localhost:8080`). Routes incoming requests to the correct internal service using Eureka-based discovery, so clients never need to know internal service addresses.

---

## Project Structure

```
my-microservices/
├── docker-compose.yml
├── proto-contracts/                # shared .proto definitions
│   └── src/main/proto/
│       └── auth.proto
│
├── eureka-server/
│   ├── Dockerfile
│   └── src/main/java/.../EurekaServerApplication.java
│
├── config-server/
│   ├── Dockerfile
│   └── src/main/java/.../ConfigServerApplication.java
│
├── auth-service/                   # gRPC server + REST
│   ├── Dockerfile
│   └── src/main/java/com/example/auth/
│       ├── AuthServiceApplication.java
│       ├── grpc/AuthGrpcService.java
│       ├── service/PermissionService.java
│       └── repository/UserRoleRepository.java
│
├── payment-service/                 # gRPC client + Kafka producer
│   ├── Dockerfile
│   └── src/main/java/com/example/payment/
│       ├── PaymentServiceApplication.java
│       ├── client/AuthClient.java
│       ├── controller/PaymentController.java
│       └── service/PaymentService.java
│
├── notification-service/            # Kafka consumer + REST (OTP verify)
│   ├── Dockerfile
│   └── src/main/java/com/example/notification/
│       ├── NotificationServiceApplication.java
│       ├── kafka/OtpRequestedConsumer.java
│       ├── kafka/PaymentConfirmedConsumer.java
│       ├── service/OtpService.java
│       ├── provider/TwilioSmsProvider.java
│       └── controller/OtpVerifyController.java
│
└── api-gateway/
    ├── Dockerfile
    └── src/main/java/.../ApiGatewayApplication.java
```

Each service is a self-contained Spring Boot application with its own `Dockerfile`, referenced by `build.context` in `docker-compose.yml`.

---

## Running the System

### Prerequisites
- Docker + Docker Compose installed
- Java 17+, Maven (if building/running services outside Docker)

### Option A — Full stack via Docker Compose (recommended)

Compose reads the dependency graph (`depends_on` + healthchecks) and starts everything in the correct order automatically:

```bash
docker compose up -d --build
```

Startup order enforced by the compose file:
1. Infrastructure — Kafka, Redis, all Postgres instances
2. Eureka Server
3. Config Server (waits for Eureka to be healthy)
4. Business services — auth-service, payment-service, notification-service
5. API Gateway (waits for Eureka + all business services)

Watch logs live instead of detached:
```bash
docker compose up --build
```

Check container status/health:
```bash
docker compose ps
```

Tail logs for one service:
```bash
docker compose logs -f auth-service
```

Stop everything:
```bash
docker compose down
```

Stop and wipe all data volumes (fresh start):
```bash
docker compose down -v
```

### Option B — Running services locally (e.g., from an IDE during development)

1. Start infrastructure only, via Docker:
   ```bash
   docker compose up -d kafka redis auth-db payment-db notification-db
   ```
2. Start **Eureka Server** first — wait until `http://localhost:8761` shows the dashboard.
3. Start **Config Server** next — wait until `http://localhost:8888/actuator/health` returns `UP`.
4. Start the business services in any order — `auth-service`, `payment-service`, `notification-service`.
5. Start **API Gateway** last.
6. Confirm each service appears on the Eureka dashboard within a few seconds of starting.

---

## Environment Variables

Each service reads these at startup (via `application.yml` + Docker Compose `environment:` block):

| Variable | Purpose | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `docker` |
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://auth-db:5432/auth_db` |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | DB credentials | — |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka registration URL | `http://eureka-server:8761/eureka/` |
| `SPRING_CONFIG_IMPORT` | Config Server URL | `optional:configserver:http://config-server:8888` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:29092` |
| `SPRING_DATA_REDIS_HOST` / `PORT` | Redis connection (notification-service only) | `redis` / `6379` |
| `GRPC_CLIENT_AUTH-SERVICE_ADDRESS` | gRPC target for auth-service (payment-service only) | `discovery:///auth-service` |

**Production note:** Config Server should be secured with Basic Auth (Spring Security) — an open Config Server exposes every service's DB credentials and API keys to anyone who can reach it.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `service X depends on undefined service Y` | `depends_on` references a service name that doesn't match any top-level key under `services:` | Check for typos/leftover names after a rename |
| `container name "X" is already in use` | Two services set the same `container_name` | Give each service a unique `container_name` |
| Service crashes on startup, can't reach Eureka/Config Server | Dependent service started before Eureka/Config Server was actually *ready* (not just "started") | Add a healthcheck + `condition: service_healthy` in `depends_on`; add `restart: on-failure` |
| `database "X" does not exist` | A service's datasource URL points to a database that was never created by its Postgres container's `POSTGRES_DB` | Give the service its own Postgres container, or add an init script to create the extra DB/user |
| Port already in use on host | Two services (or a local process) mapped to the same host port | Change the left-hand side of `ports: "HOST:CONTAINER"` for one of them |
| `version` attribute warning | Compose V2 no longer needs `version: '3.8'` | Delete the line — Compose ignores it anyway |

Useful diagnostic commands:
```bash
# List all container names + host port mappings to spot duplicates
grep -n "container_name:\|- \"[0-9]" docker-compose.yml

# See which containers are unhealthy or restarting
docker compose ps

# Check a specific service's logs
docker compose logs -f <service-name>
```

---

## Roadmap / Future Improvements

- [ ] Transactional Outbox pattern for reliable event publishing (payment-service, notification-service)
- [ ] Dead-letter topic handling + alerting for failed Kafka messages
- [ ] Distributed tracing (Micrometer Tracing + Zipkin/Jaeger) across gRPC and Kafka calls
- [ ] Resilience4j circuit breakers on the gRPC client (payment-service → auth-service)
- [ ] Spring Cloud Bus for one-shot config refresh across all services
- [ ] TLS for gRPC and Config Server in non-local environments
- [ ] Rate limiting on OTP request endpoint (Redis counter with TTL window)
