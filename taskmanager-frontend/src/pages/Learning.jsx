import { useEffect, useState } from "react";
import API from "../services/api";

function Learning() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchTopics = async () => {
    try {
      const res = await API.get("/topics");
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

  const handleAdd = async () => {
    if (!title.trim()) return;

    try {
      await API.post("/topics", { title, description });
      setTitle("");
      setDescription("");
      fetchTopics();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="learning-container">

      {/* Add Topic Card */}
      <div className="learning-card">
        <h3>Add What You Learned Today</h3>

        <input
          placeholder="Topic Title (e.g., Binary Search)"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <textarea
          placeholder="Write what you understood..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <button onClick={handleAdd}>Add Topic</button>
      </div>

      {/* Topics List */}
      <div className="learning-list">
        <h3>All Topics</h3>

        {loading ? (
          <p>Loading...</p>
        ) : topics.length === 0 ? (
          <p>No topics yet</p>
        ) : (
          topics.map((t) => (
            <div key={t.id} className="learning-item">
              <h4>{t.title}</h4>
              <p>{t.description}</p>

              <span className="stage-badge">
                Stage: {t.stage}
              </span>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default Learning;