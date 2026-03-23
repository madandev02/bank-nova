import { createTheme } from "@mui/material/styles";

export const buildMuiTheme = (isDark: boolean) =>
  createTheme({
    palette: {
      mode: isDark ? "dark" : "light",
      primary: {
        main: "#00A3FF",
      },
      secondary: {
        main: "#14B8A6",
      },
      background: {
        default: isDark ? "#0C111D" : "#F4F8FC",
        paper: isDark ? "#121B2A" : "#FFFFFF",
      },
      text: {
        primary: isDark ? "#E6EDF7" : "#0B1B33",
        secondary: isDark ? "#9CB0CC" : "#4A607D",
      },
    },
    shape: {
      borderRadius: 14,
    },
    shadows: [
      "none",
      "0 2px 8px rgba(15, 23, 42, 0.06)",
      "0 6px 18px rgba(15, 23, 42, 0.08)",
      "0 10px 24px rgba(15, 23, 42, 0.10)",
      "0 14px 30px rgba(15, 23, 42, 0.12)",
      "0 18px 34px rgba(15, 23, 42, 0.14)",
      "0 20px 36px rgba(15, 23, 42, 0.15)",
      "0 24px 40px rgba(15, 23, 42, 0.16)",
      "0 26px 42px rgba(15, 23, 42, 0.17)",
      "0 30px 46px rgba(15, 23, 42, 0.18)",
      "0 34px 50px rgba(15, 23, 42, 0.19)",
      "0 36px 52px rgba(15, 23, 42, 0.20)",
      "0 38px 55px rgba(15, 23, 42, 0.21)",
      "0 40px 58px rgba(15, 23, 42, 0.22)",
      "0 42px 60px rgba(15, 23, 42, 0.22)",
      "0 44px 62px rgba(15, 23, 42, 0.23)",
      "0 46px 64px rgba(15, 23, 42, 0.23)",
      "0 48px 66px rgba(15, 23, 42, 0.24)",
      "0 50px 68px rgba(15, 23, 42, 0.24)",
      "0 52px 70px rgba(15, 23, 42, 0.25)",
      "0 54px 72px rgba(15, 23, 42, 0.25)",
      "0 56px 74px rgba(15, 23, 42, 0.26)",
      "0 58px 76px rgba(15, 23, 42, 0.26)",
      "0 60px 78px rgba(15, 23, 42, 0.27)",
      "0 62px 80px rgba(15, 23, 42, 0.27)",
    ],
    typography: {
      fontFamily: '"Plus Jakarta Sans", "Space Grotesk", system-ui, sans-serif',
      h4: {
        fontWeight: 700,
        letterSpacing: "-0.02em",
      },
      h5: {
        fontWeight: 700,
      },
      button: {
        textTransform: "none",
        fontWeight: 700,
      },
    },
    components: {
      MuiCard: {
        styleOverrides: {
          root: {
            border: "1px solid rgba(100, 116, 139, 0.18)",
            backdropFilter: "blur(8px)",
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            boxShadow: "none",
          },
        },
      },
      MuiButton: {
        defaultProps: {
          disableElevation: true,
        },
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiTextField: {
        styleOverrides: {
          root: {
            "& .MuiOutlinedInput-root": {
              backdropFilter: "blur(4px)",
            },
          },
        },
      },
    },
  });
