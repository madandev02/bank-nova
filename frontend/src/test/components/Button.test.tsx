import { fireEvent, render, screen } from '@testing-library/react'
import { expect, test, vi } from 'vitest'
import Button from '../../components/Button'

test('Button renders with correct text', () => {
  render(<Button>Click me</Button>)
  expect(screen.getByText('Click me')).toBeInTheDocument()
})

test('Button calls onClick when clicked', () => {
  const handleClick = vi.fn()
  render(<Button onClick={handleClick}>Click me</Button>)

  const button = screen.getByText('Click me')
  fireEvent.click(button)

  expect(handleClick).toHaveBeenCalledTimes(1)
})

test('Button is disabled when disabled prop is true', () => {
  render(<Button disabled>Disabled Button</Button>)

  const button = screen.getByText('Disabled Button')
  expect(button).toBeDisabled()
})

test('Button has correct variant classes', () => {
  render(<Button variant="secondary">Secondary</Button>)

  const button = screen.getByText('Secondary')
  expect(button).toHaveClass('MuiButton-containedSecondary')
})

test('Button has correct size classes', () => {
  render(<Button size="lg">Large Button</Button>)

  const button = screen.getByText('Large Button')
  expect(button).toHaveClass('MuiButton-sizeLarge')
})
