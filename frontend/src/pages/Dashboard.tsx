import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import InsightsIcon from "@mui/icons-material/Insights";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import SendIcon from "@mui/icons-material/Send";
import TimelineIcon from "@mui/icons-material/Timeline";
import VerifiedUserIcon from "@mui/icons-material/VerifiedUser";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import WalletIcon from "@mui/icons-material/Wallet";
import { Box, Chip, Divider, Grid, IconButton, LinearProgress, Stack, Tooltip, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Analytics,
    Beneficiary,
    CardItem,
    Loan,
    SpendingLimit,
    analyticsApi,
    beneficiaryApi,
    cardApi,
    loanApi,
    spendingLimitApi,
    walletApi,
} from "../api/client";
import Alert from "../components/Alert";
import BankPage from "../components/BankPage";
import Button from "../components/Button";
import Card from "../components/Card";
import LoadingSpinner from "../components/LoadingSpinner";
import { getUserName } from "../utils/auth";
import { formatCurrency, formatDate } from "../utils/formatters";

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [balance, setBalance] = useState<number | null>(null);
  const [analytics, setAnalytics] = useState<Analytics | null>(null);
  const [cards, setCards] = useState<CardItem[]>([]);
  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>([]);
  const [spendingLimits, setSpendingLimits] = useState<SpendingLimit[]>([]);
  const [loans, setLoans] = useState<Loan[]>([]);
  const [verifiedBeneficiaries, setVerifiedBeneficiaries] = useState(0);
  const [activeLoans, setActiveLoans] = useState(0);
  const [showCardDetails, setShowCardDetails] = useState(false);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);
  const userName = getUserName() || "User";
  const now = new Date();

  const netFlow = analytics ? (analytics.totalIncome || 0) - (analytics.totalExpense || 0) : 0;
  const activityScore = analytics
    ? Math.min(100, Math.round((analytics.transactionCount || 0) * 10 + Math.min(Math.abs(netFlow), 5000) / 100))
    : 0;
  const primaryCard = cards.find((card) => card.isDefault) || cards[0] || null;
  const maskedCardNumber = primaryCard
    ? `**** **** **** ${primaryCard.last4Digits}`
    : "No card connected";

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [balanceRes, analyticsRes] = await Promise.all([
        walletApi.getBalance(),
        analyticsApi.getSummary(),
      ]);

      const [cardsRes, verifiedBeneficiariesRes, activeLoansRes] = await Promise.all([
        cardApi.getCards(),
        beneficiaryApi.getVerified(),
        loanApi.getActive(),
      ]);

      const [beneficiariesRes, spendingLimitsRes, loansRes] = await Promise.all([
        beneficiaryApi.getAll(),
        spendingLimitApi.getAll(),
        loanApi.getAll(),
      ]);

      setBalance(balanceRes.data.data.balance);
      setAnalytics(analyticsRes.data.data);
      setCards(cardsRes.data.data || []);
      setBeneficiaries(beneficiariesRes.data.data || []);
      setSpendingLimits(spendingLimitsRes.data.data || []);
      setLoans(loansRes.data.data || []);
      setVerifiedBeneficiaries((verifiedBeneficiariesRes.data.data || []).length);
      setActiveLoans((activeLoansRes.data.data || []).length);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to load dashboard";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading your wallet center..." />;

  return (
    <BankPage
      title={`Welcome back, ${userName}`}
      subtitle="Real-time overview of your money movement and wallet health"
      maxWidth="xl"
      action={
        <Button variant="outline" icon={<PersonOutlineIcon />} onClick={() => navigate("/profile")}>
          Profile Settings
        </Button>
      }
    >
      {alert && (
        <Box sx={{ mb: 3 }}>
          <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
        </Box>
      )}

      <Grid container spacing={{ xs: 2.5, md: 3.5 }}>
        <Grid size={{ xs: 12, lg: 8 }}>
          <Card>
            <Box
              sx={{
                p: { xs: 3, md: 4.2 },
                background:
                  "linear-gradient(135deg, rgba(0,163,255,0.26) 0%, rgba(20,184,166,0.24) 100%)",
              }}
            >
              <Stack direction={{ xs: "column", md: "row" }} justifyContent="space-between" spacing={3}>
                <Box>
                  <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                    <WalletIcon fontSize="small" />
                    <Typography color="text.secondary" variant="body2">
                      Primary Wallet
                    </Typography>
                  </Stack>
                  <Typography variant="h3" sx={{ fontWeight: 800, lineHeight: 1.1 }}>
                    {balance !== null ? formatCurrency(balance) : "$0.00"}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.8 }}>
                    {now.toLocaleDateString("en-US", { weekday: "long", month: "short", day: "numeric" })}
                  </Typography>
                </Box>

                <Stack spacing={1.2} alignItems={{ xs: "flex-start", md: "flex-end" }}>
                  <Chip
                    size="small"
                    label={netFlow >= 0 ? "Positive Cash Flow" : "Negative Cash Flow"}
                    color={netFlow >= 0 ? "success" : "error"}
                  />
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    {netFlow >= 0 ? "+" : ""}
                    {formatCurrency(netFlow)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Net movement this period
                  </Typography>
                </Stack>
              </Stack>

              <Divider sx={{ my: 3, borderColor: "rgba(255,255,255,0.25)" }} />

              <Grid container spacing={2}>
                {[
                  { label: "Account", value: "**** 1234" },
                  { label: "Tier", value: "Premium" },
                  { label: "Card", value: maskedCardNumber },
                  { label: "Status", value: "Secure Active" },
                ].map((item) => (
                  <Grid key={item.label} size={{ xs: 6, md: 3 }}>
                    <Box
                      sx={{
                        p: 1.8,
                        borderRadius: 2,
                        bgcolor: "rgba(255,255,255,0.38)",
                      }}
                    >
                      <Typography variant="caption" color="text.secondary">
                        {item.label}
                      </Typography>
                      <Typography sx={{ fontWeight: 700 }}>{item.value}</Typography>
                    </Box>
                  </Grid>
                ))}
              </Grid>

              {primaryCard && (
                <Box
                  sx={{
                    mt: 2,
                    p: 2,
                    borderRadius: 2,
                    bgcolor: "rgba(255,255,255,0.28)",
                    border: "1px solid rgba(255,255,255,0.32)",
                  }}
                >
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Stack direction="row" spacing={1} alignItems="center">
                      <CreditCardIcon fontSize="small" />
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        Primary Card Snapshot
                      </Typography>
                    </Stack>
                    <Tooltip title={showCardDetails ? "Hide card details" : "Show card details"}>
                      <IconButton
                        size="small"
                        onClick={() => setShowCardDetails((prev) => !prev)}
                        aria-label={showCardDetails ? "Hide card details" : "Show card details"}
                      >
                        {showCardDetails ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                      </IconButton>
                    </Tooltip>
                  </Stack>
                  <Grid container spacing={1.4}>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="caption" color="text.secondary">Cardholder</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>{primaryCard.cardholderName}</Typography>
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="caption" color="text.secondary">Card Type</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        {primaryCard.cardNetwork} {primaryCard.cardType}
                      </Typography>
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="caption" color="text.secondary">Expiry</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>{primaryCard.expiryDate}</Typography>
                    </Grid>
                    {showCardDetails && (
                      <>
                        <Grid size={{ xs: 12, md: 6 }}>
                          <Typography variant="caption" color="text.secondary">Status</Typography>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>{primaryCard.status}</Typography>
                        </Grid>
                        <Grid size={{ xs: 12, md: 6 }}>
                          <Typography variant="caption" color="text.secondary">Limits</Typography>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>
                            Daily {formatCurrency(primaryCard.dailyLimit || 0)} | Monthly {formatCurrency(primaryCard.monthlyLimit || 0)}
                          </Typography>
                        </Grid>
                        <Grid size={12}>
                          <Typography variant="caption" color="text.secondary">
                            Security note: only masked card digits are exposed by API.
                          </Typography>
                        </Grid>
                      </>
                    )}
                  </Grid>
                </Box>
              )}
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 4 }}>
          <Card>
            <Box sx={{ p: { xs: 3, md: 3.8 }, height: "100%" }}>
              <Typography variant="subtitle2" color="text.secondary">
                Wallet Health Score
              </Typography>
              <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.6 }}>
                {activityScore}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Based on activity and cash movement
              </Typography>
              <LinearProgress
                variant="determinate"
                value={activityScore}
                sx={{ mt: 2, height: 9, borderRadius: 999 }}
              />

              <Stack spacing={1.5} sx={{ mt: 3 }}>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Transactions
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>
                    {analytics?.transactionCount || 0}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Monthly Income
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>
                    {formatCurrency(analytics?.totalIncome || 0)}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Monthly Expense
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>
                    {formatCurrency(analytics?.totalExpense || 0)}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Verified Beneficiaries
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>
                    {verifiedBeneficiaries}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Active Loans
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>
                    {activeLoans}
                  </Typography>
                </Stack>
              </Stack>
            </Box>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={2.4} sx={{ mt: 2.4, mb: 3.2 }}>
        <Grid size={{ xs: 12, md: 3 }}>
          <Button
            className="w-full"
            size="lg"
            icon={<SendIcon />}
            onClick={() => navigate("/transfer")}
          >
            Send Money
          </Button>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Button
            className="w-full"
            size="lg"
            variant="secondary"
            icon={<TimelineIcon />}
            onClick={() => navigate("/history")}
          >
            Transaction History
          </Button>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Button
            className="w-full"
            size="lg"
            variant="gradient"
            icon={<WalletIcon />}
            onClick={() => navigate("/features")}
          >
            Banking Modules
          </Button>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Button
            className="w-full"
            size="lg"
            variant="outline"
            icon={<InsightsIcon />}
            onClick={fetchDashboardData}
          >
            Refresh Insights
          </Button>
        </Grid>
      </Grid>

      {analytics && (
        <Grid container spacing={{ xs: 2.4, md: 3 }} sx={{ mb: 3 }}>
          {[
            {
              title: "Income Flow",
              value: formatCurrency(analytics.totalIncome || 0),
              tone: "success.main",
              icon: <ArrowDownwardIcon fontSize="small" />,
              note: "Incoming funds this period",
            },
            {
              title: "Expense Flow",
              value: formatCurrency(analytics.totalExpense || 0),
              tone: "error.main",
              icon: <ArrowUpwardIcon fontSize="small" />,
              note: "Outgoing transfers and spend",
            },
            {
              title: "Transactions",
              value: `${analytics.transactionCount || 0}`,
              tone: "primary.main",
              icon: <TimelineIcon fontSize="small" />,
              note: "Total completed operations",
            },
            {
              title: "Avg per transfer",
              value: formatCurrency(analytics.averageTransaction || 0),
              tone: "secondary.main",
              icon: <InsightsIcon fontSize="small" />,
              note: "Average money movement",
            },
          ].map((metric) => (
            <Grid key={metric.title} size={{ xs: 12, sm: 6, lg: 3 }}>
              <Card hover>
                <Box sx={{ p: { xs: 2.4, md: 2.8 } }}>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1.8 }}>
                    <Typography color="text.secondary" variant="body2">
                      {metric.title}
                    </Typography>
                    <Box
                      sx={{
                        width: 32,
                        height: 32,
                        borderRadius: 2,
                        display: "grid",
                        placeItems: "center",
                        bgcolor: "rgba(148, 163, 184, 0.18)",
                        color: metric.tone,
                      }}
                    >
                      {metric.icon}
                    </Box>
                  </Stack>
                  <Typography sx={{ fontSize: "1.75rem", fontWeight: 800, color: metric.tone, mb: 0.9 }}>
                    {metric.value}
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={Math.min(
                      100,
                      metric.title.includes("Transactions")
                        ? (analytics.transactionCount || 0) * 10
                        : Math.abs(Number(metric.value.replace(/[^0-9.-]+/g, ""))) / 100
                    )}
                    sx={{ mb: 1.4, height: 7, borderRadius: 999 }}
                  />
                  <Typography variant="caption" color="text.secondary">
                    {metric.note}
                  </Typography>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Grid container spacing={{ xs: 2.4, md: 3 }}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.5 }}>
              <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1.6 }}>
                <Stack direction="row" spacing={1} alignItems="center">
                  <VerifiedUserIcon color="primary" fontSize="small" />
                  <Typography variant="h6">Beneficiaries</Typography>
                </Stack>
                <Button size="sm" variant="outline" onClick={() => navigate("/features")}>Manage</Button>
              </Stack>
              {beneficiaries.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No beneficiaries yet.</Typography>
              ) : (
                <Stack spacing={1.1}>
                  {beneficiaries.slice(0, 3).map((item) => (
                    <Stack
                      key={item.id}
                      direction="row"
                      justifyContent="space-between"
                      alignItems="center"
                      sx={{ p: 1.2, borderRadius: 2, bgcolor: "rgba(148, 163, 184, 0.08)" }}
                    >
                      <Box>
                        <Typography variant="body2" sx={{ fontWeight: 700 }}>{item.beneficiaryName}</Typography>
                        <Typography variant="caption" color="text.secondary">{item.beneficiaryEmail}</Typography>
                      </Box>
                      <Chip
                        size="small"
                        color={item.isVerified ? "success" : "warning"}
                        label={item.isVerified ? "Verified" : "Pending"}
                      />
                    </Stack>
                  ))}
                </Stack>
              )}
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.5 }}>
              <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1.6 }}>
                <Typography variant="h6">Spending Limits</Typography>
                <Button size="sm" variant="outline" onClick={() => navigate("/features")}>Controls</Button>
              </Stack>
              {spendingLimits.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No spending limits configured.</Typography>
              ) : (
                <Stack spacing={1.2}>
                  {spendingLimits.slice(0, 3).map((limit) => {
                    const used = limit.limitAmount > 0 ? Math.min(100, (limit.currentSpent / limit.limitAmount) * 100) : 0;
                    return (
                      <Box key={limit.id} sx={{ p: 1.2, borderRadius: 2, bgcolor: "rgba(148, 163, 184, 0.08)" }}>
                        <Stack direction="row" justifyContent="space-between">
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>{limit.category} ({limit.limitType})</Typography>
                          <Typography variant="caption" color="text.secondary">{Math.round(used)}%</Typography>
                        </Stack>
                        <LinearProgress variant="determinate" value={used} sx={{ mt: 1, mb: 0.6, height: 7, borderRadius: 999 }} />
                        <Typography variant="caption" color="text.secondary">
                          {formatCurrency(limit.currentSpent)} of {formatCurrency(limit.limitAmount)}
                        </Typography>
                      </Box>
                    );
                  })}
                </Stack>
              )}
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12 }}>
          <Card>
            <Box sx={{ p: 2.5 }}>
              <Stack direction={{ xs: "column", sm: "row" }} justifyContent="space-between" alignItems={{ xs: "flex-start", sm: "center" }} sx={{ mb: 1.6 }}>
                <Typography variant="h6">Loan Portfolio Snapshot</Typography>
                <Button size="sm" variant="outline" onClick={() => navigate("/features")}>See All Loans</Button>
              </Stack>
              {loans.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No loans available for this account.</Typography>
              ) : (
                <Grid container spacing={1.4}>
                  {loans.slice(0, 2).map((loan) => (
                    <Grid key={loan.id} size={{ xs: 12, md: 6 }}>
                      <Box sx={{ p: 1.4, borderRadius: 2, bgcolor: "rgba(148, 163, 184, 0.08)" }}>
                        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.8 }}>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>{loan.loanNumber}</Typography>
                          <Chip size="small" label={loan.status} color={loan.status === "ACTIVE" ? "success" : "default"} />
                        </Stack>
                        <Typography variant="caption" color="text.secondary">Outstanding</Typography>
                        <Typography variant="body2" sx={{ fontWeight: 700, mb: 0.6 }}>{formatCurrency(loan.outstandingBalance)}</Typography>
                        <Typography variant="caption" color="text.secondary">
                          Next due: {formatDate(loan.nextPaymentDueDate)}
                        </Typography>
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          </Card>
        </Grid>
      </Grid>
    </BankPage>
  );
};

export default Dashboard;
