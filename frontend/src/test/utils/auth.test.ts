import { beforeEach, expect, test, vi } from 'vitest'
import { getToken, isTokenExpired, removeToken, saveToken } from '../../utils/auth'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
})

beforeEach(() => {
  vi.clearAllMocks()
})

test('setToken stores token in localStorage', () => {
  const token = 'jwt-token-123'
  saveToken(token)

  expect(localStorage.setItem).toHaveBeenCalledWith('banknova_token', token)
  expect(localStorage.setItem).toHaveBeenCalledWith('tokenTimestamp', expect.any(String))
})

test('getToken retrieves token from localStorage', () => {
  const token = 'jwt-token-123'
  localStorageMock.getItem.mockReturnValue(token)

  const result = getToken()

  expect(result).toBe(token)
  expect(localStorage.getItem).toHaveBeenCalledWith('banknova_token')
})

test('removeToken removes token from localStorage', () => {
  removeToken()

  expect(localStorage.removeItem).toHaveBeenCalledWith('banknova_token')
  expect(localStorage.removeItem).toHaveBeenCalledWith('userEmail')
  expect(localStorage.removeItem).toHaveBeenCalledWith('userId')
  expect(localStorage.removeItem).toHaveBeenCalledWith('userName')
  expect(localStorage.removeItem).toHaveBeenCalledWith('tokenTimestamp')
})

test('isTokenExpired returns false for valid token', () => {
  // Mock a timestamp that is less than 24 hours old
  const recentTimestamp = (new Date().getTime() - (12 * 60 * 60 * 1000)).toString() // 12 hours ago
  localStorageMock.getItem.mockReturnValue(recentTimestamp)

  const result = isTokenExpired()

  expect(result).toBe(false)
})

test('isTokenExpired returns true for expired token', () => {
  // Mock a timestamp that is more than 24 hours old
  const oldTimestamp = (new Date().getTime() - (48 * 60 * 60 * 1000)).toString() // 48 hours ago
  localStorageMock.getItem.mockReturnValue(oldTimestamp)

  const result = isTokenExpired()

  expect(result).toBe(true)
})

test('isTokenExpired returns true for invalid token', () => {
  // Mock no timestamp (invalid/missing)
  localStorageMock.getItem.mockReturnValue(null)

  const result = isTokenExpired()

  expect(result).toBe(true)
})
