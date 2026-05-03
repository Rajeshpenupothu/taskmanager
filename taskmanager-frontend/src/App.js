import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import OAuthSuccess from "./pages/OAuthSuccess";
import PrivateRoute from "./components/PrivateRoute";
import Learning from "./pages/Learning";
import Revision from "./pages/Revision";

function App() {
  const token = localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>

        {/* ✅ Public Routes */}
        <Route
          path="/"
          element={token ? <Navigate to="/dashboard" replace /> : <Login />}
        />

        <Route
          path="/signup"
          element={token ? <Navigate to="/dashboard" replace /> : <Signup />}
        />

        <Route path="/oauth-success" element={<OAuthSuccess />} />
        <Route path="/forgot" element={<ForgotPassword />} />
        <Route path="/reset/:token" element={<ResetPassword />} />

        {/* 🔒 Protected Route */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />

        {/* 🔁 Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;