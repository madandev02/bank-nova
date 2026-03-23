# BankNova

BankNova is a full-stack digital banking simulation built to demonstrate production-style engineering in a portfolio context.

It focuses on secure authentication, money transfer workflows, analytics, and a polished user experience across frontend and backend.

## Portfolio Value

This project demonstrates:

- End-to-end full-stack delivery (React + Spring Boot + PostgreSQL)
- Secure API architecture with JWT and Spring Security
- Transaction and analytics business flows
- Automated testing and CI quality gates
- Dockerized local environment and deploy-ready structure

This is a strong project for junior full-stack interviews, especially when paired with a second project in a different domain.

## Architecture

- Frontend: React, TypeScript, Vite, Material UI
- Backend: Spring Boot 3, Spring Security, Spring Data JPA
- Database: PostgreSQL (H2 for tests)
- Auth: JWT bearer tokens
- Tooling: Maven, Vitest, GitHub Actions, Docker Compose

## Security and Reliability

Implemented controls include:

- JWT authentication and protected API routes
- Rate limiting on authentication and transfer endpoints
- Security headers (HSTS, Referrer-Policy, X-Content-Type-Options, X-Frame-Options, Permissions-Policy)
- Global exception handling for predictable API errors
- Backend and frontend test suites with CI checks on push and pull request

## Quick Start

1. Run with Docker
   - `docker compose up --build -d`
2. Frontend
   - http://localhost:3000
3. Backend
   - http://localhost:8080
4. API docs
   - http://localhost:8080/swagger-ui.html

## Demo Accounts

Use the following seeded credentials to sign in immediately after startup.

| Role              | Email               | Password         |
| ----------------- | ------------------- | ---------------- |
| Primary demo user | `demo@banknova.com` | `DemoWallet123!` |
| Standard user     | `test@banknova.com` | `Password123`    |
| Receiver user     | `jane@banknova.com` | `Password123`    |

Notes:

- Demo users are seeded by backend startup logic (`DataInitializer`).
- If a user already exists in your database, it is preserved and not overwritten.

## Professional Onboarding Flow

1. Start the stack
   - `docker compose up --build -d`
2. Open the application
   - Frontend: http://localhost:3000
3. Sign in with one of the demo accounts above
4. Validate core flows
   - Dashboard and wallet balance
   - Transfer (with optional email OTP verification)
   - Features page (cards, beneficiaries, spending limits, loans)

## Database Refresh (Optional)

If your local database is old and you want a clean demo state:

1. Stop and remove containers + volumes
   - `docker compose down -v`
2. Rebuild and start
   - `docker compose up --build -d`

This recreates PostgreSQL data and re-seeds demo records.

## Project Documentation

- Frontend guide: [frontend/README.md](frontend/README.md)
- Backend guide: [backend/README.md](backend/README.md)

## Interview Positioning

In interviews, present BankNova as:

- A secure transaction-focused product, not just a CRUD demo
- Evidence of debugging and iterative hardening
- Proof of test-driven quality and deployment readiness

A second project with strong backend domain logic (like healthcare scheduling with anti-double-booking) will elevate your profile to strong junior or junior-plus.
