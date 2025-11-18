import React from "react";

export default function Sidebar({ isOpen }) {
  return (
    <div
      className={`bg-light border-end position-fixed h-100 p-3 shadow-sm ${
        isOpen ? "d-block" : "d-none d-md-block"
      }`}
      style={{ width: "240px", zIndex: 1050 }}
    >
      <h5 className="fw-bold text-primary mb-4">Menu</h5>
      <ul className="nav flex-column">
        <li className="nav-item mb-2">
          <a href="/" className="nav-link text-dark">Dashboard</a>
        </li>
        <li className="nav-item mb-2">
          <a href="/customers" className="nav-link text-dark">Manage Customers</a>
        </li>
        <li className="nav-item mb-2">
          <a href="/" className="nav-link text-dark">Reports</a>
        </li>
        <li className="nav-item mb-2">
          <a href="/" className="nav-link text-dark">Settings</a>
        </li>
      </ul>
    </div>
  );
}
