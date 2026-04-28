import axios from "axios";

const API = axios.create({
  baseURL: "https://taskmanager-1-kxsc.onrender.com",
});

// ✅ Request interceptor (attach token)
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

// ✅ Response interceptor (handle 401 globally)
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 🔥 Token invalid or expired
      localStorage.removeItem("token");

      // Redirect to login
      window.location.href = "/";
    }

    return Promise.reject(error);
  }
);

export default API;