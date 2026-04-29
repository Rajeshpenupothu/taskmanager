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

// ✅ Response interceptor (FIXED)
API.interceptors.response.use(
  (response) => response,
  (error) => {
    const token = localStorage.getItem("token");

    // 🔥 Only redirect if user was logged in
    if (error.response?.status === 401 && token) {
      localStorage.removeItem("token");

      // Instead of full reload, just change URL
      window.location.replace("/");
    }

    return Promise.reject(error);
  }
);

export default API;