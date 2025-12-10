import React, { useState } from "react";
import app from "../Firebase";
import { getDatabase, ref, push, set } from "firebase/database";
import Navigation from "./Navigation";

function Write() {
  const [fruitName, setFruitName] = useState("");
  const [fruitDefinition, setFruitDefinition] = useState("");

  const saveData = async () => {
    const db = getDatabase(app);
    const fruitsRef = ref(db, "nature/fruits");
    const newFruitRef = push(fruitsRef);

    set(newFruitRef, { fruitName, fruitDefinition })
      .then(() => {
        alert("✅ Data berhasil disimpan!");
        setFruitName("");
        setFruitDefinition("");
      })
      .catch((error) => {
        alert("❌ Gagal menyimpan data: " + error.message);
        console.error(error);
      });
  };

  return (
    <div style={{ textAlign: "center", marginTop: 50 }}>
      <Navigation />
      <h2>✏️ Tambah Data ke Firebase</h2>
      <input
        type="text"
        placeholder="Nama buah"
        value={fruitName}
        onChange={(e) => setFruitName(e.target.value)}
      />
      <br />
      <input
        type="text"
        placeholder="Deskripsi buah"
        value={fruitDefinition}
        onChange={(e) => setFruitDefinition(e.target.value)}
      />
      <br />
      <button onClick={saveData}>Save Data</button>
    </div>
  );
}

export default Write;
