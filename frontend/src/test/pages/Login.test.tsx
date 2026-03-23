import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { expect, test, vi } from 'vitest'
import * as authApi from '../../api/client'
import { AuthProvider } from '../../context/AuthContext'
import Login from '../../pages/Login'

// Mock the API
vi.mock('../../api/client', () => ({
  authApi: {
    login: vi.fn()
  }
}))

const renderLogin = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <Login />
      </AuthProvider>
    </BrowserRouter>
  )
}

test('Login form renders correctly', () => {
  renderLogin()

  expect(screen.getByLabelText('Email Address')).toBeInTheDocument()
  expect(screen.getByLabelText('Password')).toBeInTheDocument()
  expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument()
  expect(screen.getByText(/don't have an account/i)).toBeInTheDocument()
})

test('Login form shows validation errors for empty fields', async () => {
  renderLogin()

  const submitButton = screen.getByRole('button', { name: /sign in/i })
  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(screen.getByText(/email is required/i)).toBeInTheDocument()
    expect(screen.getByText(/password is required/i)).toBeInTheDocument()
  })
})

test('Login form submits successfully', async () => {
  authApi.authApi.login.mockResolvedValueOnce({
    token: 'fake-token',
    userId: 1,
    name: 'John Doe',
    email: 'john@example.com'
  })

  renderLogin()

  const emailInput = screen.getByPlaceholderText('you@example.com')
  const passwordInput = screen.getByPlaceholderText('••••••••')
  const submitButton = screen.getByRole('button', { name: /sign in/i })

  fireEvent.change(emailInput, { target: { value: 'john@example.com' } })
  fireEvent.change(passwordInput, { target: { value: 'password123' } })
  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(authApi.authApi.login).toHaveBeenCalledWith(
      'john@example.com',
      'password123'
    )
  })
})

test('Login form shows error on failed login', async () => {
  authApi.authApi.login.mockRejectedValueOnce(new Error('Invalid credentials'))

  renderLogin()

  const emailInput = screen.getByPlaceholderText('you@example.com')
  const passwordInput = screen.getByPlaceholderText('••••••••')
  const submitButton = screen.getByRole('button', { name: /sign in/i })

  fireEvent.change(emailInput, { target: { value: 'john@example.com' } })
  fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } })
  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(screen.getByText(/login failed/i)).toBeInTheDocument()
  })
})
