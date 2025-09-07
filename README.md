# URL Shortener API (Kotlin + Spring Boot)

A production-ready **URL Shortener API** built with **Kotlin, Spring Boot, and JPA**.  
This service provides endpoints to shorten long URLs, resolve short URLs, list all stored mappings, and redirect users.

---

## 🚀 Features

- Shorten long URLs into unique slugs
- Resolve slug → original URL
- Redirect short URL directly
- Pagination and sorting for listing URLs
- Default **expiry = 30 days** after creation
- Hit counter to track visits
- Configurable `base-url`

---

## 🛠 Tech Stack

- **Kotlin**
- **Spring Boot** (Web, Data JPA, Validation)
- **PostgreSQL/MySQL** (with JPA/Hibernate)
- **Flyway** for DB migrations
- **Maven**

---

## 📌 API Endpoints

### 1. List all URLs (paginated)

GET /api/v1/urls

### 2. Create a short URL

POST /api/v1/shorten

### 3. Get original URL + stats

GET /api/v1/urls/{slug}

### 4. Redirect to original URL

GET /{slug}

---

## 📊 Database Schema

```sql
CREATE TABLE url_table (
    id BIGSERIAL PRIMARY KEY,
    short_key VARCHAR(10) NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() + interval '30 days'),
    hits BIGINT NOT NULL DEFAULT 0
);
```

---

## 🧪 Running Locally

mvn clean install
mvn spring-boot:run
