# Eventora

Eventora is an original event-ticketing platform built as two independent Spring Boot applications.

## Architecture

| Application | Port | Database | Purpose |
| --- | ---: | --- | --- |
| Eventora main | 8080 | `eventora` / `eventora-db` | MVC UI, users, venues, events and ticket orders |
| Eventora Reviews | 8081 | `eventora_reviews` / `eventora-reviews-db` | Domain-specific event ratings and visitor reviews REST API |

The applications use separate databases. The main application consumes the reviews API through Spring Cloud OpenFeign. It invokes `GET /api/reviews/event/{eventId}`, `POST /api/reviews`, `PUT /api/reviews/{id}` and `DELETE /api/reviews/{id}`. Review creation, editing and deletion are initiated from the event details page and produce visible UI feedback.

## Technology stack

- Java 17 and Spring Boot 3.4
- Spring MVC, Thymeleaf and Bean Validation
- Spring Security with BCrypt, roles and CSRF protection
- Spring Data JPA with UUID primary keys
- Spring Cloud OpenFeign
- MySQL for production and H2 for zero-configuration local execution
- Spring Cache and Spring Scheduling
- JUnit 5, Mockito, MockMvc, integration tests and JaCoCo
- Maven and Docker Compose

## Domain functionality

- Create, edit and delete events as an administrator
- Purchase up to eight tickets per event with stock validation
- Cancel a ticket order and restore event capacity
- Apply promotional codes and redeem loyalty points
- Create, edit and delete an event review through the reviews microservice
- View event reviews loaded through the microservice
- Edit the authenticated user's own profile
- Change user roles as an administrator

Spring Security defines public pages, authenticated pages and `ADMIN`-only endpoints. Roles are `USER`, `VIP` and `ADMIN`. CSRF remains enabled. Passwords are BCrypt-hashed.

## Pages

The main application has 12 complete Thymeleaf pages: home/catalogue, event details, schedule, ticket list, ticket details, administration, event form, user administration, profile, message, error and about. Eleven use backend model data or user state; About is informational.

## Scheduling, caching and logging

- A cron job removes expired verification and password-reset tokens every night.
- A fixed-delay job refreshes event caches every five minutes.
- Event catalogue and event details use Spring's caching abstraction; mutations evict stale data.
- Every required state-changing domain functionality logs its outcome with SLF4J.

## Run locally

Start the reviews service first:

```bash
cd eventora-reviews
../mvnw spring-boot:run
```

Then start the main application in another terminal:

```bash
./mvnw spring-boot:run
```

Open `http://localhost:8080`. The reviews API is available at `http://localhost:8081/api/reviews`.

Default accounts are listed in `TEST_USERS.txt`. For MySQL, create separate schemas named `eventora` and `eventora_reviews`, then supply datasource environment variables.

## Tests

Run both suites and generate JaCoCo reports:

```bash
./mvnw clean verify
cd eventora-reviews
../mvnw clean verify
```

Reports are generated under each application's `target/site/jacoco` directory. The suites include unit, JPA integration, full-context integration and MockMvc API tests.
