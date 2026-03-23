import MoonIcon from "@mui/icons-material/DarkMode";
import SunIcon from "@mui/icons-material/LightMode";
import LogoutIcon from "@mui/icons-material/Logout";
import MenuIcon from "@mui/icons-material/Menu";
import {
    AppBar,
    Box,
    Button,
    Container,
    Drawer,
    IconButton,
    List,
    ListItemButton,
    ListItemText,
    Stack,
    Toolbar,
    Typography,
} from "@mui/material";
import React, { useMemo, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useTheme } from "../context/ThemeContext";
import { removeToken } from "../utils/auth";

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const { isDark, toggleTheme } = useTheme();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const token = localStorage.getItem("banknova_token");

  const privateLinks = useMemo(
    () => [
      { label: "Dashboard", to: "/dashboard" },
      { label: "Transfer", to: "/transfer" },
      { label: "History", to: "/history" },
      { label: "Modules", to: "/features" },
      { label: "Profile", to: "/profile" },
    ],
    []
  );

  const handleLogout = () => {
    removeToken();
    navigate("/login");
  };

  return (
    <AppBar
      position="sticky"
      color="transparent"
      elevation={0}
      sx={{
        borderBottom: "1px solid",
        borderColor: "divider",
        backdropFilter: "blur(10px)",
        backgroundColor: (theme) =>
          theme.palette.mode === "dark"
            ? "rgba(12, 17, 29, 0.84)"
            : "rgba(255, 255, 255, 0.84)",
      }}
    >
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ minHeight: 72 }}>
          <Button
            component={NavLink}
            to="/"
            color="inherit"
            sx={{
              p: 0,
              minWidth: 0,
              mr: 3,
              "&:hover": { backgroundColor: "transparent" },
            }}
          >
            <Stack direction="row" spacing={1.5} alignItems="center">
              <Box
                sx={{
                  width: 36,
                  height: 36,
                  borderRadius: 2,
                  display: "grid",
                  placeItems: "center",
                  fontWeight: 800,
                  color: "white",
                  background: "linear-gradient(135deg, #00A3FF 0%, #14B8A6 100%)",
                }}
              >
                B
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                BankNova
              </Typography>
            </Stack>
          </Button>

          <Stack
            direction="row"
            spacing={1}
            sx={{ display: { xs: "none", md: "flex" }, flexGrow: 1 }}
          >
            {token &&
              privateLinks.map((link) => (
                <Button
                  key={link.to}
                  component={NavLink}
                  to={link.to}
                  sx={{
                    color: "text.primary",
                    borderRadius: 3,
                    px: 1.6,
                    py: 1,
                    "&.active": {
                      backgroundColor: "primary.main",
                      color: "white",
                    },
                  }}
                >
                  {link.label}
                </Button>
              ))}
          </Stack>

          <Stack direction="row" spacing={1} alignItems="center" sx={{ ml: "auto" }}>
            <IconButton color="inherit" onClick={toggleTheme} aria-label="Toggle theme">
              {isDark ? <SunIcon /> : <MoonIcon />}
            </IconButton>

            {token ? (
              <Button
                color="error"
                variant="outlined"
                startIcon={<LogoutIcon />}
                onClick={handleLogout}
                sx={{ display: { xs: "none", sm: "inline-flex" } }}
              >
                Logout
              </Button>
            ) : (
              <>
                <Button component={NavLink} to="/login" sx={{ display: { xs: "none", sm: "inline-flex" } }}>
                  Sign In
                </Button>
                <Button
                  component={NavLink}
                  to="/register"
                  variant="contained"
                  sx={{ display: { xs: "none", sm: "inline-flex" } }}
                >
                  Sign Up
                </Button>
              </>
            )}

            <IconButton
              color="inherit"
              onClick={() => setMobileMenuOpen(true)}
              sx={{ display: { md: "none" } }}
            >
              <MenuIcon />
            </IconButton>
          </Stack>
        </Toolbar>
      </Container>

      <Drawer
        anchor="right"
        open={mobileMenuOpen}
        onClose={() => setMobileMenuOpen(false)}
        PaperProps={{ sx: { width: 280 } }}
      >
        <Box sx={{ p: 2 }}>
          <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 700 }}>
            Menu
          </Typography>
          <List>
            {token &&
              privateLinks.map((link) => (
                <ListItemButton
                  key={link.to}
                  component={NavLink}
                  to={link.to}
                  onClick={() => setMobileMenuOpen(false)}
                >
                  <ListItemText primary={link.label} />
                </ListItemButton>
              ))}

            {!token && (
              <>
                <ListItemButton component={NavLink} to="/login" onClick={() => setMobileMenuOpen(false)}>
                  <ListItemText primary="Sign In" />
                </ListItemButton>
                <ListItemButton component={NavLink} to="/register" onClick={() => setMobileMenuOpen(false)}>
                  <ListItemText primary="Sign Up" />
                </ListItemButton>
              </>
            )}

            {token && (
              <ListItemButton
                onClick={() => {
                  handleLogout();
                  setMobileMenuOpen(false);
                }}
              >
                <ListItemText primary="Logout" />
              </ListItemButton>
            )}
          </List>
        </Box>
      </Drawer>
    </AppBar>
  );
};

export default Navbar;
