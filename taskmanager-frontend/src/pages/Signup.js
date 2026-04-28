import { useState } from "react";
import API from "../services/api";
import { useNavigate, useLocation } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import "../styles/login.css";
import { FiEye, FiEyeOff } from "react-icons/fi";

function Signup() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();

  const params = new URLSearchParams(location.search);
  const error = params.get("error");

  const [showPassword, setShowPassword] = useState(false);

  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    if (!username || !email || !password) {
      setErrorMsg("All fields are required");
      return;
    }

    if (!validateEmail(email)) {
      setErrorMsg("Enter a valid email");
      return;
    }

    if (password.length < 6) {
      setErrorMsg("Password must be at least 6 characters");
      return;
    }

    try {
      setLoading(true);

      await API.post("/auth/signup", {
        username,
        email,
        password,
      });

      navigate("/");

    } catch (err) {
      setErrorMsg(err.response?.data || "Signup failed. Try again.");
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

        <h2>Create Account</h2>
        <p className="sub">Start managing your tasks</p>

        {(error === "not_registered" || errorMsg) && (
          <p className="error-text">
            {errorMsg || "This email is not registered. Please sign up."}
          </p>
        )}

        <form onSubmit={handleSignup}>

          {/* USERNAME */}
          <div className="input-group">
            <input
              type="text"
              placeholder=" "
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
            <label>Username</label>
          </div>

          {/* EMAIL */}
          <div className="input-group">
            <input
              type="email"
              placeholder=" "
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <label>Email</label>
          </div>

          {/* PASSWORD */}
          {/* PASSWORD */}
<div className="input-group password-group">
  <input
    type={showPassword ? "text" : "password"}
    placeholder=" "
    value={password}
    onChange={(e) => setPassword(e.target.value)}
  />
  <label>Password</label>

  <span
    className="eye-icon"
    onClick={() => setShowPassword(!showPassword)}
  >
    {showPassword ? <FiEyeOff /> : <FiEye />}
  </span>
</div>

          <button className="login-btn" disabled={loading}>
            {loading ? "Creating..." : "Create Account"}
          </button>

        </form>

        {/* LOGIN LINK */}
        <p className="auth-link">
          Already have an account?{" "}
          <span onClick={() => navigate("/")}>Login</span>
        </p>

        {/* DIVIDER */}
        <div className="divider">
          <span></span>
          <p>or</p>
          <span></span>
        </div>

        {/* GOOGLE */}
        <button className="google-btn" onClick={handleGoogleLogin}>
          <FcGoogle /> Continue with Google
        </button>

      </div>
    </div>
  );
}

export default Signup;