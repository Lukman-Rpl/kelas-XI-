import React, { useState } from "react";
import app from "../Firebase";
import { getDatabase, ref, get } from "firebase/database";
import Navigation from "./Navigation";

function Read() {
  const [fruitArray, setFruitArray] = useState([]);

  const fetchData = async () => {
    const db = getDatabase(app);
    const fruitsRef = ref(db, "nature/fruits");

    try {
      const snapshot = await get(fruitsRef);
      if (snapshot.exists()) {
        const data = snapshot.val();
        const result = Object.values(data);
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
      <h2>📖 Lihat Data dari Firebase</h2>
      <button onClick={fetchData}>Fetch Data</button>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {fruitArray.map((item, index) => (
          <li key={index} style={{ marginTop: 10 }}>
            🍎 <b>{item.fruitName}</b> — {item.fruitDefinition}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Read;
