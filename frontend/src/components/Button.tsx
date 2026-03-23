import { CircularProgress, Button as MuiButton } from "@mui/material";
import React from "react";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "outline" | "glass" | "gradient";
  size?: "sm" | "md" | "lg";
  loading?: boolean;
  icon?: React.ReactNode;
  children: React.ReactNode;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = "primary",
      size = "md",
      loading = false,
      disabled = false,
      icon,
      children,
      className = "",
      ...props
    },
    ref
  ) => {
    const variants: Record<NonNullable<ButtonProps["variant"]>, { variant: "contained" | "outlined"; color: "primary" | "secondary" | "error"; sx?: object }> = {
      primary: { variant: "contained", color: "primary" },
      secondary: { variant: "contained", color: "secondary" },
      danger: { variant: "contained", color: "error" },
      outline: { variant: "outlined", color: "primary" },
      glass: {
        variant: "outlined",
        color: "primary",
        sx: {
          backdropFilter: "blur(8px)",
          backgroundColor: "rgba(255,255,255,0.05)",
        },
      },
      gradient: {
        variant: "contained",
        color: "primary",
        sx: {
          background: "linear-gradient(90deg, #00A3FF 0%, #14B8A6 100%)",
          "&:hover": {
            background: "linear-gradient(90deg, #0095EA 0%, #0F9E8E 100%)",
          },
        },
      },
    };

    const sizes = {
      sm: "small" as const,
      md: "medium" as const,
      lg: "large" as const,
    };

    const mappedVariant = variants[variant] ?? variants.primary;

    return (
      <MuiButton
        ref={ref}
        disabled={disabled || loading}
        variant={mappedVariant.variant}
        color={mappedVariant.color}
        size={sizes[size]}
        startIcon={!loading ? icon : undefined}
        className={className}
        sx={{ borderRadius: 3, ...mappedVariant.sx }}
        {...props}
      >
        {loading ? <CircularProgress size={20} color="inherit" /> : children}
      </MuiButton>
    );
  }
);

Button.displayName = "Button";

export default Button;
