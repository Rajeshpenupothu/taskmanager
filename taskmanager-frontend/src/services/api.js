import axios from "axios";

const API = axios.create({
  baseURL: "https://taskmanager-1-kxsc.onrender.com",
});

// ✅ REQUEST INTERCEPTOR (attach token)
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ RESPONSE INTERCEPTOR (SAFE VERSION)
API.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const token = localStorage.getItem("token");

    // 🚫 DO NOT redirect for login API
    const isLoginRequest = error.config?.url?.includes("/auth/login");

    // 🔥 Redirect ONLY if:
    // - 401 error
    // - token exists (user was logged in)
    // - NOT a login request
    if (status === 401 && token && !isLoginRequest) {
      localStorage.removeItem("token");

      // prevent reload loop
      if (window.location.pathname !== "/") {
        window.location.replace("/");
      }
    }

    return Promise.reject(error);
  }
);

export default API;