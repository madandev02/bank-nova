import UserCircleIcon from "@mui/icons-material/AccountCircle";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import PlaceIcon from "@mui/icons-material/Place";
import SaveIcon from "@mui/icons-material/Save";
import SmartphoneIcon from "@mui/icons-material/Smartphone";
import { Box, Grid, Stack, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UpdateProfileRequest, userApi } from "../api/client";
import Alert from "../components/Alert";
import BankPage from "../components/BankPage";
import Button from "../components/Button";
import Card from "../components/Card";
import Input from "../components/Input";
import LoadingSpinner from "../components/LoadingSpinner";

const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [alert, setAlert] = useState<{ type: "success" | "error"; message: string } | null>(null);
  const [profileData, setProfileData] = useState({
    name: "",
    email: "",
    phone: "",
    country: "",
    city: "",
    address: "",
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await userApi.getProfile();
      const data = response.data.data;
      setProfileData({
        name: data.name || "",
        email: data.email || "",
        phone: data.phone || "",
        country: data.country || "",
        city: data.city || "",
        address: data.address || "",
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to load profile";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      const updateRequest: UpdateProfileRequest = {
        name: profileData.name,
        phone: profileData.phone,
        country: profileData.country,
        city: profileData.city,
        address: profileData.address,
      };

      await userApi.updateProfile(updateRequest);
      setAlert({ type: "success", message: "Profile updated successfully!" });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to update profile";
      setAlert({ type: "error", message: errorMessage });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading profile..." />;

  return (
    <BankPage
      title="Profile & Identity"
      subtitle="Manage your account identity and wallet contact details"
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

      <Card>
        <Box sx={{ p: { xs: 2.3, md: 3 } }}>
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="Full Name"
                type="text"
                placeholder="John Doe"
                value={profileData.name}
                onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                icon={<UserCircleIcon fontSize="small" />}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="Email Address"
                type="email"
                value={profileData.email}
                disabled
                hint="Email cannot be changed"
                icon={<MailOutlineIcon fontSize="small" />}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="Phone Number"
                type="tel"
                placeholder="+1 (555) 000-0000"
                value={profileData.phone}
                onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                icon={<SmartphoneIcon fontSize="small" />}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="Country"
                type="text"
                placeholder="United States"
                value={profileData.country}
                onChange={(e) => setProfileData({ ...profileData, country: e.target.value })}
                icon={<PlaceIcon fontSize="small" />}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="City"
                type="text"
                placeholder="New York"
                value={profileData.city}
                onChange={(e) => setProfileData({ ...profileData, city: e.target.value })}
              />
            </Grid>

            <Grid size={{ xs: 12, md: 6 }}>
              <Input
                label="Address"
                type="text"
                placeholder="123 Main St, Apt 4B"
                value={profileData.address}
                onChange={(e) => setProfileData({ ...profileData, address: e.target.value })}
              />
            </Grid>
          </Grid>

          <Stack
            direction={{ xs: "column", sm: "row" }}
            spacing={1.4}
            justifyContent="space-between"
            alignItems={{ xs: "flex-start", sm: "center" }}
            sx={{ mt: 3 }}
          >
            <Typography variant="caption" color="text.secondary">
              Your profile information is protected by encrypted wallet security controls.
            </Typography>
            <Button icon={<SaveIcon />} loading={saving} disabled={saving} onClick={handleSave}>
              {saving ? "Saving..." : "Save Changes"}
            </Button>
          </Stack>
        </Box>
      </Card>
    </BankPage>
  );
};

export default Profile;
