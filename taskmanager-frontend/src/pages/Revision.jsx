import { useEffect, useState } from "react";
import API from "../services/api";

function Revision() {
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchTopics = async () => {
    try {
      const res = await API.get("/topics/today");
      setTopics(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTopics();
  }, []);

  // 🔁 NORMAL (1 → 2 → 4 → 8)
  const handleRevise = async (id) => {
    try {
      await API.put(`/topics/revise/${id}`);
      fetchTopics();
    } catch (err) {
      console.error(err);
    }
  };

  // 🔴 HARD → stay same stage (no API call)
  const handleHard = (id) => {
    alert("⚠️ Keep practicing this topic again today");
  };

  // 🟢 EASY → jump faster (call twice)
  const handleEasy = async (id) => {
    try {
      await API.put(`/topics/revise/${id}`);
      await API.put(`/topics/revise/${id}`);
      fetchTopics();
    } catch (err) {
      console.error(err);
    }
  };

  // 🧠 Stage label
  const getStageLabel = (stage) => {
    switch (stage) {
      case 1:
        return "Day 1 (Learning)";
      case 2:
        return "Day 3 (Revision)";
      case 4:
        return "Day 7 (Strong)";
      case 8:
        return "Completed";
      default:
        return "Unknown";
    }
  };

  // 🎨 Stage color
  const getStageColor = (stage) => {
    switch (stage) {
      case 1:
        return "#52c41a"; // green
      case 2:
        return "#faad14"; // yellow
      case 4:
        return "#1677ff"; // blue
      case 8:
        return "#999"; // gray
      default:
        return "#ddd";
    }
  };

  return (
    <div className="revision-container">
      <h2>Revision Today</h2>

      {loading ? (
        <p>Loading...</p>
      ) : topics.length === 0 ? (
        <div className="empty-box">
          <p>No topics today 🎉</p>
          <span>You’re all caught up!</span>
        </div>
      ) : (
        topics.map((t) => (
          <div key={t.id} className="revision-card">

            <h3>{t.title}</h3>

            <p className="desc">
              {t.description || "No description"}
            </p>

            <div
              className="stage-badge"
              style={{ background: getStageColor(t.stage) }}
            >
              {getStageLabel(t.stage)}
            </div>

            <p className="next-date">
              Next: {t.nextRevisionDate || "Completed"}
            </p>

            {/* 🔘 ACTION BUTTONS */}
            <div className="revision-actions">
              <button
                className="hard"
                onClick={() => handleHard(t.id)}
              >
                Hard
              </button>

              <button
                className="revise"
                onClick={() => handleRevise(t.id)}
              >
                Revise
              </button>

              <button
                className="easy"
                onClick={() => handleEasy(t.id)}
              >
                Easy
              </button>
            </div>
          </div>
        ))
      )}
    </div>
  );
}

export default Revision;