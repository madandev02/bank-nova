import axios, { AxiosError, AxiosInstance } from "axios";
import { getToken, getUserEmail, removeToken } from "../utils/auth";
import { API_BASE_URL } from "../utils/constants";

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

const normalizeApiPayload = <T>(payload: unknown): ApiResponse<T> => {
  if (
    payload &&
    typeof payload === "object" &&
    "data" in (payload as Record<string, unknown>)
  ) {
    return payload as ApiResponse<T>;
  }

  return {
    status: "success",
    code: 200,
    message: "OK",
    data: payload as T,
    timestamp: new Date().toISOString(),
  };
};

// Request interceptor - Add JWT token to every request
apiClient.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle token expiration
apiClient.interceptors.response.use(
  (response) => {
    response.data = normalizeApiPayload(response.data);
    return response;
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      removeToken();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export interface ApiResponse<T> {
  status: string;
  code: number;
  message: string;
  data: T;
  error?: string;
  timestamp: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  userId?: string;
  id?: string | number;
  name: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  mobileNumber: string;
}

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  phone: string;
  profilePicture?: string;
  country?: string;
  city?: string;
  address?: string;
  emailVerified: boolean;
  phoneVerified: boolean;
  twoFactorEnabled: boolean;
  kycStatus: string;
  accountStatus: string;
}

export interface UpdateProfileRequest {
  name?: string;
  phone?: string;
  profilePicture?: string;
  country?: string;
  city?: string;
  address?: string;
}

export interface WalletResponse {
  balance: number;
  currency: string;
}

export interface Transaction {
  id: string;
  type: string;
  amount: number;
  description: string;
  status: string;
  createdAt: string;
  recipientEmail?: string;
}

export interface TransferRequest {
  recipientEmail: string;
  amount: number;
  description: string;
}

export interface Analytics {
  totalIncome: number;
  totalExpense: number;
  transactionCount: number;
  averageTransaction: number;
}

export interface CardItem {
  id: number;
  last4Digits: string;
  cardholderName: string;
  expiryDate: string;
  cardNetwork: string;
  cardType: string;
  status: string;
  isDefault: boolean;
  dailyLimit?: number;
  monthlyLimit?: number;
}

export interface Beneficiary {
  id: number;
  beneficiaryName: string;
  beneficiaryEmail: string;
  accountNumber: string;
  relationship: string;
  isVerified: boolean;
  verificationDate?: string;
  createdAt: string;
}

export interface CreateBeneficiaryRequest {
  beneficiaryName: string;
  beneficiaryEmail: string;
  accountNumber: string;
  relationship: string;
}

export interface SpendingLimit {
  id: number;
  category: string;
  limitType: string;
  limitAmount: number;
  currentSpent: number;
  remainingBudget: number;
  isActive: boolean;
  createdAt: string;
}

export interface SetSpendingLimitRequest {
  category: string;
  limitType: string;
  limitAmount: number;
}

export interface Loan {
  id: number;
  loanNumber: string;
  principalAmount: number;
  outstandingBalance: number;
  annualInterestRate: number;
  loanTermMonths: number;
  monthlyPayment: number;
  loanType: string;
  status: string;
  loanPurpose: string;
  disbursementDate: string;
  nextPaymentDueDate: string;
  totalInterestPaid: number;
  totalAmountPaid: number;
}

export interface TransferVerificationResult {
  id: number;
  status: string;
  verificationMethod: string;
  message: string;
}

interface BackendTransactionDto {
  id: string | number;
  senderEmail: string;
  receiverEmail: string;
  amount: number | string;
  timestamp: string;
  status: string;
  description?: string;
}

interface BackendAnalyticsDto {
  totalBalance?: number | string;
  totalTransactions?: number;
  totalSent?: number | string;
  totalReceived?: number | string;
}

const toNumber = (value: unknown): number => {
  if (typeof value === "number") return value;
  if (typeof value === "string") {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }
  return 0;
};

const normalizeStatus = (status: string): string => {
  if (status === "SUCCESS") return "COMPLETED";
  return status;
};

const toTransactionType = (tx: BackendTransactionDto): string => {
  const currentUser = getUserEmail();
  if (!currentUser) return "DEBIT";
  return tx.receiverEmail?.toLowerCase() === currentUser.toLowerCase() ? "CREDIT" : "DEBIT";
};

const toDescription = (tx: BackendTransactionDto): string => {
  if (tx.description && tx.description.trim()) return tx.description;
  const type = toTransactionType(tx);
  return type === "CREDIT" ? `Transfer from ${tx.senderEmail}` : `Transfer to ${tx.receiverEmail}`;
};

const mapTransaction = (tx: BackendTransactionDto): Transaction => ({
  id: String(tx.id),
  type: toTransactionType(tx),
  amount: toNumber(tx.amount),
  description: toDescription(tx),
  status: normalizeStatus(tx.status),
  createdAt: tx.timestamp,
  recipientEmail: tx.receiverEmail,
});

const mapAnalytics = (data: BackendAnalyticsDto): Analytics => {
  const totalIncome = toNumber(data.totalReceived);
  const totalExpense = toNumber(data.totalSent);
  const transactionCount = data.totalTransactions || 0;
  const averageTransaction = transactionCount > 0 ? (totalIncome + totalExpense) / transactionCount : 0;

  return {
    totalIncome,
    totalExpense,
    transactionCount,
    averageTransaction,
  };
};

// Auth API Calls
export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<ApiResponse<LoginResponse>>("/api/v1/auth/register", data),
  login: (email: string, password: string) =>
    apiClient.post<ApiResponse<LoginResponse>>("/api/v1/auth/login", {
      email,
      password,
    }),
};

