import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import BookmarkAddIcon from "@mui/icons-material/BookmarkAdd";
import CurrencyExchangeIcon from "@mui/icons-material/CurrencyExchange";
import MarkEmailReadIcon from "@mui/icons-material/MarkEmailRead";
import TipsAndUpdatesIcon from "@mui/icons-material/TipsAndUpdates";
import VerifiedUserIcon from "@mui/icons-material/VerifiedUser";
import { Box, Chip, FormControlLabel, Grid, Stack, Switch, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CardItem, TransferRequest, cardApi, transactionApi, transferVerificationApi } from "../api/client";
import Alert from "../components/Alert";
import BankPage from "../components/BankPage";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import { formatCurrency, isValidEmail } from "../utils/formatters";

const Transfer: React.FC = () => {
  const BENEFICIARIES_KEY = "banknova_beneficiaries";
  const MAX_TRANSFER_AMOUNT = 5000;

  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);
  const [saveBeneficiary, setSaveBeneficiary] = useState(false);
  const [requireOtp, setRequireOtp] = useState(true);
  const [pendingTransferId, setPendingTransferId] = useState<number | null>(null);
  const [otpCode, setOtpCode] = useState("");
  const [otpVerified, setOtpVerified] = useState(false);
  const [cards, setCards] = useState<CardItem[]>([]);
  const [beneficiaries, setBeneficiaries] = useState<string[]>(() => {
    try {
      const raw = localStorage.getItem(BENEFICIARIES_KEY);
      const parsed = raw ? (JSON.parse(raw) as string[]) : [];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  });
  const [formData, setFormData] = useState({
    recipientEmail: "",
    amount: "",
    description: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const primaryCard = cards.find((card) => card.isDefault) || cards[0] || null;

  useEffect(() => {
    const fetchCards = async () => {
      try {
        const cardsRes = await cardApi.getCards();
        setCards(cardsRes.data.data || []);
      } catch {
        setCards([]);
      }
    };

    fetchCards();
  }, []);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!isValidEmail(formData.recipientEmail)) {
      newErrors.recipientEmail = "Valid email is required";
    }
    if (!formData.amount || parseFloat(formData.amount) <= 0) {
      newErrors.amount = "Valid amount is required";
    }
    if (parseFloat(formData.amount) > MAX_TRANSFER_AMOUNT) {
      newErrors.amount = `Maximum transfer per transaction is $${MAX_TRANSFER_AMOUNT.toLocaleString()}`;
    }
    if (!formData.description.trim()) {
      newErrors.description = "Description is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const persistBeneficiaries = (items: string[]) => {
    setBeneficiaries(items);
    localStorage.setItem(BENEFICIARIES_KEY, JSON.stringify(items));
  };

  const saveCurrentBeneficiary = () => {
    const email = formData.recipientEmail.trim().toLowerCase();
    if (!email || !isValidEmail(email)) return;
    if (beneficiaries.includes(email)) return;
    const next = [email, ...beneficiaries].slice(0, 8);
    persistBeneficiaries(next);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    const transferRequest: TransferRequest = {
      recipientEmail: formData.recipientEmail,
      amount: parseFloat(formData.amount),
      description: formData.description,
    };

    if (requireOtp && !otpVerified) {
      const transferId = Date.now();
      setLoading(true);
      try {
        await transferVerificationApi.initiate(
          transferId,
          `${transferRequest.recipientEmail} | ${transferRequest.amount} | ${transferRequest.description}`
        );
        setPendingTransferId(transferId);
        setAlert({ type: "success", message: "Verification code sent to your email." });
      } catch (error: any) {
        const errorMessage = error.response?.data?.message || "Could not start email verification";
        setAlert({ type: "error", message: errorMessage });
      } finally {
        setLoading(false);
      }
      return;
    }

    setLoading(true);
    try {
      await transactionApi.transfer(transferRequest);

      if (saveBeneficiary) {
        saveCurrentBeneficiary();
      }

      setAlert({ type: "success", message: "Transfer completed successfully!" });
      setTimeout(() => {
        navigate("/history");
      }, 1200);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Transfer failed";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (!otpCode.trim()) {
      setAlert({ type: "error", message: "Enter the verification code first." });
      return;
    }

    setLoading(true);
    try {
      await transferVerificationApi.verify(otpCode.trim());
      setOtpVerified(true);
      setAlert({ type: "success", message: "Email verification completed. You can now confirm transfer." });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Invalid verification code";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    if (!pendingTransferId) return;

    setLoading(true);
    try {
      await transferVerificationApi.resend(pendingTransferId);
      setAlert({ type: "success", message: "A new verification code has been sent." });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Could not resend verification code";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  const parsedAmount = parseFloat(formData.amount || "0");
  const transferFee = parsedAmount > 0 ? Math.min(5, parsedAmount * 0.005) : 0;
  const totalDebit = parsedAmount + transferFee;

  return (
    <BankPage
      title="Send Money"
      subtitle="Fast and secure transfer between digital wallet accounts"
      maxWidth="md"
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

      <Grid container spacing={2.2}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card>
            <Box sx={{ p: { xs: 2.3, md: 3 } }}>
              <Stack component="form" spacing={2} onSubmit={handleSubmit}>
                {beneficiaries.length > 0 && (
                  <Stack spacing={1}>
                    <Typography variant="body2" color="text.secondary">
                      Saved beneficiaries
                    </Typography>
                    <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                      {beneficiaries.map((email) => (
                        <Chip
                          key={email}
                          label={email}
                          onClick={() => setFormData((prev) => ({ ...prev, recipientEmail: email }))}
                          variant="outlined"
                          size="small"
                        />
                      ))}
                    </Stack>
                  </Stack>
                )}

                <Input
                  label="Recipient Email"
                  type="email"
                  placeholder="recipient@example.com"
                  value={formData.recipientEmail}
                  onChange={(e) => setFormData({ ...formData, recipientEmail: e.target.value })}
                  error={errors.recipientEmail}
                  disabled={loading}
                  icon={<MarkEmailReadIcon fontSize="small" />}
                />

                <Input
                  label="Amount (USD)"
                  type="number"
                  placeholder="1000.00"
                  step="0.01"
                  min="0"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  error={errors.amount}
                  disabled={loading}
                  icon={<CurrencyExchangeIcon fontSize="small" />}
                />

                <Input
                  label="Description"
                  type="text"
                  placeholder="e.g., rent payment, shared dinner, monthly savings"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  error={errors.description}
                  disabled={loading}
                />

                <FormControlLabel
                  control={
                    <Switch
                      checked={saveBeneficiary}
                      onChange={(e) => setSaveBeneficiary(e.target.checked)}
                      color="primary"
                    />
                  }
                  label={
                    <Stack direction="row" spacing={0.8} alignItems="center">
                      <BookmarkAddIcon fontSize="small" />
                      <Typography variant="body2">Save as beneficiary</Typography>
                    </Stack>
                  }
                />

                <FormControlLabel
                  control={
                    <Switch
                      checked={requireOtp}
                      onChange={(e) => {
                        const enabled = e.target.checked;
                        setRequireOtp(enabled);
                        if (!enabled) {
                          setPendingTransferId(null);
                          setOtpVerified(false);
                          setOtpCode("");
                        }
                      }}
                      color="primary"
                    />
                  }
                  label={
                    <Stack direction="row" spacing={0.8} alignItems="center">
                      <VerifiedUserIcon fontSize="small" />
                      <Typography variant="body2">Require email verification code</Typography>
                    </Stack>
                  }
                />

                {requireOtp && pendingTransferId && !otpVerified && (
                  <Stack spacing={1.2}>
                    <Input
                      label="Email Verification Code"
                      type="text"
                      placeholder="6-digit code"
                      value={otpCode}
                      onChange={(e) => setOtpCode(e.target.value)}
                      disabled={loading}
                    />
                    <Stack direction={{ xs: "column", sm: "row" }} spacing={1}>
                      <Button type="button" variant="secondary" onClick={handleVerifyOtp} disabled={loading}>
                        Verify Code
                      </Button>
                      <Button type="button" variant="outline" onClick={handleResendOtp} disabled={loading}>
                        Resend Code
                      </Button>
                    </Stack>
                  </Stack>
                )}

                {requireOtp && otpVerified && (
                  <Chip color="success" label="Email verification completed" size="small" />
                )}

                <Button type="submit" loading={loading} disabled={loading} size="lg" icon={<CurrencyExchangeIcon />}>
                  {loading
                    ? "Processing Transfer..."
                    : requireOtp && !otpVerified
                    ? "Send Verification Code"
                    : "Confirm Transfer"}
                </Button>
              </Stack>
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Stack spacing={2}>
            <Card>
              <Box sx={{ p: 2.2 }}>
                <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 0.6 }}>
                  Source Card
                </Typography>
                {primaryCard ? (
                  <>
                    <Typography variant="body2" sx={{ fontWeight: 700 }}>
                      **** **** **** {primaryCard.last4Digits}
                    </Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ display: "block", mb: 1.2 }}>
                      {primaryCard.cardNetwork} {primaryCard.cardType} • {primaryCard.status}
                    </Typography>
                  </>
                ) : (
                  <Typography variant="caption" color="text.secondary" sx={{ display: "block", mb: 1.2 }}>
                    No active card connected.
                  </Typography>
                )}

                <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 0.6 }}>
                  Transfer Preview
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 800 }}>
                  {parsedAmount > 0 ? formatCurrency(parsedAmount) : "$0.00"}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  Recipient: {formData.recipientEmail || "-"}
                </Typography>
                <Stack spacing={0.4} sx={{ mt: 1.2 }}>
                  <Stack direction="row" justifyContent="space-between">
                    <Typography variant="caption" color="text.secondary">
                      Transfer fee
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {formatCurrency(transferFee)}
                    </Typography>
                  </Stack>
                  <Stack direction="row" justifyContent="space-between">
                    <Typography variant="body2" sx={{ fontWeight: 700 }}>
                      Total debit
                    </Typography>
                    <Typography variant="body2" sx={{ fontWeight: 700 }}>
                      {formatCurrency(totalDebit)}
                    </Typography>
                  </Stack>
                </Stack>
                <Typography variant="caption" color="text.secondary" sx={{ mt: 0.8, display: "block" }}>
                  Limit per transfer: ${MAX_TRANSFER_AMOUNT.toLocaleString()}
                </Typography>
              </Box>
            </Card>

            <Card>
              <Box sx={{ p: 2.2 }}>
                <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                  <TipsAndUpdatesIcon color="primary" fontSize="small" />
                  <Typography variant="subtitle2">Transfer Tips</Typography>
                </Stack>
                <Typography variant="body2" color="text.secondary">
                  Double-check recipient email and amount before submitting. Transfers are encrypted
                  and processed with wallet-level fraud controls.
                </Typography>
                {requireOtp && (
                  <Chip
                    size="small"
                    color={otpVerified ? "success" : "warning"}
                    label={otpVerified ? "OTP verified" : "OTP verification enabled"}
                    sx={{ mt: 1.3 }}
                  />
                )}
              </Box>
            </Card>
          </Stack>
        </Grid>
      </Grid>
    </BankPage>
  );
};

export default Transfer;
