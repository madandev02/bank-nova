// Local Storage key for JWT token
const TOKEN_KEY = "banknova_token";

const SESSION_KEY = "banknova_session_type";

const getStorage = (persist = false): Storage => (persist ? localStorage : sessionStorage);

const getActiveStorage = (): Storage => {
  const sessionType = localStorage.getItem(SESSION_KEY);
  if (!sessionType && localStorage.getItem(TOKEN_KEY)) {
    return localStorage;
  }
  return sessionType === "persistent" ? localStorage : sessionStorage;
};

export const saveToken = (token: string, rememberMe = true): void => {
  // Always clear previous session artifacts before saving a new token.
  removeToken();
  const storage = getStorage(rememberMe);
  localStorage.setItem(SESSION_KEY, rememberMe ? "persistent" : "session");
  storage.setItem(TOKEN_KEY, token);
  storage.setItem("tokenTimestamp", new Date().getTime().toString());
};

export const getToken = (): string | null => {
  return (
    getActiveStorage().getItem(TOKEN_KEY) ||
    localStorage.getItem(TOKEN_KEY) ||
    sessionStorage.getItem(TOKEN_KEY)
  );
};

export const removeToken = (): void => {
  localStorage.removeItem(SESSION_KEY);
  localStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem("userEmail");
  sessionStorage.removeItem("userEmail");
  localStorage.removeItem("userId");
  sessionStorage.removeItem("userId");
  localStorage.removeItem("userName");
  sessionStorage.removeItem("userName");
  localStorage.removeItem("tokenTimestamp");
  sessionStorage.removeItem("tokenTimestamp");
};

export const isAuthenticated = (): boolean => {
  const token = getToken();
  return Boolean(token) && !isTokenExpired();
};

export const isTokenExpired = (): boolean => {
  const timestamp =
    getActiveStorage().getItem("tokenTimestamp") ||
    localStorage.getItem("tokenTimestamp") ||
    sessionStorage.getItem("tokenTimestamp");
  if (!timestamp) return true;

  const now = new Date().getTime();
  const oneDay = 24 * 60 * 60 * 1000; // 24 hours

  return (now - parseInt(timestamp)) > oneDay;
};

export const saveUserData = (email: string, userId: string, userName: string): void => {
  const storage = getActiveStorage();
  storage.setItem("userEmail", email);
  storage.setItem("userId", userId);
  storage.setItem("userName", userName);
};

export const getUserEmail = (): string | null => {
  return (
    getActiveStorage().getItem("userEmail") ||
    localStorage.getItem("userEmail") ||
    sessionStorage.getItem("userEmail")
  );
};

export const getUserId = (): string | null => {
  return (
    getActiveStorage().getItem("userId") ||
    localStorage.getItem("userId") ||
    sessionStorage.getItem("userId")
  );
};

export const getUserName = (): string | null => {
  return (
    getActiveStorage().getItem("userName") ||
    localStorage.getItem("userName") ||
    sessionStorage.getItem("userName")
  );
};
