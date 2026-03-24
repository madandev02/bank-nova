# BankNova

BankNova is a full-stack digital banking simulation built to demonstrate production-style engineering in a portfolio context.

It showcases secure authentication, real-world transaction workflows, analytics, and a polished user experience across a modern frontend and backend architecture.

🌐 **Live Demo:** https://bank-nova.vercel.app/

---

## 🚀 Key Features

* 🔐 Secure authentication with JWT and protected routes
* 💸 Money transfer workflows with validation and business logic
* 📊 Dashboard with analytics and balance insights
* 📜 Transaction history with status tracking
* ⚡ Custom rate limiting for critical operations (auth & transfers)
* 🧪 Automated testing across frontend and backend
* 🐳 Fully Dockerized environment for local development
* ☁️ Cloud deployment (Vercel + Render)

---

## 💼 Portfolio Value

This project demonstrates:

* End-to-end full-stack delivery (React + Spring Boot + PostgreSQL)
* Secure API architecture using JWT and Spring Security
* Real-world financial domain modeling (wallets, transactions)
* Clean architecture and separation of concerns
* Testing strategies and CI-driven quality checks
* Production-ready deployment structure

---

## 🧠 Architecture

* **Frontend:** React, TypeScript, Vite, Material UI
* **Backend:** Spring Boot 3, Spring Security, Spring Data JPA
* **Database:** PostgreSQL (H2 for tests)
* **Authentication:** JWT (stateless)
* **Tooling:** Maven, Vitest, GitHub Actions, Docker Compose

---

## 🔐 Security & Reliability

Implemented controls include:

* JWT authentication and protected API routes
* Rate limiting on authentication and transfer endpoints
* Security headers:

  * HSTS
  * Referrer Policy
  * X-Content-Type-Options
  * X-Frame-Options
  * Permissions Policy
* Centralized exception handling for predictable API errors
* Backend and frontend test suites with CI checks on push and pull request

---

## ⚡ Quick Start

### 1. Run with Docker

```bash id="q1q2q3"
docker compose up --build -d
```

### 2. Access services

* Frontend → http://localhost:3000
* Backend → http://localhost:8080
* API Docs → http://localhost:8080/swagger-ui.html

**Swagger note:**

* Correct path: `/swagger-ui.html`
* `/swagger-ui/index.html` may fail depending on proxy setup

---

## 👤 Demo Accounts

Use the following seeded credentials:

| Role              | Email               | Password         |
| ----------------- | ------------------- | ---------------- |
| Primary demo user | `demo@banknova.com` | `DemoWallet123!` |
| Standard user     | `test@banknova.com` | `Password123`    |
| Receiver user     | `jane@banknova.com` | `Password123`    |

**Notes:**

* Users are seeded on backend startup (`DataInitializer`)
* Existing users are preserved

---

## 🧪 Example User Flow

1. Log in with a demo account
2. View dashboard and wallet balance
3. Perform a transfer to another user
4. Review transaction history
5. Explore analytics and features

---

## 🚀 Deployment Overview

### Production-style setup:

1. Create PostgreSQL instance (Render)
2. Deploy backend (Render - Docker)
3. Deploy frontend (Vercel)

### Backend configuration (Render):

* Root directory: `backend`
* Runtime: Docker
* Branch: `main`

**Environment variables:**

```bash id="env123"
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>
SPRING_DATASOURCE_USERNAME=<db_user>
SPRING_DATASOURCE_PASSWORD=<db_password>
JWT_SECRET=<64+ character random secret>
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

After frontend deploy:

```bash id="env456"
CORS_ALLOWED_ORIGINS=https://<your-vercel-url>,http://localhost:3000
```

**Health endpoints:**

* `/api/health`
* `/swagger-ui.html`

---

## 🗄️ Database Reset (Optional)

```bash id="dbreset"
docker compose down -v
docker compose up --build -d
```

This resets PostgreSQL and reseeds demo data.

---

## 📚 Project Documentation

* Frontend → [frontend/README.md](frontend/README.md)
* Backend → [backend/README.md](backend/README.md)
