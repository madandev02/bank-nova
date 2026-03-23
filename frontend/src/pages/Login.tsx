import BoltIcon from "@mui/icons-material/Bolt";
import LockIcon from "@mui/icons-material/Lock";
import MailIcon from "@mui/icons-material/Mail";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import {
    Box,
    Checkbox,
    Chip,
    Container,
    Divider,
    FormControlLabel,
    IconButton,
    Link as MuiLink,
    Stack,
    Typography,
} from "@mui/material";
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../api/client";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import { saveToken, saveUserData } from "../utils/auth";
import { isValidEmail } from "../utils/formatters";

const Login: React.FC = () => {
  const demoAccounts = [
    { label: "Primary Demo", email: "demo@banknova.com", password: "DemoWallet123!" },
    { label: "Standard User", email: "test@banknova.com", password: "Password123" },
    { label: "Receiver User", email: "jane@banknova.com", password: "Password123" },
  ];

  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.email.trim()) newErrors.email = "Email is required";
    else if (!isValidEmail(formData.email)) newErrors.email = "Valid email is required";

    if (!formData.password.trim()) newErrors.password = "Password is required";
    else if (formData.password.length < 6) newErrors.password = "Valid password is required";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    setLoading(true);
    try {
      const response = await authApi.login(formData.email, formData.password);
      const payload = (response.data as any)?.data ?? response.data;
      const userId = String(payload.userId ?? payload.id ?? "");

      saveToken(payload.token, rememberMe);
      saveUserData(payload.email, userId, payload.name);

      setAlert({ type: "success", message: "Login successful!" });
      setTimeout(() => navigate("/dashboard"), 1000);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Login failed";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        py: { xs: 6, md: 10 },
        background:
          "radial-gradient(circle at 10% 20%, rgba(0,163,255,0.20) 0%, transparent 40%), radial-gradient(circle at 85% 80%, rgba(20,184,166,0.20) 0%, transparent 42%)",
      }}
    >
      <Container maxWidth="sm">
        <Card style={{ overflow: "hidden" }}>
          <Box sx={{ p: { xs: 3, md: 5 } }}>
            <Stack spacing={2} alignItems="center" sx={{ mb: 4 }}>
              <Box
                sx={{
                  width: 64,
                  height: 64,
                  borderRadius: 3,
                  display: "grid",
                  placeItems: "center",
                  color: "white",
                  background: "linear-gradient(135deg, #00A3FF 0%, #14B8A6 100%)",
                }}
              >
                <BoltIcon />
              </Box>
              <Typography variant="h4">Welcome Back</Typography>
              <Typography color="text.secondary">Secure access to your digital banking workspace</Typography>
            </Stack>

            {alert && (
              <Box sx={{ mb: 3 }}>
                <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
              </Box>
            )}

            <Stack component="form" spacing={2.2} onSubmit={handleSubmit}>
              <Box
                sx={{
                  p: 1.4,
                  borderRadius: 2,
                  border: "1px solid",
                  borderColor: "divider",
                  bgcolor: "rgba(148, 163, 184, 0.06)",
                }}
              >
                <Typography variant="caption" color="text.secondary" sx={{ display: "block", mb: 1 }}>
                  Quick demo access
                </Typography>
                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                  {demoAccounts.map((account) => (
                    <Chip
                      key={account.email}
                      label={account.label}
                      size="small"
                      clickable
                      onClick={() =>
                        setFormData({
                          email: account.email,
                          password: account.password,
                        })
                      }
                    />
                  ))}
                </Stack>
              </Box>

              <Input
                label="Email Address"
                type="email"
                placeholder="you@example.com"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                error={errors.email}
                disabled={loading}
                icon={<MailIcon fontSize="small" />}
              />

              <Input
                label="Password"
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                error={errors.password}
                disabled={loading}
                icon={<LockIcon fontSize="small" />}
                endAdornment={
                  <IconButton
                    edge="end"
                    size="small"
                    onClick={() => setShowPassword((prev) => !prev)}
                    aria-label={showPassword ? "Hide password" : "Show password"}
                  >
                    {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                  </IconButton>
                }
              />

              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      size="small"
                    />
                  }
                  label={<Typography variant="body2">Remember me</Typography>}
                />
                <MuiLink component={Link} to="/forgot-password" underline="hover" variant="body2">
                  Forgot?
                </MuiLink>
              </Stack>

              <Button type="submit" loading={loading} disabled={loading} size="lg">
                {loading ? "Signing In..." : "Sign In"}
              </Button>
            </Stack>

            <Divider sx={{ my: 2.5 }} />

            <Stack spacing={1.5} sx={{ mt: 3 }}>
              <Typography variant="body2" color="text.secondary" align="center">
                Don&apos;t have an account?
              </Typography>
              <Link to="/register" style={{ textDecoration: "none" }}>
                <Button variant="outline" className="w-full">
                  Create Account
                </Button>
              </Link>
            </Stack>
          </Box>
        </Card>
      </Container>
    </Box>
  );
};

export default Login;
