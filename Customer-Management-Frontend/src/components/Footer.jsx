import React from "react";

export default function Footer() {
  return (
    <footer className="bg-primary text-white text-center py-3 mt-auto">
      <small>© {new Date().getFullYear()} Customer Management System</small>
    </footer>
  );
}
