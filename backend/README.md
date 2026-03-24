# BankNova Backend

Robust backend service simulating a digital banking system, built with Spring Boot.
This API provides secure, scalable endpoints for authentication, wallet management, transactions, analytics, and user profile operations.

---

## 🚀 Key Features

* 🔐 Stateless JWT Authentication with Spring Security
* ⚡ Custom rate limiting system using Spring AOP (per user/IP strategies)
* 🧪 Unit & Slice Testing strategy (`@WebMvcTest`, `@DataJpaTest`)
* 🏦 Wallet and transaction management (banking domain simulation)
* 📊 Transaction history and analytics endpoints
* 🛡️ Security headers (HSTS, X-Frame-Options, Referrer Policy, etc.)
* 📄 OpenAPI/Swagger documentation
* ❗ Centralized exception handling for consistent API responses

---

## 🧠 Architecture

Layered architecture:

Controller → Service → Repository → Database

* Spring Data JPA for persistence
* PostgreSQL for production environment
* H2 in-memory database for testing
* Clean separation of concerns across layers

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot 3.3
* Spring Security
* Spring Data JPA
* PostgreSQL (runtime)
* H2 (tests)
* JWT (jjwt)
* OpenAPI / Swagger

---

## 🔐 Security Controls

* Stateless authentication using JWT
* Authorization rules for protected endpoints
* Custom rate limiting using AOP annotations
* Rate limiting applied to:

  * Login and register endpoints
  * Transfer operations
* Security headers configured in HTTP filter chain:

  * HSTS
  * Referrer Policy
  * Permissions Policy
  * X-Content-Type-Options
  * X-Frame-Options
* Centralized exception handling for consistent error responses

---

## 📡 API Endpoints

Main endpoint groups:

* Authentication → `/api/v1/auth/*`
* Wallet → `/api/v1/wallet/*`
* Transactions → `/api/v1/transactions/*`
* Users / Profile → `/api/v1/users/*`
* Health → `/api/v1/health`

---

## ▶️ Running Locally

### 1. Run tests

```
./mvnw test
```

### 2. Start application

```
./mvnw spring-boot:run
```

---

## 📄 API Documentation (Swagger)

```
http://localhost:8080/swagger-ui.html
```

**Notes:**

* Correct UI endpoint: `/swagger-ui.html`
* API docs endpoint: `/api-docs`
* `/swagger-ui/index.html` may not work behind some proxies

---

## 📦 Build

Package the application:

```
./mvnw clean package
```

---

## 🧪 Testing

* Unit and slice tests for:

  * Services
  * Repositories
  * Controllers

Run all tests:

```
./mvnw test
```

---

## 🗄️ Data Layer

* Core domain entities:

  * User
  * Wallet
  * Transaction

* PostgreSQL used for runtime persistence

* H2 used for testing profile (fast execution)

---

## 🎯 Technical Decisions 

* **JWT over sessions** → Enables stateless and scalable authentication
* **Spring AOP for rate limiting** → Handles cross-cutting concerns cleanly
* **Test slices (`@WebMvcTest`, `@DataJpaTest`)** → Faster and more focused tests
* **Layered architecture** → Improves maintainability and scalability

---

## 💡 Purpose

This project was built as part of a fullstack portfolio to simulate a real-world backend system in the financial domain, focusing on security, scalability, and clean architecture using Java and Spring Boot.

---

## 🌐 Related

Frontend deployed on Vercel
Backend deployed on Render
