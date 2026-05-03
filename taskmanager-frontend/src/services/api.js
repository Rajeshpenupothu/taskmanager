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

// ✅ RESPONSE INTERCEPTOR (FIXED VERSION)
API.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const token = localStorage.getItem("token");

    const url = error.config?.url || "";

    // 🚫 DO NOT redirect for:
    // - login request
    // - learning/revision APIs
    const isLoginRequest = url.includes("/auth/login");
    const isTopicRequest = url.includes("/topics");

    if (status === 401 && token && !isLoginRequest && !isTopicRequest) {
      console.warn("🔐 Token expired or invalid → logging out");

      localStorage.removeItem("token");

      if (window.location.pathname !== "/") {
        window.location.replace("/");
      }
    }

    return Promise.reject(error);
  }
);

export default API;