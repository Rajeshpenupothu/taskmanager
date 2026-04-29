import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import { FiEye, FiEyeOff } from "react-icons/fi";
import API from "../services/api";
import "../styles/login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // ✅ keep as it is (no issue here now)
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token && !email && !password) {
      navigate("/dashboard");
    }
  }, [navigate, email, password]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    localStorage.removeItem("token");

    if (!email || !password) {
      setErrorMsg("Please fill all fields");
      return;
    }

    try {
      setLoading(true);

      const res = await API.post("/auth/login", {
        email,
        password,
      });

      localStorage.setItem("token", res.data.token);

      API.defaults.headers.common["Authorization"] =
        `Bearer ${res.data.token}`;

      navigate("/dashboard");

    } catch (err) {

      const msg =
        err.response?.data?.message ||
        err.response?.data ||
        "Invalid email or password";

      setErrorMsg(msg);

    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href =
      "https://taskmanager-1-kxsc.onrender.com/oauth2/authorization/google";
  };

  return (
    <div className="login-container">
      <div className="login-card">

        <h2>Welcomes You</h2>

        {errorMsg && (
          <div className="error-box">
            {errorMsg}
          </div>
        )}

        <form onSubmit={handleLogin}>

          {/* EMAIL */}
          <div className="input-group">
            <input
              type="email"
              placeholder=" "
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}   // ❌ removed error clear
            />
            <label>Email</label>
          </div>

          {/* PASSWORD */}
          <div className="input-group password-group">
            <input
              type={showPassword ? "text" : "password"}
              placeholder=" "
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)} // ❌ removed error clear
            />
            <label>Password</label>

            <span
              className="eye-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FiEyeOff /> : <FiEye />}
            </span>
          </div>

          <p
            className="links"
            onClick={() => navigate("/forgot")}
          >
            Forgot password?
          </p>

          <button className="login-btn" disabled={loading}>
            {loading ? "Signing in..." : "SIGN IN"}
          </button>

        </form>

        <p className="auth-link">
          Don’t have an account?{" "}
          <span onClick={() => navigate("/signup")}>Sign up</span>
        </p>

        <div className="divider">
          <span></span>
          <p>or</p>
          <span></span>
        </div>

        <button className="google-btn" onClick={handleGoogleLogin}>
          <FcGoogle /> SIGN IN WITH GOOGLE
        </button>

      </div>
    </div>
  );
}

export default Login;