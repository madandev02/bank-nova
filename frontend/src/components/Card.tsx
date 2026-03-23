import { Card as MuiCard } from "@mui/material";
import React from "react";

interface CardProps {
  children: React.ReactNode;
  className?: string;
  hover?: boolean;
  glass?: boolean;
  style?: React.CSSProperties;
}

const Card: React.FC<CardProps> = ({ children, className = "", hover = false, glass = true, style }) => {
  return (
    <MuiCard
      style={style}
      className={className}
      sx={{
        borderRadius: 4,
        transition: "all 0.25s ease",
           backdropFilter: glass ? "blur(12px)" : "none",
           backgroundColor: (theme) =>
             glass
               ? theme.palette.mode === "dark"
                 ? "rgba(18, 27, 42, 0.78)"
                 : "rgba(255, 255, 255, 0.72)"
               : theme.palette.background.paper,
           border: (theme) =>
             glass
               ? `1px solid ${
                   theme.palette.mode === "dark"
                     ? "rgba(148, 163, 184, 0.18)"
                     : "rgba(100, 116, 139, 0.16)"
                 }`
               : `1px solid ${theme.palette.divider}`,
        ...(hover
          ? {
              "&:hover": {
                transform: "translateY(-2px)",
                   boxShadow: 8,
              },
            }
          : {}),
      }}
    >
      {children}
    </MuiCard>
  );
};

export default Card;
