@echo off
REM BankNova Development Setup Script for Windows
REM This script helps set up the development environment

echo 🚀 BankNova Development Setup
echo =============================

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker is not running. Please start Docker Desktop and try again.
    pause
    exit /b 1
)

echo ✅ Docker is running

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ docker-compose is not available
    pause
    exit /b 1
)

echo ✅ docker-compose is available

REM Create environment file if it doesn't exist
if not exist .env (
    echo 📝 Creating .env file...
    (
        echo # Database Configuration
        echo POSTGRES_DB=banknova
        echo POSTGRES_USER=banknova_user
        echo POSTGRES_PASSWORD=banknova_password
        echo.
        echo # JWT Secret ^(change in production^)
        echo JWT_SECRET=banknova_jwt_secret_key_for_development_only_not_for_production
        echo.
        echo # Application Settings
        echo SPRING_PROFILES_ACTIVE=docker
    ) > .env
    echo ✅ .env file created
) else (
    echo ✅ .env file already exists
)

REM Stop any existing containers
echo 🛑 Stopping existing containers...
docker-compose down >nul 2>&1

REM Build and start services
echo 🏗️  Building and starting services...
docker-compose up --build -d

REM Wait for services to be healthy
echo ⏳ Waiting for services to start...
timeout /t 30 /nobreak >nul

REM Check if services are running
docker-compose ps | findstr "Up" >nul
if %errorlevel% equ 0 (
    echo ✅ Services are running!
    echo.
    echo 🌐 Access your application:
    echo    Frontend: http://localhost:3000
    echo    Backend API: http://localhost:8080
    echo    Database: localhost:5432
    echo.
    echo 📊 View logs: docker-compose logs -f
    echo 🛑 Stop services: docker-compose down
) else (
    echo ❌ Some services failed to start. Check logs with: docker-compose logs
    pause
    exit /b 1
)

pause
