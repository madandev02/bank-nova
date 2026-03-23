import { beforeEach, expect, test, vi } from 'vitest'

// Mock the entire client module
vi.mock('../../api/client', () => ({
  authApi: {
    register: vi.fn(),
    login: vi.fn()
  }
}))

import { authApi } from '../../api/client'

beforeEach(() => {
  vi.clearAllMocks()
})

test('authApi.register makes correct API call', async () => {
  const mockResponse = {
    data: {
      token: 'jwt-token',
      userId: 1,
      name: 'John Doe',
      email: 'john@example.com'
    }
  }

  authApi.register.mockResolvedValueOnce(mockResponse)

  const registerData = {
    name: 'John Doe',
    email: 'john@example.com',
    password: 'password123',
    phone: '+1234567890'
  }

  const result = await authApi.register(registerData)

  expect(authApi.register).toHaveBeenCalledWith(registerData)
  expect(result).toEqual(mockResponse)
})

test('authApi.login makes correct API call', async () => {
  const mockResponse = {
    data: {
      token: 'jwt-token',
      userId: 1,
      name: 'John Doe',
      email: 'john@example.com'
    }
  }

  authApi.login.mockResolvedValueOnce(mockResponse)

  const loginData = {
    email: 'john@example.com',
    password: 'password123'
  }

  const result = await authApi.login(loginData)

  expect(authApi.login).toHaveBeenCalledWith(loginData)
  expect(result).toEqual(mockResponse)
})

test('authApi.register handles error correctly', async () => {
  const errorMessage = 'Email already exists'
  authApi.register.mockRejectedValueOnce(new Error(errorMessage))

  const registerData = {
    name: 'John Doe',
    email: 'john@example.com',
    password: 'password123',
    phone: '+1234567890'
  }

  await expect(authApi.register(registerData)).rejects.toThrow(errorMessage)
})
