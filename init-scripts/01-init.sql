-- BankNova Database Initialization Script
-- This script runs automatically when the PostgreSQL container starts for the first time

-- Create database (if not exists)
-- Note: Database is already created by POSTGRES_DB environment variable

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Insert demo users (passwords are hashed versions of 'Password123')
-- Note: In a real application, use proper password hashing in the application layer
-- These are for demo purposes only

-- The application will handle user creation and password hashing
-- This script just ensures the database is ready
