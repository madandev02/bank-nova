import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import BoltIcon from "@mui/icons-material/Bolt";
import HistoryIcon from "@mui/icons-material/History";
import { Box, Chip, Stack, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Transaction, transactionApi } from "../api/client";
import Alert from "../components/Alert";
import BankPage from "../components/BankPage";
import Button from "../components/Button";
import Card from "../components/Card";
import LoadingSpinner from "../components/LoadingSpinner";
import { formatCurrency, formatDate } from "../utils/formatters";

const History: React.FC = () => {
  const navigate = useNavigate();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);

  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const response = await transactionApi.getHistory();
      setTransactions(response.data.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to load transactions";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading transaction history..." />;

  return (
    <BankPage
      title="Transaction History"
      subtitle="Track every credit and debit with status and timestamps"
      action={
        <Button variant="outline" icon={<ArrowBackIcon />} onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </Button>
      }
    >
      {alert && (
        <Box sx={{ mb: 3 }}>
          <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
        </Box>
      )}

      <Stack spacing={1.4}>
        {transactions.length === 0 ? (
          <Card>
            <Stack spacing={1.4} alignItems="center" sx={{ p: 5 }}>
              <HistoryIcon color="disabled" />
              <Typography variant="h6">No transactions yet</Typography>
              <Typography variant="body2" color="text.secondary">
                Your wallet activity will appear here once you complete transfers.
              </Typography>
            </Stack>
          </Card>
        ) : (
          transactions.map((transaction) => {
            const isCredit = transaction.type === "CREDIT";
            const statusColor =
              transaction.status === "COMPLETED"
                ? "success"
                : transaction.status === "PENDING"
                ? "warning"
                : "error";

            return (
              <Card key={transaction.id} hover>
                <Stack
                  direction={{ xs: "column", md: "row" }}
                  justifyContent="space-between"
                  alignItems={{ xs: "flex-start", md: "center" }}
                  spacing={1.8}
                  sx={{ p: 2.3 }}
                >
                  <Stack direction="row" spacing={1.6} alignItems="center">
                    <Box
                      sx={{
                        width: 42,
                        height: 42,
                        borderRadius: 2,
                        display: "grid",
                        placeItems: "center",
                        bgcolor: isCredit ? "rgba(34,197,94,0.16)" : "rgba(239,68,68,0.16)",
                        color: isCredit ? "success.main" : "error.main",
                      }}
                    >
                      {isCredit ? <ArrowDownwardIcon fontSize="small" /> : <ArrowUpwardIcon fontSize="small" />}
                    </Box>

                    <Box>
                      <Typography sx={{ fontWeight: 700 }}>{transaction.description}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        {formatDate(transaction.createdAt)}
                      </Typography>
                      <Chip
                        size="small"
                        label={transaction.status}
                        color={statusColor}
                        sx={{ mt: 0.8 }}
                      />
                    </Box>
                  </Stack>

                  <Box sx={{ textAlign: { xs: "left", md: "right" } }}>
                    <Typography
                      sx={{
                        fontWeight: 800,
                        fontSize: "1.2rem",
                        color: isCredit ? "success.main" : "error.main",
                      }}
                    >
                      {isCredit ? "+" : "-"}
                      {formatCurrency(transaction.amount)}
                    </Typography>
                    <Stack direction="row" spacing={0.6} alignItems="center" justifyContent={{ xs: "flex-start", md: "flex-end" }}>
                      <BoltIcon sx={{ color: "primary.main", fontSize: 14 }} />
                      <Typography variant="caption" color="text.secondary">
                        Wallet processed
                      </Typography>
                    </Stack>
                  </Box>
                </Stack>
              </Card>
            );
          })
        )}
      </Stack>
    </BankPage>
  );
};

export default History;
