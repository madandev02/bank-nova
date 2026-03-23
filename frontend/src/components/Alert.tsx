import CloseIcon from "@mui/icons-material/Close";
import { IconButton, Alert as MuiAlert } from "@mui/material";
import React from "react";

interface AlertProps {
  type: "success" | "error" | "warning" | "info";
  message: string;
  onClose?: () => void;
}

const Alert: React.FC<AlertProps> = ({ type, message, onClose }) => {
  return (
    <MuiAlert
      severity={type}
      variant="filled"
      action={
        onClose ? (
          <IconButton size="small" color="inherit" onClick={onClose}>
            <CloseIcon fontSize="small" />
          </IconButton>
        ) : undefined
      }
      sx={{ borderRadius: 3 }}
    >
      {message}
    </MuiAlert>
  );
};

export default Alert;
