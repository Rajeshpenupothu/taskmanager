import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import API from "../services/api";

function OAuthSuccess() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (token) {
      // ✅ store token
      localStorage.setItem("token", token);

      // ✅ set header globally
      API.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      // ✅ redirect cleanly (no back to oauth page)
      window.location.replace("/dashboard");
    } else {
      navigate("/");
    }
  }, [location, navigate]);

  return <p>Logging you in...</p>;
}

export default OAuthSuccess;