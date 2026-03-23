import { Box, Container, Stack, Typography } from "@mui/material";
import React from "react";

interface BankPageProps {
  title: string;
  subtitle?: string;
  action?: React.ReactNode;
  children: React.ReactNode;
  maxWidth?: "sm" | "md" | "lg" | "xl";
}

const BankPage: React.FC<BankPageProps> = ({
  title,
  subtitle,
  action,
  children,
  maxWidth = "lg",
}) => {
  return (
    <Box
      sx={{
        minHeight: "100vh",
        position: "relative",
        py: { xs: 4, md: 6 },
        background:
          "radial-gradient(circle at 12% 10%, rgba(0,163,255,0.18), transparent 34%), radial-gradient(circle at 86% 84%, rgba(20,184,166,0.20), transparent 38%)",
      }}
    >
      <Container maxWidth={maxWidth} sx={{ position: "relative", zIndex: 1 }}>
        <Stack
          direction={{ xs: "column", md: "row" }}
          alignItems={{ xs: "flex-start", md: "center" }}
          justifyContent="space-between"
          spacing={2}
          sx={{ mb: 4 }}
        >
          <Box>
            <Typography variant="h4">{title}</Typography>
            {subtitle && (
              <Typography color="text.secondary" sx={{ mt: 0.75 }}>
                {subtitle}
              </Typography>
            )}
          </Box>
          {action}
        </Stack>

        {children}
      </Container>
    </Box>
  );
};

export default BankPage;
