import { InputAdornment, TextField } from "@mui/material";
import React from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
  endAdornment?: React.ReactNode;
  hint?: string;
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, icon, endAdornment, hint, className = "", ...props }, ref) => {
    return (
      <TextField
        fullWidth
        inputRef={ref}
        label={label}
        error={Boolean(error)}
        helperText={error || hint}
        className={className}
        InputProps={{
          startAdornment: icon ? (
            <InputAdornment position="start">{icon}</InputAdornment>
          ) : undefined,
          endAdornment: endAdornment ? (
            <InputAdornment position="end">{endAdornment}</InputAdornment>
          ) : undefined,
        }}
        FormHelperTextProps={{
          component: "div",
        }}
        {...props}
        sx={{
          "& .MuiInputBase-root": {
            borderRadius: 3,
          },
          "& .MuiFormHelperText-root": {
            marginLeft: 0,
          },
        }}
      />
    );
  }
);

Input.displayName = "Input";

export default Input;
