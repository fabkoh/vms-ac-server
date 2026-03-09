# vms-ac-server

Backend for the **Visitor Management System and Access Control (VMS-AC)**.

Built with **Spring Boot 2.7**, **Java 11+**, and **Maven**.

---

## Prerequisites

| Tool | Min version | Check |
|------|-------------|-------|
| Java (JDK) | 11 | `java -version` |
| Maven | 3.8 | `mvn -version` |

> The repo includes an `mvnw` wrapper so a system-level Maven install is optional.
> If using `mvnw`, first run: `chmod +x mvnw`

---

## Repository Structure

```
vms-ac-server/
├── src/
│   ├── main/
│   │   ├── java/com/vmsac/vmsacserver/
│   │   │   ├── controller/   # REST API endpoints
│   │   │   ├── model/        # JPA entities + DTOs
│   │   │   ├── repository/   # Spring Data JPA interfaces
│   │   │   ├── service/      # Business logic
│   │   │   ├── security/     # JWT auth + Spring Security config
│   │   │   ├── config/       # App-level configuration beans
│   │   │   ├── util/         # Data loaders, helpers
│   │   │   └── payload/      # Request/response POJOs
│   │   └── resources/
│   │       ├── application.properties            # Base config (port 8082)
│   │       ├── application-dev.properties        # Dev profile (H2 in-memory)
│   │       ├── application-production.properties # Prod profile (PostgreSQL)
│   │       ├── schema-h2.sql                     # H2 schema init
│   │       └── schema-postgresql.sql             # PostgreSQL schema init
│   └── test/                 # Unit/integration tests
├── pom.xml                   # Maven dependencies
└── mvnw / mvnw.cmd           # Maven wrapper
```

---

## Compile the Java Code

Just compile (no packaging):
```bash
mvn compile
```

---

## Running in Development (H2 in-memory DB)

No external database needed — uses an embedded H2 database.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Or with the Maven wrapper:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API base: **http://localhost:8082**
- Swagger UI: **http://localhost:8082/swagger-ui/**
- H2 console: **http://localhost:8082/h2-console**

### H2 Console Login

On first startup, the app auto-writes a **"VMS-AC Dev"** preset into `~/.h2.server.properties`.

1. Open **http://localhost:8082/h2-console**
2. JDBC URL and username (`Admin`) are pre-filled, password is empty

---

## Building for Production (JAR)

**Step 1 — Package:**
```bash
mvn clean package -DskipTests
```
This compiles, runs tests (skipped here), and produces:
```
target/vms-ac-backend-0.0.1-SNAPSHOT.jar
```

**Step 2 — Configure PostgreSQL** in `src/main/resources/application-production.properties`:
```properties
spring.datasource.url=jdbc:postgresql://<HOST>:5431/vms_ac_db
spring.datasource.username=postgres
spring.datasource.password=<your_password>
jasypt.encryptor.password=<your_encryption_key>
```

**Step 3 — Run the JAR:**
```bash
java -jar target/vms-ac-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Compile only | `mvn compile` |
| Run dev | `mvn spring-boot:run -Dspring-boot.run.profiles=dev` |
| Run tests | `mvn test` |
| Package JAR | `mvn clean package -DskipTests` |
| Run JAR (dev) | `java -jar target/*.jar --spring.profiles.active=dev` |
| Run JAR (prod) | `java -jar target/*.jar --spring.profiles.active=production` |
