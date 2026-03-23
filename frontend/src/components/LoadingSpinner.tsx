import { Box, CircularProgress, Stack, Typography } from "@mui/material";
import React from "react";
import Card from "./Card";

interface LoadingSpinnerProps {
  fullScreen?: boolean;
  message?: string;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  fullScreen = false,
  message = "Loading...",
}) => {
  const content = (
    <Card>
      <Stack spacing={2} alignItems="center" sx={{ p: 4 }}>
        <CircularProgress size={42} thickness={4.5} />
        <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 600 }}>
          {message}
        </Typography>
      </Stack>
    </Card>
  );

  if (fullScreen) {
    return (
      <Box
        sx={{
          minHeight: "100vh",
          display: "grid",
          placeItems: "center",
          px: 2,
          background:
            "radial-gradient(circle at 12% 10%, rgba(0,163,255,0.18), transparent 34%), radial-gradient(circle at 86% 84%, rgba(20,184,166,0.20), transparent 38%)",
        }}
      >
        {content}
      </Box>
    );
  }

  return (
    <Box sx={{ display: "grid", placeItems: "center", p: 4 }}>
      {content}
    </Box>
  );
};

export default LoadingSpinner;
