import React, { useState } from "react";
import app from "../Firebase";
import { getDatabase, ref, get, remove } from "firebase/database";
import Navigation from "./Navigation";

function Delete() {
  const [fruitArray, setFruitArray] = useState([]);

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
      alert("Gagal mengambil data: " + error.message);
    }
  };

  const deleteData = async (id) => {
    const db = getDatabase(app);
    const fruitRef = ref(db, "nature/fruits/" + id);

    try {
      await remove(fruitRef);
      alert("🗑️ Data berhasil dihapus!");
      fetchData(); // refresh data
    } catch (error) {
      alert("❌ Gagal menghapus data: " + error.message);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: 50 }}>
      <Navigation />
      <h2>🗑️ Hapus Data Buah</h2>
      <button onClick={fetchData}>Fetch Data</button>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {fruitArray.map((item) => (
          <li key={item.fruitID} style={{ marginTop: 10 }}>
            🍉 <b>{item.fruitName}</b> — {item.fruitDefinition}{" "}
            <button
              onClick={() => deleteData(item.fruitID)}
              style={{ marginLeft: 10, color: "white", backgroundColor: "red" }}
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Delete;
 