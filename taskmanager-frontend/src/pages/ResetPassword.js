import { useState } from "react";
import API from "../services/api";
import { useNavigate, useParams } from "react-router-dom";
import { FiEye, FiEyeOff } from "react-icons/fi";
import "../styles/reset.css";

function ResetPassword() {
  const { token } = useParams();
  const navigate = useNavigate();

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const handleReset = async (e) => {
    e.preventDefault();
    setErrorMsg("");
    setSuccessMsg("");

    if (!password || !confirmPassword) {
      setErrorMsg("Please fill all fields");
      return;
    }

    if (password.length < 6) {
      setErrorMsg("Password must be at least 6 characters");
      return;
    }

    if (password !== confirmPassword) {
      setErrorMsg("Passwords do not match");
      return;
    }

    try {
      setLoading(true);

      await API.post("/auth/reset-password", null, {
        params: { token, password },
      });

      setSuccessMsg("Password updated successfully!");

      setTimeout(() => {
        navigate("/");
      }, 1500);

    } catch (err) {
      setErrorMsg(
        err.response?.data || "Reset failed. Try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reset-container">
      <div className="reset-card">

        <h2>Reset Password</h2>

        {errorMsg && <p className="error-text">{errorMsg}</p>}
        {successMsg && <p className="success-text">{successMsg}</p>}

        <form onSubmit={handleReset}>

          {/* PASSWORD */}
          <div className="input-group password-group">
            <input
              type={showPassword ? "text" : "password"}
              placeholder=" "
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <label>New Password</label>

            <span
              className="eye-icon"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FiEyeOff /> : <FiEye />}
            </span>
          </div>

          {/* CONFIRM PASSWORD */}
          <div className="input-group password-group">
            <input
              type={showConfirm ? "text" : "password"}
              placeholder=" "
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            <label>Confirm Password</label>

            <span
              className="eye-icon"
              onClick={() => setShowConfirm(!showConfirm)}
            >
              {showConfirm ? <FiEyeOff /> : <FiEye />}
            </span>
          </div>

          <button disabled={loading}>
            {loading ? "Updating..." : "Reset Password"}
          </button>

        </form>

        <p className="back-link" onClick={() => navigate("/")}>
          ← Back to Login
        </p>

      </div>
    </div>
  );
}

export default ResetPassword;