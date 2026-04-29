import { Navigate, useLocation } from "react-router-dom";

function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  const location = useLocation();

  // ❌ No token → redirect to login
  if (!token) {
    return <Navigate to="/" state={{ from: location }} replace />;
  }

  // ✅ Token exists → allow access
  return children;
}

export default PrivateRoute;