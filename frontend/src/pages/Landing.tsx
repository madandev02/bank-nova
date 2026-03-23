import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";
import BoltIcon from "@mui/icons-material/Bolt";
import GppGoodIcon from "@mui/icons-material/GppGood";
import NorthEastIcon from "@mui/icons-material/NorthEast";
import QueryStatsIcon from "@mui/icons-material/QueryStats";
import SendIcon from "@mui/icons-material/Send";
import { Box, Chip, Container, Divider, Grid, Stack, Typography } from "@mui/material";
import React from "react";
import { Link } from "react-router-dom";
import Button from "../components/Button";
import Card from "../components/Card";

const Landing: React.FC = () => {
  return (
    <Box
      sx={{
        minHeight: "100vh",
        background:
          "radial-gradient(circle at 15% 10%, rgba(0,163,255,0.20), transparent 30%), radial-gradient(circle at 85% 82%, rgba(20,184,166,0.20), transparent 35%)",
      }}
    >
      <Container maxWidth="xl" sx={{ pt: { xs: 6, md: 10 }, pb: { xs: 7, md: 10 } }}>
        <Grid container spacing={{ xs: 4, md: 5 }} alignItems="center" sx={{ mb: { xs: 8, md: 13 } }}>
          <Grid size={{ xs: 12, md: 7 }}>
            <Stack spacing={2.5}>
              <Chip
                icon={<BoltIcon />}
                label="Next-generation digital wallet"
                color="primary"
                variant="outlined"
                sx={{ width: "fit-content", fontWeight: 700 }}
              />
              <Typography variant="h2" sx={{ fontWeight: 800, lineHeight: 1.05, fontSize: { xs: "2.4rem", md: "4rem" } }}>
                Banking that feels
                <Box component="span" sx={{ display: "block", color: "primary.main" }}>
                  premium and instant
                </Box>
              </Typography>
              <Typography variant="h6" color="text.secondary" sx={{ maxWidth: 640, lineHeight: 1.5 }}>
                BankNova combines the trust of a bank with the speed of a digital wallet.
                Move money, monitor your spending, and stay secure in one glassmorphism
                dashboard designed for modern finance. New backend-powered modules now include
                cards, beneficiaries, spending limits, loans, and email OTP transfer verification.
              </Typography>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2} sx={{ pt: 1 }}>
                <Link to="/register" style={{ textDecoration: "none" }}>
                  <Button size="lg" icon={<NorthEastIcon />}>
                    Open Your Wallet
                  </Button>
                </Link>
                <Link to="/login" style={{ textDecoration: "none" }}>
                  <Button variant="outline" size="lg">
                    Sign In
                  </Button>
                </Link>
                <Link to="/features" style={{ textDecoration: "none" }}>
                  <Button variant="secondary" size="lg">
                    Explore Modules
                  </Button>
                </Link>
              </Stack>
              <Stack direction="row" spacing={1.2} flexWrap="wrap" useFlexGap>
                {[
                  "Real-time transfers",
                  "Biometric-grade security",
                  "Smart spending analytics",
                ].map((label) => (
                  <Chip key={label} label={label} sx={{ borderRadius: 2, bgcolor: "background.paper" }} />
                ))}
              </Stack>
            </Stack>
          </Grid>

          <Grid size={{ xs: 12, md: 5 }}>
            <Card glass style={{ overflow: "hidden" }}>
              <Box sx={{ p: 3.2 }}>
                <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2.4 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                    Main Wallet
                  </Typography>
                  <Chip label="Active" color="success" size="small" />
                </Stack>

                <Typography variant="body2" color="text.secondary">
                  Available balance
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: 800, mb: 2.5 }}>
                  $42,580.90
                </Typography>

                <Grid container spacing={2} sx={{ mb: 2.5 }}>
                  <Grid size={6}>
                    <Card>
                      <Box sx={{ p: 2 }}>
                        <Typography variant="caption" color="text.secondary">
                          Income
                        </Typography>
                        <Typography sx={{ fontWeight: 700, color: "success.main" }}>
                          +$9,240
                        </Typography>
                      </Box>
                    </Card>
                  </Grid>
                  <Grid size={6}>
                    <Card>
                      <Box sx={{ p: 2 }}>
                        <Typography variant="caption" color="text.secondary">
                          Expenses
                        </Typography>
                        <Typography sx={{ fontWeight: 700, color: "error.main" }}>
                          -$4,620
                        </Typography>
                      </Box>
                    </Card>
                  </Grid>
                </Grid>

                <Stack spacing={1.2}>
                  {[
                    { title: "Salary", amount: "+$3,200" },
                    { title: "Cloud Subscription", amount: "-$45" },
                    { title: "Transfer from Sarah", amount: "+$180" },
                  ].map((item) => (
                    <Stack
                      key={item.title}
                      direction="row"
                      justifyContent="space-between"
                      sx={{
                        px: 1.5,
                        py: 1,
                        borderRadius: 2,
                        bgcolor: "rgba(148, 163, 184, 0.08)",
                      }}
                    >
                      <Typography variant="body2">{item.title}</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        {item.amount}
                      </Typography>
                    </Stack>
                  ))}
                </Stack>
              </Box>
            </Card>
          </Grid>
        </Grid>

        <Grid container spacing={{ xs: 2.2, md: 3 }} sx={{ mb: { xs: 8, md: 12 } }}>
          {[
            {
              icon: <GppGoodIcon color="primary" />,
              title: "Bank-grade security",
              text: "Advanced fraud detection and encrypted transaction flows.",
            },
            {
              icon: <SendIcon color="primary" />,
              title: "Instant wallet transfers",
              text: "Move money globally in seconds with transparent status updates.",
            },
            {
              icon: <QueryStatsIcon color="primary" />,
              title: "Financial insights",
              text: "Understand your cash flow and spending trends in one glance.",
            },
            {
              icon: <AccountBalanceWalletIcon color="primary" />,
              title: "Unified accounts",
              text: "Cards, savings, and daily wallet operations in one place.",
            },
            {
              icon: <GppGoodIcon color="primary" />,
              title: "Email OTP verification",
              text: "Add an extra transfer approval step with one-time verification codes.",
            },
          ].map((feature) => (
            <Grid key={feature.title} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
              <Card hover>
                <Box sx={{ p: 2.5 }}>
                  <Box sx={{ mb: 1.5 }}>{feature.icon}</Box>
                  <Typography variant="h6" sx={{ fontSize: "1.05rem", mb: 1 }}>
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {feature.text}
                  </Typography>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>

        <Card>
          <Box sx={{ p: { xs: 3, md: 5 }, textAlign: "center" }}>
            <Typography variant="h4" sx={{ mb: 1.2 }}>
              Built for trust, speed, and clarity
            </Typography>
            <Typography color="text.secondary" sx={{ maxWidth: 760, mx: "auto", mb: 3 }}>
              Every interaction is optimized for real digital banking behavior: quick transfers,
              clear balances, confident decisions, and secure access.
            </Typography>
            <Stack direction={{ xs: "column", sm: "row" }} justifyContent="center" spacing={2}>
              <Link to="/register" style={{ textDecoration: "none" }}>
                <Button size="lg">Create Free Account</Button>
              </Link>
              <Link to="/login" style={{ textDecoration: "none" }}>
                <Button variant="outline" size="lg">
                  Go to Dashboard
                </Button>
              </Link>
            </Stack>
          </Box>
        </Card>

        <Box
          component="footer"
          sx={{
            mt: { xs: 7, md: 10 },
            pt: { xs: 3.5, md: 4.5 },
            borderTop: "1px solid",
            borderColor: "divider",
          }}
        >
          <Grid container spacing={{ xs: 3, md: 4 }}>
            <Grid size={{ xs: 12, md: 4 }}>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                BankNova
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ maxWidth: 420 }}>
                Educational full-stack banking simulation built for portfolio demonstration.
                This platform showcases authentication, transfers, analytics, and responsive
                product design in a modern web architecture.
              </Typography>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1.1 }}>
                Project
              </Typography>
              <Stack spacing={0.7}>
                <Typography variant="body2" color="text.secondary">
                  Frontend: React + TypeScript + MUI
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Backend: Spring Boot + PostgreSQL
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Security: JWT + protected routes
                </Typography>
              </Stack>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 2.5 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1.1 }}>
                Contact
              </Typography>
              <Stack spacing={0.7}>
                <Typography variant="body2" color="text.secondary">
                  madandev-portfolio.vercel.app
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  mauricionarvilla@gmail.com
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Available for junior roles
                </Typography>
              </Stack>
            </Grid>

            <Grid size={{ xs: 12, md: 2.5 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1.1 }}>
                Legal
              </Typography>
              <Stack spacing={0.7}>
                <Typography variant="body2" color="text.secondary">
                  Disclaimer: Demo only, not a real bank.
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Terms of Service apply to demo usage.
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Privacy: No production-grade guarantees.
                </Typography>
              </Stack>
            </Grid>
          </Grid>

          <Divider sx={{ my: 2.5 }} />

          <Typography variant="caption" color="text.secondary" sx={{ display: "block" }}>
            © {new Date().getFullYear()} BankNova. Built as a portfolio project to demonstrate
            full-stack product engineering and UI/UX execution.
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Landing;
