import { useEffect, useState, useCallback } from "react";
import API from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/dashboard.css";
import Learning from "./Learning";
import Revision from "./Revision";

function Dashboard() {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("dashboard");

  const navigate = useNavigate();

  const fetchTasks = useCallback(async () => {
    try {
      const res = await API.get("/tasks");
      setTasks(res.data.content || res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const addTask = async () => {
    if (!title.trim()) return;

    try {
      await API.post("/tasks", {
        title,
        description,
        status: "PENDING",
      });

      setTitle("");
      setDescription("");
      fetchTasks();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteTask = async (id) => {
    try {
      await API.delete(`/tasks/${id}`);
      fetchTasks();
    } catch (err) {
      console.error(err);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  // 📊 Stats
  const total = tasks.length;
  const pending = tasks.filter(t => t.status === "PENDING").length;
  const completed = tasks.filter(t => t.status === "COMPLETED").length;

  return (
    <div className="dashboard">

      {/* Sidebar */}
      <div className="sidebar">
        <h2>TaskManager</h2>

        <p className={`menu-item ${activeTab === "dashboard" ? "active" : ""}`}
          onClick={() => setActiveTab("dashboard")}>
          Dashboard
        </p>

        <p className={`menu-item ${activeTab === "learning" ? "active" : ""}`}
          onClick={() => setActiveTab("learning")}>
          Learning
        </p>

        <p className={`menu-item ${activeTab === "revision" ? "active" : ""}`}
          onClick={() => setActiveTab("revision")}>
          Revision
        </p>
      </div>

      {/* Main */}
      <div className="main">

        {/* Topbar */}
        <div className="topbar">
          <h2>{activeTab.toUpperCase()}</h2>
          <button className="logout-btn" onClick={logout}>Logout</button>
        </div>

        {/* DASHBOARD TAB */}
        {activeTab === "dashboard" && (
          <>
            {/* 📊 Stats */}
            <div className="stats">
              <div className="stat-card">Total: {total}</div>
              <div className="stat-card">Pending: {pending}</div>
              <div className="stat-card">Completed: {completed}</div>
            </div>

            {/* ➕ Add Task */}
            <div className="task-form">
              <input
                placeholder="Task Title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
              <input
                placeholder="Task Description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
              <button onClick={addTask}>Add Task</button>
            </div>

            {/* 🧾 Task Grid */}
            <div className="task-grid">
              {loading ? (
                <p className="empty">Loading tasks...</p>
              ) : tasks.length === 0 ? (
                <div className="empty-box">
                  <p>No tasks yet</p>
                  <span>Add your first task above</span>
                </div>
              ) : (
                tasks.map((task) => (
                  <div key={task.id} className="task-card">
                    <h4>{task.title}</h4>
                    <p>{task.description || "No description"}</p>

                    <span className={`badge ${task.status.toLowerCase()}`}>
                      {task.status}
                    </span>

                    <div className="task-actions">
                      <button onClick={() => deleteTask(task.id)}>
                        Delete
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </>
        )}

        {activeTab === "learning" && <Learning />}
        {activeTab === "revision" && <Revision />}
      </div>
    </div>
  );
}

export default Dashboard;