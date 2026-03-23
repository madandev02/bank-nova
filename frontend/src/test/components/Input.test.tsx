import { fireEvent, render, screen } from '@testing-library/react'
import { expect, test, vi } from 'vitest'
import Input from '../../components/Input'

test('Input renders with label', () => {
  render(<Input label="Email" />)
  expect(screen.getByLabelText('Email')).toBeInTheDocument()
})

test('Input shows error message', () => {
  render(<Input label="Email" error="Invalid email" />)
  expect(screen.getByText('Invalid email')).toBeInTheDocument()
})

test('Input calls onChange when value changes', () => {
  const handleChange = vi.fn()
  render(<Input label="Name" onChange={handleChange} />)

  const input = screen.getByRole('textbox')
  fireEvent.change(input, { target: { value: 'John Doe' } })

  expect(handleChange).toHaveBeenCalledTimes(1)
})

test('Input has correct type', () => {
  render(<Input label="Password" type="password" />)

  const input = screen.getByDisplayValue('')
  expect(input).toHaveAttribute('type', 'password')
})

test('Input shows icon when provided', () => {
  const TestIcon = () => <span data-testid="test-icon">🔒</span>

  render(<Input label="Password" icon={<TestIcon />} />)

  expect(screen.getByTestId('test-icon')).toBeInTheDocument()
})
