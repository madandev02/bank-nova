export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Modern Fintech Primary - Rich Tech Blue
        primary: {
          50: '#f0f7ff',
          100: '#e0eeff',
          200: '#c1deff',
          300: '#a2cdff',
          400: '#83bcff',
          500: '#4a9dff', // Main vibrant blue
          600: '#2e7fd9',
          700: '#1e5fb3',
          800: '#143f8d',
          900: '#0d2867',
        },
        // Secondary - Vibrant Teal
        secondary: {
          50: '#f0fdf8',
          100: '#e0fdf2',
          200: '#c0fbea',
          300: '#a0f9e2',
          400: '#5af0d0', // Vibrant teal
          500: '#2dd4c0',
          600: '#1db9a0',
          700: '#149e8a',
          800: '#0d8370',
          900: '#066856',
        },
        // Accent - Purple for highlights
        accent: {
          50: '#faf5ff',
          100: '#f5ebff',
          200: '#ead5ff',
          300: '#ddbfff',
          400: '#c084ff',
          500: '#9d4edd', // Vibrant purple
          600: '#7b2cbf',
          700: '#5a0fa5',
          800: '#3d0a8b',
          900: '#200571',
        },
        // Success - Emerald green
        success: {
          50: '#f0fdf8',
          100: '#e0fdf4',
          200: '#c1fde8',
          300: '#a2fadc',
          400: '#5eeec8',
          500: '#10b981', // Vibrant emerald
          600: '#059669',
          700: '#047857',
          800: '#065f46',
          900: '#064e3b',
        },
        // Warning - Amber
        warning: {
          50: '#fffbf0',
          100: '#fef7e0',
          200: '#fdefc1',
          300: '#fce7a2',
          400: '#fcc563',
          500: '#f59e0b', // Vibrant amber
          600: '#d97706',
          700: '#b45309',
          800: '#92400e',
          900: '#78350f',
        },
        // Danger - Red
        danger: {
          50: '#fef2f2',
          100: '#fee2e2',
          200: '#fecaca',
          300: '#fca5a5',
          400: '#f87171',
          500: '#ef4444', // Vibrant red
          600: '#dc2626',
          700: '#b91c1c',
          800: '#911d1d',
          900: '#7f1d1d',
        },
        // Dark mode backgrounds - Premium Dark
        dark: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280',
          600: '#4b5563',
          700: '#374151',
          750: '#2d3748',
          800: '#1f2937',
          850: '#1a202c',
          900: '#111827',
          950: '#0f172a',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        display: ['Space Grotesk', 'system-ui', 'sans-serif'],
      },
      backdropBlur: {
        xs: '2px',
        sm: '4px',
        md: '8px',
        lg: '12px',
        xl: '20px',
      },
      boxShadow: {
        'glass-sm': '0 8px 32px rgba(31, 38, 135, 0.15)',
        'glass': '0 8px 32px rgba(31, 38, 135, 0.2)',
        'glass-lg': '0 20px 60px rgba(31, 38, 135, 0.25)',
        'glow': '0 0 40px rgba(74, 157, 255, 0.3)',
        'glow-lg': '0 0 60px rgba(74, 157, 255, 0.4)',
        'glow-accent': '0 0 40px rgba(157, 78, 221, 0.3)',
        'neon': '0 0 20px rgba(45, 212, 192, 0.7)',
      },
      animation: {
        fadeIn: 'fadeIn 0.5s ease-in-out',
        slideUp: 'slideUp 0.5s ease-out',
        slideDown: 'slideDown 0.5s ease-out',
        scaleIn: 'scaleIn 0.3s ease-out',
        glow: 'glow 3s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' }
        },
        slideUp: {
          '0%': { transform: 'translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' }
        },
        slideDown: {
          '0%': { transform: 'translateY(-20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' }
        },
        scaleIn: {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' }
        },
        glow: {
          '0%, 100%': { textShadow: '0 0 20px rgba(45, 212, 192, 0.5)' },
          '50%': { textShadow: '0 0 40px rgba(45, 212, 192, 0.8)' }
        }
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-brand': 'linear-gradient(135deg, #4a9dff 0%, #2dd4c0 100%)',
      },
    },
  },
  plugins: [
    function({ addUtilities }) {
      addUtilities({
        '.glass': {
          backgroundColor: 'rgba(255, 255, 255, 0.08)',
          backdropFilter: 'blur(12px)',
          borderRadius: '16px',
          border: '1px solid rgba(255, 255, 255, 0.1)',
        },
        '.glass-md': {
          backgroundColor: 'rgba(255, 255, 255, 0.1)',
          backdropFilter: 'blur(20px)',
          borderRadius: '16px',
          border: '1px solid rgba(255, 255, 255, 0.15)',
        },
        '.glass-dark': {
          backgroundColor: 'rgba(15, 23, 42, 0.8)',
          backdropFilter: 'blur(12px)',
          borderRadius: '16px',
          border: '1px solid rgba(255, 255, 255, 0.08)',
        },
      })
    }
  ],
}
