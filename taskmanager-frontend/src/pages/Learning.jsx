import { useState } from "react";
import API from "../services/api";

function Learning() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const handleAdd = async () => {
    try {
      await API.post("/topics", { title, description });
      setTitle("");
      setDescription("");
      alert("Topic added!");
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="dashboard-container">
      <h2>Learning</h2>

      <input
        placeholder="Topic Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />

      <input
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />

      <button onClick={handleAdd}>Add Topic</button>
    </div>
  );
}

export default Learning;