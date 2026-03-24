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

## Render Backend Deployment (Production-Style)

Create resources in this order:

1. Render Postgres
2. Render Web Service (backend)
3. Vercel frontend (after backend is live)

For Render Web Service, use:

- Root directory: `backend`
- Runtime: `Docker`
- Branch: `main`

Set these environment variables in Render Web Service:

- `SPRING_PROFILES_ACTIVE=docker`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>`
- `SPRING_DATASOURCE_USERNAME=<db_user>`
- `SPRING_DATASOURCE_PASSWORD=<db_password>`
- `JWT_SECRET=<64+ character random secret>`
- `CORS_ALLOWED_ORIGINS=http://localhost:3000` (update to include Vercel URL after frontend deploy)

Render sets `PORT` automatically. The backend now reads that value directly.

After frontend deployment, update CORS:

- `CORS_ALLOWED_ORIGINS=https://<your-vercel-url>,http://localhost:3000`

Health / verification endpoints:

- `https://<backend-url>/api/health`
- `https://<backend-url>/swagger-ui.html`

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
