import React, { useState } from "react";
import app from "../Firebase";
import { getDatabase, ref, get } from "firebase/database";
import { useNavigate } from "react-router-dom";
import Navigation from "./Navigation";

function UpdateRead() {
  const [fruitArray, setFruitArray] = useState([]);
  const navigate = useNavigate();

  const fetchData = async () => {
    const db = getDatabase(app);
    const fruitsRef = ref(db, "nature/fruits");

    try {
      const snapshot = await get(fruitsRef);
      if (snapshot.exists()) {
        const data = snapshot.val();
        const ids = Object.keys(data);
        const result = ids.map((id) => ({
          fruitID: id,
          ...data[id],
        }));
        setFruitArray(result);
      } else {
        alert("Data tidak ditemukan.");
      }
    } catch (error) {
      console.error(error);
      alert("Gagal mengambil data: " + error.message);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: 50 }}>
      <Navigation />
      <h2>📝 Pilih Data untuk Diedit</h2>
      <button onClick={fetchData}>Fetch Data</button>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {fruitArray.map((item) => (
          <li key={item.fruitID} style={{ marginTop: 10 }}>
            🍇 <b>{item.fruitName}</b> — {item.fruitDefinition}  
            <button
              onClick={() =>
                navigate("/update", { state: { fruit: item } })
              }
              style={{ marginLeft: 10 }}
            >
              Edit
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default UpdateRead;
