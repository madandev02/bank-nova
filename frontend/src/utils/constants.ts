export const API_BASE_URL = import.meta.env.VITE_API_URL || "/api";
export const APP_NAME = "BankNova";

// API Endpoints
export const AUTH_ENDPOINTS = {
  REGISTER: "/api/v1/auth/register",
  LOGIN: "/api/v1/auth/login",
  REFRESH: "/api/v1/auth/refresh",
};

export const USER_ENDPOINTS = {
  PROFILE: "/api/v1/user/profile",
};

export const WALLET_ENDPOINTS = {
  BALANCE: "/api/v1/wallet/balance",
};

export const TRANSACTION_ENDPOINTS = {
  TRANSFER: "/api/v1/transactions/send",
  HISTORY: "/api/v1/transactions/history",
};

export const ANALYTICS_ENDPOINTS = {
  SUMMARY: "/api/v1/transactions/analytics",
};

export const HEALTH_ENDPOINT = "/api/v1/health";
