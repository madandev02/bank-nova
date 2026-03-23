import AddCardIcon from "@mui/icons-material/AddCard";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import PeopleAltIcon from "@mui/icons-material/PeopleAlt";
import SavingsIcon from "@mui/icons-material/Savings";
import VerifiedUserIcon from "@mui/icons-material/VerifiedUser";
import { Box, Chip, Grid, Stack, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Beneficiary,
    CardItem,
    CreateBeneficiaryRequest,
    Loan,
    SetSpendingLimitRequest,
    SpendingLimit,
    beneficiaryApi,
    cardApi,
    loanApi,
    spendingLimitApi,
} from "../api/client";
import Alert from "../components/Alert";
import BankPage from "../components/BankPage";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import LoadingSpinner from "../components/LoadingSpinner";
import { formatCurrency } from "../utils/formatters";

const Features: React.FC = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [cards, setCards] = useState<CardItem[]>([]);
  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>([]);
  const [limits, setLimits] = useState<SpendingLimit[]>([]);
  const [loans, setLoans] = useState<Loan[]>([]);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);

  const [newBeneficiary, setNewBeneficiary] = useState<CreateBeneficiaryRequest>({
    beneficiaryName: "",
    beneficiaryEmail: "",
    accountNumber: "",
    relationship: "FRIEND",
  });

  const [newLimit, setNewLimit] = useState<SetSpendingLimitRequest>({
    category: "TRANSFER",
    limitType: "MONTHLY",
    limitAmount: 1000,
  });

  const loadData = async () => {
    try {
      setLoading(true);
      const [cardRes, benRes, limitRes, loanRes] = await Promise.all([
        cardApi.getCards(),
        beneficiaryApi.getAll(),
        spendingLimitApi.getAll(),
        loanApi.getAll(),
      ]);

      setCards(cardRes.data.data || []);
      setBeneficiaries(benRes.data.data || []);
      setLimits(limitRes.data.data || []);
      setLoans(loanRes.data.data || []);
    } catch (error: any) {
      setAlert({ type: "error", message: error.response?.data?.message || "Could not load advanced features" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const runAction = async (action: () => Promise<unknown>, successMessage: string) => {
    try {
      setBusy(true);
      await action();
      setAlert({ type: "success", message: successMessage });
      await loadData();
    } catch (error: any) {
      setAlert({ type: "error", message: error.response?.data?.message || "Action failed" });
    } finally {
      setBusy(false);
    }
  };

  const maskedCardNumber = (last4: string) => `**** **** **** ${last4}`;

  if (loading) return <LoadingSpinner fullScreen message="Loading banking features..." />;

  return (
    <BankPage
      title="Banking Features"
      subtitle="Cards, beneficiaries, spending controls, and loan products"
      maxWidth="xl"
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

      <Grid container spacing={2.4}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.4 }}>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1.4 }}>
                <CreditCardIcon color="primary" />
                <Typography variant="h6">Your Cards</Typography>
              </Stack>

              <Stack spacing={1.2}>
                {cards.length === 0 && (
                  <Typography variant="body2" color="text.secondary">
                    No cards found. Seed data includes cards for demo users.
                  </Typography>
                )}
                {cards.map((card) => (
                  <Card key={card.id} glass={false}>
                    <Box sx={{ p: 1.6 }}>
                      <Stack direction="row" justifyContent="space-between" alignItems="center">
                        <Box>
                          <Typography sx={{ fontWeight: 700 }}>{maskedCardNumber(card.last4Digits)}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {card.cardNetwork} • {card.cardType} • {card.status}
                          </Typography>
                        </Box>
                        <Stack direction="row" spacing={0.8}>
                          {card.isDefault ? (
                            <Chip color="success" size="small" label="Default" />
                          ) : (
                            <Button
                              type="button"
                              variant="outline"
                              size="sm"
                              disabled={busy}
                              onClick={() => runAction(() => cardApi.setDefault(card.id), "Default card updated")}
                            >
                              Set Default
                            </Button>
                          )}
                          {card.status === "ACTIVE" && (
                            <Button
                              type="button"
                              variant="danger"
                              size="sm"
                              disabled={busy}
                              onClick={() => runAction(() => cardApi.block(card.id), "Card blocked")}
                            >
                              Block
                            </Button>
                          )}
                        </Stack>
                      </Stack>
                    </Box>
                  </Card>
                ))}
              </Stack>
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.4 }}>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1.4 }}>
                <PeopleAltIcon color="primary" />
                <Typography variant="h6">Beneficiaries</Typography>
              </Stack>

              <Grid container spacing={1.2} sx={{ mb: 1.4 }}>
                <Grid size={{ xs: 12, md: 6 }}>
                  <Input
                    label="Name"
                    value={newBeneficiary.beneficiaryName}
                    onChange={(e) => setNewBeneficiary((prev) => ({ ...prev, beneficiaryName: e.target.value }))}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 6 }}>
                  <Input
                    label="Email"
                    value={newBeneficiary.beneficiaryEmail}
                    onChange={(e) => setNewBeneficiary((prev) => ({ ...prev, beneficiaryEmail: e.target.value }))}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 6 }}>
                  <Input
                    label="Account Number"
                    value={newBeneficiary.accountNumber}
                    onChange={(e) => setNewBeneficiary((prev) => ({ ...prev, accountNumber: e.target.value }))}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 6 }}>
                  <Input
                    label="Relationship"
                    value={newBeneficiary.relationship}
                    onChange={(e) =>
                      setNewBeneficiary((prev) => ({ ...prev, relationship: e.target.value.toUpperCase() }))
                    }
                    hint="FRIEND, FAMILY, WORK, OTHER"
                  />
                </Grid>
              </Grid>

              <Button
                type="button"
                icon={<AddCardIcon />}
                disabled={busy}
                onClick={() =>
                  runAction(
                    () => beneficiaryApi.add(newBeneficiary),
                    "Beneficiary added. Verification email sent."
                  )
                }
              >
                Add Beneficiary
              </Button>

              <Stack spacing={1.1} sx={{ mt: 1.6 }}>
                {beneficiaries.map((item) => (
                  <Card key={item.id} glass={false}>
                    <Box sx={{ p: 1.4 }}>
                      <Stack direction="row" justifyContent="space-between" alignItems="center">
                        <Box>
                          <Typography sx={{ fontWeight: 700 }}>{item.beneficiaryName}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {item.beneficiaryEmail} • {item.relationship}
                          </Typography>
                        </Box>
                        <Stack direction="row" spacing={0.8} alignItems="center">
                          <Chip
                            size="small"
                            color={item.isVerified ? "success" : "warning"}
                            label={item.isVerified ? "Verified" : "Pending"}
                            icon={<VerifiedUserIcon />}
                          />
                          <Button
                            type="button"
                            size="sm"
                            variant="outline"
                            disabled={busy}
                            onClick={() => runAction(() => beneficiaryApi.remove(item.id), "Beneficiary removed")}
                          >
                            Remove
                          </Button>
                        </Stack>
                      </Stack>
                    </Box>
                  </Card>
                ))}
              </Stack>
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.4 }}>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1.4 }}>
                <SavingsIcon color="primary" />
                <Typography variant="h6">Spending Limits</Typography>
              </Stack>

              <Grid container spacing={1.2} sx={{ mb: 1.4 }}>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Input
                    label="Category"
                    value={newLimit.category}
                    onChange={(e) => setNewLimit((prev) => ({ ...prev, category: e.target.value.toUpperCase() }))}
                    hint="TRANSFER, FOOD, OTHER..."
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Input
                    label="Period"
                    value={newLimit.limitType}
                    onChange={(e) => setNewLimit((prev) => ({ ...prev, limitType: e.target.value.toUpperCase() }))}
                    hint="DAILY, WEEKLY, MONTHLY"
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Input
                    label="Amount"
                    type="number"
                    min="0"
                    value={String(newLimit.limitAmount)}
                    onChange={(e) =>
                      setNewLimit((prev) => ({
                        ...prev,
                        limitAmount: Number(e.target.value || 0),
                      }))
                    }
                  />
                </Grid>
              </Grid>

              <Button
                type="button"
                disabled={busy}
                onClick={() => runAction(() => spendingLimitApi.set(newLimit), "Spending limit created")}
              >
                Save Limit
              </Button>

              <Stack spacing={1.1} sx={{ mt: 1.6 }}>
                {limits.map((limit) => (
                  <Card key={limit.id} glass={false}>
                    <Box sx={{ p: 1.4 }}>
                      <Stack direction="row" justifyContent="space-between" alignItems="center">
                        <Box>
                          <Typography sx={{ fontWeight: 700 }}>
                            {limit.category} • {limit.limitType}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {formatCurrency(limit.currentSpent)} of {formatCurrency(limit.limitAmount)} used
                          </Typography>
                        </Box>
                        <Stack direction="row" spacing={0.8} alignItems="center">
                          <Chip size="small" label={`Remaining ${formatCurrency(limit.remainingBudget)}`} />
                          <Button
                            type="button"
                            size="sm"
                            variant="outline"
                            disabled={busy}
                            onClick={() => runAction(() => spendingLimitApi.disable(limit.id), "Spending limit disabled")}
                          >
                            Disable
                          </Button>
                        </Stack>
                      </Stack>
                    </Box>
                  </Card>
                ))}
              </Stack>
            </Box>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <Card>
            <Box sx={{ p: 2.4 }}>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1.4 }}>
                <AddCardIcon color="primary" />
                <Typography variant="h6">Loans</Typography>
              </Stack>

              <Stack spacing={1.1}>
                {loans.length === 0 && (
                  <Typography variant="body2" color="text.secondary">
                    No loans found.
                  </Typography>
                )}
                {loans.map((loan) => (
                  <Card key={loan.id} glass={false}>
                    <Box sx={{ p: 1.5 }}>
                      <Stack direction="row" justifyContent="space-between" alignItems="center">
                        <Box>
                          <Typography sx={{ fontWeight: 700 }}>{loan.loanNumber}</Typography>
                          <Typography variant="body2" color="text.secondary">
                            {loan.loanType} • {loan.status}
                          </Typography>
                        </Box>
                        <Box sx={{ textAlign: "right" }}>
                          <Typography sx={{ fontWeight: 700 }}>{formatCurrency(loan.outstandingBalance)}</Typography>
                          <Typography variant="caption" color="text.secondary">
                            Outstanding balance
                          </Typography>
                        </Box>
                      </Stack>
                    </Box>
                  </Card>
                ))}
              </Stack>
            </Box>
          </Card>
        </Grid>
      </Grid>
    </BankPage>
  );
};

export default Features;
