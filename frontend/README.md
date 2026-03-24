# BankNova Frontend

Modern digital banking frontend built with React and TypeScript.
This application provides a secure and responsive user experience for authentication, transactions, analytics, and wallet management.

---

## 🚀 Key Features

* 🔐 Secure authentication flows (login, register, protected routes)
* 💾 Session & persistent token handling ("remember me")
* 📊 Dashboard with balance insights and analytics summaries
* 💸 Transfer flow with beneficiary helper and fee preview
* 📜 Transaction history with status mapping
* 🎨 Responsive UI with modern glassmorphism banking design

---

## 🧠 Architecture

Component-based architecture with clear separation of concerns:

* Pages → user flows and screens
* Components → reusable UI elements
* API layer → Axios client and endpoint abstraction
* Context → global state (auth, theme)
* Utils → helpers, constants, formatting

---

## 🛠️ Tech Stack

* React 18
* TypeScript
* Vite
* Material UI
* Axios
* React Router
* Vitest + Testing Library

---

## 🔐 Security Practices

* Authorization header injection via Axios interceptor
* Automatic handling of unauthorized responses (redirect flow)
* Token storage strategy (session + persistent modes)
* Defensive API response normalization for robustness

---

## ▶️ Local Development

### 1. Install dependencies

```bash
npm ci
```

### 2. Start development server

```bash
npm run dev
```

The dev server proxies `/api` requests to `http://localhost:8080` via `vite.config.ts`.

You can optionally override with:

```bash
VITE_API_URL=your_api_url
```

---

## 🧪 Quality Commands

* Run tests:

```bash
npm run test -- --run
```

* Build production bundle:

```bash
npm run build
```

* Lint:

```bash
npm run lint
```

---

## 📁 Project Structure

* `src/pages` → user flows and screens
* `src/components` → reusable UI components
* `src/api` → API client and endpoint wrappers
* `src/utils` → auth helpers, constants, formatting
* `src/context` → global app state (auth, theme)

---

## 🔗 API Integration

Expected API base path:

```bash
/api
```

(Handled via proxy in development and Nginx in Docker)

### Main API groups:

* `/api/v1/auth/*`
* `/api/v1/transactions/*`
* `/api/v1/user/*`
* `/api/v1/wallet/*`

📄 Backend Swagger reference:
`/swagger-ui.html`
Example: https://banknova-api.onrender.com/swagger-ui.html

---

## 🎯 Technical Decisions

* **API abstraction layer** → isolates UI from backend DTO changes
* **Token storage strategy** → supports both session and "remember me" flows
* **Axios interceptors** → centralize auth handling and error flows
* **Component modularization** → improves scalability and maintainability
* **Frontend testing strategy** → protects critical auth and form flows

---

## 💡 Purpose

This project was developed as part of a fullstack portfolio to simulate a real-world digital banking frontend, focusing on security, UX, and clean architecture using React and TypeScript.

---

## 🌐 Related

Frontend deployed on Vercel
Backend deployed on Render
