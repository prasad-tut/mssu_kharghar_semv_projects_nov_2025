import React from "react";
import { useNavigate } from "react-router-dom";
import useFetchCustomers from "../hooks/useFetchCustomers";
import customerApi from "../api/customerClient";

export default function CustomerList() {
  const { customers, loading, fetchCustomers } = useFetchCustomers();
  const navigate = useNavigate();

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this customer?")) {
      try {
        await customerApi.delete(id);
        fetchCustomers(); // Refresh after deletion
      } catch (error) {
        console.error("Error deleting customer:", error);
      }
    }
  };

  const handleEdit = (id) => {
    navigate(`/customer/update/${id}`);
  };

  const handleAdd = () => {
    navigate("/customer/add");
  };

  return (
    <div className="container mt-4">
      {/* ✅ Header with Add Customer button */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold">Customer List</h2>
        <button className="btn btn-success" onClick={handleAdd}>
          + Add Customer
        </button>
      </div>

      {/* ✅ Loader / Empty / Table Display */}
      {loading ? (
        <div className="text-center">Loading...</div>
      ) : customers.length === 0 ? (
        <div className="text-center">No customers found.</div>
      ) : (
        <div className="table-responsive">
          <table className="table table-striped table-bordered">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Address</th>
                <th>City</th>
                <th>State</th>
                <th>Postal Code</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((customer) => (
                <tr key={customer.id}>
                  <td>{customer.id}</td>
                  <td>{customer.firstName}</td>
                  <td>{customer.lastName}</td>
                  <td>{customer.email}</td>
                  <td>{customer.phoneNumber}</td>
                  <td>{customer.address}</td>
                  <td>{customer.city}</td>
                  <td>{customer.state}</td>
                  <td>{customer.postalCode}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-primary me-2"
                      onClick={() => handleEdit(customer.id)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(customer.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
