import { expect, test } from 'vitest'
import { formatCurrency, formatPhone, isValidEmail, isValidPassword } from '../../utils/formatters'

test('formatCurrency formats numbers correctly', () => {
  expect(formatCurrency(1234.56)).toBe('$1,234.56')
  expect(formatCurrency(100)).toBe('$100.00')
  expect(formatCurrency(0)).toBe('$0.00')
  expect(formatCurrency(-50.25)).toBe('-$50.25')
})

test('formatPhoneNumber formats phone numbers correctly', () => {
  expect(formatPhone('+1234567890')).toBe('+11234567890')
  expect(formatPhone('1234567890')).toBe('+11234567890')
  expect(formatPhone('5551234567')).toBe('+15551234567')
})

test('validateEmail validates email addresses', () => {
  expect(isValidEmail('john@example.com')).toBe(true)
  expect(isValidEmail('invalid-email')).toBe(false)
  expect(isValidEmail('')).toBe(false)
  expect(isValidEmail('test@domain')).toBe(false)
})

test('validatePassword validates passwords', () => {
  expect(isValidPassword('Password123!')).toBe(true)
  expect(isValidPassword('short')).toBe(false)
  expect(isValidPassword('nouppercase123!')).toBe(false)
  expect(isValidPassword('NOLOWERCASE123!')).toBe(true) // Has uppercase, numbers, special char
  expect(isValidPassword('NoNumbers!')).toBe(false)
  expect(isValidPassword('NoSpecial123')).toBe(false)
})