// User API Calls
export const userApi = {
  getProfile: () =>
    apiClient.get<ApiResponse<UserProfile>>("/api/v1/user/profile"),
  updateProfile: (data: UpdateProfileRequest) =>
    apiClient.put<ApiResponse<UserProfile>>("/api/v1/user/profile", data),
};

// Wallet API Calls
export const walletApi = {
  getBalance: () =>
    apiClient.get<ApiResponse<WalletResponse>>("/api/v1/wallet/balance"),
};

// Transaction API Calls
export const transactionApi = {
  transfer: async (data: TransferRequest) => {
    const response = await apiClient.post<ApiResponse<BackendTransactionDto>>("/api/v1/transactions/send", {
      receiverEmail: data.recipientEmail,
      amount: data.amount,
      description: data.description,
    });

    response.data.data = mapTransaction(response.data.data);
    return response as typeof response & { data: ApiResponse<Transaction> };
  },
  getHistory: async () => {
    const response = await apiClient.get<ApiResponse<BackendTransactionDto[]>>("/api/v1/transactions/history");
    response.data.data = response.data.data.map(mapTransaction);
    return response as typeof response & { data: ApiResponse<Transaction[]> };
  },
};

export const transferVerificationApi = {
  initiate: (transferId: number, transferDetails: string) =>
    apiClient.post<ApiResponse<TransferVerificationResult>>("/api/v1/verify-transfer/initiate", {
      transferId,
      transferDetails,
    }),
  verify: (otpCode: string) =>
    apiClient.post<ApiResponse<string>>("/api/v1/verify-transfer/verify", { otpCode }),
  resend: (transferId: number) =>
    apiClient.post<ApiResponse<string>>("/api/v1/verify-transfer/resend", { transferId }),
};

export const cardApi = {
  getCards: () => apiClient.get<ApiResponse<CardItem[]>>("/api/v1/cards"),
  setDefault: (cardId: number) =>
    apiClient.patch<ApiResponse<string>>(`/api/v1/cards/${cardId}/default`),
  block: (cardId: number) => apiClient.post<ApiResponse<string>>(`/api/v1/cards/${cardId}/block`),
  close: (cardId: number) => apiClient.delete<ApiResponse<string>>(`/api/v1/cards/${cardId}`),
};

export const beneficiaryApi = {
  getAll: () => apiClient.get<ApiResponse<Beneficiary[]>>("/api/v1/beneficiaries"),
  getVerified: () => apiClient.get<ApiResponse<Beneficiary[]>>("/api/v1/beneficiaries/verified"),
  add: (data: CreateBeneficiaryRequest) =>
    apiClient.post<ApiResponse<Beneficiary>>("/api/v1/beneficiaries/add", data),
  remove: (beneficiaryId: number) =>
    apiClient.delete<ApiResponse<string>>(`/api/v1/beneficiaries/${beneficiaryId}`),
};

export const spendingLimitApi = {
  getAll: () => apiClient.get<ApiResponse<SpendingLimit[]>>("/api/v1/spending-limits"),
  set: (data: SetSpendingLimitRequest) =>
    apiClient.post<ApiResponse<SpendingLimit>>("/api/v1/spending-limits/set", data),
  disable: (limitId: number) =>
    apiClient.delete<ApiResponse<string>>(`/api/v1/spending-limits/${limitId}`),
};

export const loanApi = {
  getAll: () => apiClient.get<ApiResponse<Loan[]>>("/api/v1/loans"),
  getActive: () => apiClient.get<ApiResponse<Loan[]>>("/api/v1/loans/active"),
};

// Analytics API Calls
export const analyticsApi = {
  getSummary: async () => {
    const response = await apiClient.get<ApiResponse<BackendAnalyticsDto>>("/api/v1/transactions/analytics");
    response.data.data = mapAnalytics(response.data.data);
    return response as typeof response & { data: ApiResponse<Analytics> };
  },
};

export default apiClient;
