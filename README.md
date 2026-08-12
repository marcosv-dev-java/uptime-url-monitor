# Uptime URL Monitor

A REST API that monitors URL availability, tracking uptime, response time, and status history with scheduled checks and JWT authentication.

## Tech Stack

- Java 21
- Spring Boot 4.0.7
- Spring Security + JWT (Auth0 java-jwt)
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway
- SpringDoc OpenAPI (Swagger)
- Maven

## Features

- JWT authentication with role-based access control (USER / ADMIN)
- IDOR prevention on all monitor endpoints
- Scheduled URL checks with per-monitor configurable intervals
- Check history with pagination
- Uptime percentage calculation for any time period
- Soft delete with inactive monitor listing
- Timed pause/unpause for monitors
- Decorative frontend (vanilla HTML/JS)

## Prerequisites

- Java 21
- PostgreSQL 14+
- Maven 3.9+

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/marcosv-dev-java/uptime-url-monitor.git
cd uptime-url-monitor
```

### 2. Create the database

```sql
CREATE DATABASE db_uptime_monitor;
```

### 3. Configure environment variables

The application reads database credentials and the JWT secret from environment variables. Set the following before running:

```bash
DB_USERNAME=your_postgres_username
DB_PASSWORD=your_postgres_password
JWT_SECRET=your_secret_key_at_least_32_characters
```

Or create a `.env` file and export them in your shell session.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Flyway will automatically run all migrations on startup and create the required tables.

### 5. Open the frontend

Open `frontend/index.html` in your browser via a local server (e.g. IntelliJ's built-in server or VS Code Live Server).

---

## API Endpoints

### Auth

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | Public | Register a new user, returns JWT |
| POST | `/auth/login` | Public | Authenticate, returns JWT |

### Monitors

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/monitors` | User | List authenticated user's active monitors |
| GET | `/monitors/{id}` | Owner | Get monitor by ID |
| POST | `/monitors` | User | Create a new monitor |
| PUT | `/monitors/{id}` | Owner | Update monitor name, URL, or interval |
| DELETE | `/monitors/{id}` | Owner | Soft delete a monitor |
| GET | `/monitors/inactive` | User | List user's deleted monitors |
| PUT | `/monitors/{id}/pause` | Owner | Pause monitor for a given duration |
| PUT | `/monitors/{id}/unpause` | Owner | Force resume a paused monitor |

### Check History

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/monitors/{id}/checks?pageNumber=0` | Owner | Paginated check history (10 per page, newest first) |
| GET | `/monitors/{id}/checks/uptime-percentage?from=&to=` | Owner | Uptime percentage for a time period |

### Admin

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/admin/user/{username}/monitors` | ROLE_ADMIN | List all monitors for a specific user |

---

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Tokens are obtained from `/auth/login` or `/auth/register` and expire after 2 hours.

---

## Database Migrations

Flyway manages all schema changes. Migration files are in `src/main/resources/db/migration`:

| Version | Description |
|---------|-------------|
| V1 | Create users table |
| V2 | Create monitors table |
| V3 | Create check_results table |
| V4 | Add index on next_check_due |
| V5 | Add paused_until column to monitors |
| V6 | Increase error_message column length |

---

## Scheduler

The application runs a background scheduler every 30 seconds that checks all monitors whose `nextCheckDue` has passed. Each check records the HTTP status, response time, and success/failure in the `check_results` table, then updates the monitor's status and schedules the next check based on its configured interval.

Paused monitors are skipped until their `pausedUntil` timestamp has passed.

---

## Security Notes

- Passwords are hashed with BCrypt
- JWT tokens include username and role as claims
- Every monitor endpoint with `{id}` validates ownership before responding — accessing another user's monitor returns 403
- CSRF is disabled intentionally: the API is stateless (JWT, no session cookies), so there is no CSRF surface to protect
- CORS is configured to allow only `http://localhost:63342` in development

---

## Repository

[github.com/marcosv-dev-java/uptime-url-monitor](https://github.com/marcosv-dev-java/uptime-url-monitor)