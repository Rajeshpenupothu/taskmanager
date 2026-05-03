import { useEffect, useState } from "react";
import API from "../services/api";

function Revision() {
  const [topics, setTopics] = useState([]);

  const fetchTopics = async () => {
    try {
      const res = await API.get("/topics/today");
      setTopics(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchTopics();
  }, []);

  const handleRevise = async (id) => {
    await API.put(`/topics/revise/${id}`);
    fetchTopics();
  };

  return (
    <div className="dashboard-container">
      <h2>Revision Today</h2>

      {topics.length === 0 ? (
        <p>No topics today</p>
      ) : (
        topics.map((t) => (
          <div key={t.id}>
            <h4>{t.title}</h4>
            <button onClick={() => handleRevise(t.id)}>
              Revise
            </button>
          </div>
        ))
      )}
    </div>
  );
}

export default Revision;