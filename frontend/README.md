# BankNova Frontend

Frontend application for BankNova built with React and TypeScript.

## Purpose

The frontend delivers a modern digital-banking experience with secure session handling, transfer workflows, transaction history, and analytics views.

## Stack

- React 18
- TypeScript
- Vite
- Material UI
- Axios
- React Router
- Vitest + Testing Library

## Core Features

- Authentication flows (register, login, protected routes)
- Session/persistent token handling (remember me)
- Dashboard with balance and analytics summaries
- Transfer flow with beneficiary helper and fee preview
- Transaction history and status mapping
- Responsive UI with glassmorphism banking style

## Security-Relevant Frontend Practices

- Authorization header injection via Axios interceptor
- Automatic unauthorized handling and redirect flow
- Token storage strategy supporting session and persistent modes
- Defensive API response normalization for robustness

## Local Development

1. Install dependencies
   - `npm ci`
2. Start dev server
   - `npm run dev`

The dev server proxies `/api` requests to `http://localhost:8080` via `vite.config.ts`, so `.env.local` is optional.
If needed, you can still override with `VITE_API_URL`.

## Quality Commands

- Run tests: `npm run test -- --run`
- Build production bundle: `npm run build`
- Lint: `npm run lint`

## Key Folders

- `src/pages` user flows and screens
- `src/components` reusable UI components
- `src/api` API client and endpoint wrappers
- `src/utils` auth helpers, constants, formatting
- `src/context` auth/theme app-level state

## Integration Contract

Expected API base in app runtime:

- `/api` (proxied by Vite in development and Nginx in Docker)

Primary API groups used:

- `/api/v1/auth/*`
- `/api/v1/transactions/*`
- `/api/v1/user/*`
- `/api/v1/wallet/*`

Backend docs reference:

- Correct Swagger UI path is `/swagger-ui.html` (for example: `https://banknova-api.onrender.com/swagger-ui.html`).

## Interview Talking Points

- How API response mapping isolates UI from backend DTO changes
- Why token storage supports both session and remember-me behavior
- How frontend tests protect critical auth and form flows
