import React from "react";
import { useNavigate } from "react-router-dom";

function Navigation() {
  const navigate = useNavigate();

  return (
    <nav style={{ marginBottom: "20px" }}>
      <button onClick={() => navigate("/")}>🏠 Write</button>
      <button onClick={() => navigate("/read")}>📖 Read</button>
      <button onClick={() => navigate("/updateread")}>📝 Edit</button>
      <button onClick={() => navigate("/delete")}>🗑️ Delete</button>
    </nav>
  );
}

export default Navigation;
