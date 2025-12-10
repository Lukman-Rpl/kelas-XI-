import React, { useState } from "react";
import app from "../Firebase";
import { getDatabase, ref, update } from "firebase/database";
import { useLocation } from "react-router-dom";
import Navigation from "./Navigation";

function Update() {
  const location = useLocation();
  const fruit = location.state?.fruit;

  const [fruitName, setFruitName] = useState(fruit?.fruitName || "");
  const [fruitDefinition, setFruitDefinition] = useState(
    fruit?.fruitDefinition || ""
  );

  const updateData = async () => {
    const db = getDatabase(app);
    const fruitRef = ref(db, "nature/fruits/" + fruit.fruitID);

    try {
      await update(fruitRef, {
        fruitName,
        fruitDefinition,
      });
      alert("✅ Data berhasil diperbarui!");
    } catch (error) {
      alert("❌ Gagal memperbarui data: " + error.message);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: 50 }}>
      <Navigation />
      <h2>✏️ Edit Data Buah</h2>
      <input
        type="text"
        value={fruitName}
        onChange={(e) => setFruitName(e.target.value)}
      />
      <br />
      <input
        type="text"
        value={fruitDefinition}
        onChange={(e) => setFruitDefinition(e.target.value)}
      />
      <br />
      <button onClick={updateData}>Update Data</button>
    </div>
  );
}

export default Update;
