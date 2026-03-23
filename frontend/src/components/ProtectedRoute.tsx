import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { isAuthenticated, isTokenExpired } from "../utils/auth";

const ProtectedRoute: React.FC = () => {
  if (!isAuthenticated() || isTokenExpired()) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
