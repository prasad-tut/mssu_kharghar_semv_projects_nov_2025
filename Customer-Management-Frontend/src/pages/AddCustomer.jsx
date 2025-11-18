import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import customerApi from "../api/customerClient";

export default function AddCustomer() {
  const navigate = useNavigate();
  const [customer, setCustomer] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    address: "",
    city: "",
    state: "",
    postalCode: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      await customerApi.add(customer);
      navigate("/customers");
    } catch (err) {
      setError("Failed to add customer. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-4 text-center">Add New Customer</h2>
      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="row g-3">
          {[
            { name: "firstName", label: "First Name" },
            { name: "lastName", label: "Last Name" },
            { name: "email", label: "Email", type: "email" },
            { name: "phoneNumber", label: "Phone Number" },
            { name: "address", label: "Address" },
            { name: "city", label: "City" },
            { name: "state", label: "State" },
            { name: "postalCode", label: "Postal Code" },
          ].map(({ name, label, type = "text" }) => (
            <div className="col-md-6" key={name}>
              <label className="form-label">{label}</label>
              <input
                type={type}
                name={name}
                value={customer[name]}
                onChange={handleChange}
                className="form-control"
                required={name !== "address"}
              />
            </div>
          ))}
        </div>

        <div className="mt-4 text-center">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? "Adding..." : "Add Customer"}
          </button>
          <button
            type="button"
            className="btn btn-secondary ms-2"
            onClick={() => navigate("/customers")}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
