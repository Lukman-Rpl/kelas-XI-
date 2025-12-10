import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Write from "./components/Write";
import Read from "./components/Read";
import UpdateRead from "./components/UpdateRead";
import Update from "./components/Update";
import Delete from "./components/Delete";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Write />} />
        <Route path="/read" element={<Read />} />
        <Route path="/updateread" element={<UpdateRead />} />
        <Route path="/update" element={<Update />} />
        <Route path="/delete" element={<Delete />} />
      </Routes>
    </Router>
  );
}

export default App;
