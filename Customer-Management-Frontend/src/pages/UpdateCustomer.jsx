import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import customerApi from "../api/customerClient";

export default function UpdateCustomer() {
  const { id } = useParams();
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

  useEffect(() => {
    customerApi
      .getById(id)
      .then((res) => setCustomer(res.data))
      .catch((err) => console.error("Error fetching customer:", err));
  }, [id]);

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await customerApi.update(id, customer);
      alert("Customer updated successfully!");
      navigate("/customers");
    } catch (err) {
      console.error("Error updating customer:", err);
    }
  };

  return (
    <div className="container mt-4">
      <h2>Update Customer</h2>
      <form onSubmit={handleSubmit}>
        {Object.keys(customer).map((key) => (
          <div className="mb-3" key={key}>
            <label className="form-label text-capitalize">{key}</label>
            <input
              type="text"
              name={key}
              value={customer[key] || ""}
              onChange={handleChange}
              className="form-control"
            />
          </div>
        ))}
        <button type="submit" className="btn btn-primary">
          Update Customer
        </button>
      </form>
    </div>
  );
}
