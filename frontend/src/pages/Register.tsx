import BoltIcon from "@mui/icons-material/Bolt";
import LockIcon from "@mui/icons-material/Lock";
import MailIcon from "@mui/icons-material/Mail";
import PersonIcon from "@mui/icons-material/Person";
import PhoneIcon from "@mui/icons-material/Phone";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Box, Checkbox, Chip, Container, FormControlLabel, IconButton, Link as MuiLink, Stack, Typography } from "@mui/material";
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi, RegisterRequest } from "../api/client";
import Alert from "../components/Alert";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import { saveToken, saveUserData } from "../utils/auth";
import { isValidEmail, isValidPassword } from "../utils/formatters";

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) newErrors.name = "Name is required";
    if (!isValidEmail(formData.email)) newErrors.email = "Valid email is required";
    if (!isValidPassword(formData.password)) {
      newErrors.password =
        "Password must be 8+ chars with uppercase, number, and special character";
    }
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }
    if (!formData.phone.trim()) newErrors.phone = "Phone number is required";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    setLoading(true);
    try {
      const registerRequest: RegisterRequest = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        mobileNumber: formData.phone,
      };

      const response = await authApi.register(registerRequest);
      const data = (response.data as any)?.data ?? response.data;
      const userId = String(data.userId ?? data.id ?? "");

      saveToken(data.token);
      saveUserData(data.email, userId, data.name);

      setAlert({ type: "success", message: "Account created successfully!" });
      setTimeout(() => navigate("/dashboard"), 1200);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Registration failed";
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
          "radial-gradient(circle at 12% 16%, rgba(0,163,255,0.20) 0%, transparent 35%), radial-gradient(circle at 80% 80%, rgba(20,184,166,0.18) 0%, transparent 42%)",
      }}
    >
      <Container maxWidth="sm">
        <Card style={{ overflow: "hidden" }}>
          <Box sx={{ p: { xs: 3, md: 5 } }}>
            <Stack spacing={2} alignItems="center" sx={{ mb: 3.5 }}>
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
              <Typography variant="h4">Create Account</Typography>
              <Typography color="text.secondary">Open your wallet with secure onboarding and protected transfers</Typography>
            </Stack>

            <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap sx={{ mb: 2.4 }}>
              <Chip size="small" label="JWT protected APIs" color="primary" variant="outlined" />
              <Chip size="small" label="Email OTP for transfers" color="secondary" variant="outlined" />
              <Chip size="small" label="Cards and spending controls" variant="outlined" />
            </Stack>

            {alert && (
              <Box sx={{ mb: 3 }}>
                <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />
              </Box>
            )}

            <Stack component="form" spacing={2} onSubmit={handleSubmit}>
              <Input
                label="Full Name"
                type="text"
                placeholder="John Doe"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                error={errors.name}
                disabled={loading}
                icon={<PersonIcon fontSize="small" />}
              />

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
                label="Phone Number"
                type="tel"
                placeholder="+1 (555) 000-0000"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                error={errors.phone}
                disabled={loading}
                icon={<PhoneIcon fontSize="small" />}
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

              <Input
                label="Confirm Password"
                type={showConfirmPassword ? "text" : "password"}
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={(e) =>
                  setFormData({ ...formData, confirmPassword: e.target.value })
                }
                error={errors.confirmPassword}
                disabled={loading}
                icon={<LockIcon fontSize="small" />}
                endAdornment={
                  <IconButton
                    edge="end"
                    size="small"
                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                    aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                  >
                    {showConfirmPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                  </IconButton>
                }
              />

              <FormControlLabel
                control={<Checkbox required size="small" />}
                label={
                  <Typography variant="caption" color="text.secondary">
                    I agree to BankNova Terms and Privacy Policy
                  </Typography>
                }
              />

              <Button type="submit" loading={loading} disabled={loading} size="lg">
                {loading ? "Creating Account..." : "Create Account"}
              </Button>
            </Stack>

            <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 3 }}>
              Already have an account?{" "}
              <MuiLink component={Link} to="/login" underline="none" sx={{ fontWeight: 700 }}>
                Sign In
              </MuiLink>
            </Typography>
          </Box>
        </Card>
      </Container>
    </Box>
  );
};

export default Register;
