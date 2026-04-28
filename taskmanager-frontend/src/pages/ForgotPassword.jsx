import { useState } from "react";
import API from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/forgot.css";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    if (!email) {
      setErrorMsg("Please enter your email");
      return;
    }

    try {
      setLoading(true);

      await API.post("/auth/forgot-password", null, {
        params: { email },
      });

      setSent(true);
    } catch (err) {
      setErrorMsg(
        err.response?.data || "Failed to send reset email"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-container">
      <div className="forgot-card">

        {/* 🔥 TITLE CHANGED */}
        <h2>Enter Your Email</h2>

        {!sent ? (
          <>
            <p className="sub-text">
              We will send a password reset link to your email
            </p>

            {errorMsg && <p className="error-text">{errorMsg}</p>}

            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <input
                  type="email"
                  placeholder=" "
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                <label>Email Address</label>
              </div>

              <button disabled={loading}>
                {loading ? "Sending..." : "Send Reset Link"}
              </button>
            </form>
          </>
        ) : (
          <p className="success-text">
            Reset link has been sent to your email
          </p>
        )}

        <p
          className="back-link"
          onClick={() => navigate("/")}
        >
          ← Back to Login
        </p>

      </div>
    </div>
  );
}

export default ForgotPassword;