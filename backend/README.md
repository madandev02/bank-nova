# BankNova Backend

Spring Boot backend for BankNova digital banking simulation.

## Purpose

This service exposes secure REST APIs for authentication, wallet operations, transfers, history, analytics, and profile management.

## Stack

- Java 17
- Spring Boot 3.3
- Spring Security
- Spring Data JPA
- PostgreSQL (runtime)
- H2 (tests)
- JWT (jjwt)
- OpenAPI/Swagger

## Security Controls

- Stateless JWT authentication
- Authorization rules for protected endpoints
- Rate limiting via AOP annotations
  - Login and register endpoints rate-limited
  - Transfer endpoint rate-limited
- Security headers configured in HTTP filter chain
  - HSTS
  - Referrer Policy
  - Permissions Policy
  - X-Content-Type-Options
  - X-Frame-Options
- Centralized exception handling for consistent API errors

## Main Endpoint Groups

- Authentication: `/api/v1/auth/*`
- Wallet: `/api/v1/wallet/*`
- Transactions: `/api/v1/transactions/*`
- Users/Profile: `/api/v1/users/*`
- Health: `/api/v1/health`

## Local Run

1. Install dependencies and run tests
   - `./mvnw test`
2. Start API
   - `./mvnw spring-boot:run`
3. Swagger
   - `http://localhost:8080/swagger-ui.html`

## Build

- Package jar: `./mvnw clean package`

## Testing

- Unit and slice tests are included for services, repositories, and controllers
- Run all tests: `./mvnw test`

## Data Layer Notes

- Primary relational model centered on User, Wallet, and Transaction entities
- Postgres is used for runtime persistence
- H2 is used in test profile for faster execution

## Interview Talking Points

- Why stateless JWT was chosen over server sessions
- How rate limiting is implemented with AOP and per-principal/IP keys
- How test slices (`@WebMvcTest`, `@DataJpaTest`) reduce test runtime while preserving confidence
