import { BrowserRouter, Routes, Route } from "react-router-dom";
import DefaultLayout from "../layout/defaultLayout";
import Dashboard from "../pages/Dashboard";
import CustomerList from "../pages/CustomerList";
import UpdateCustomer from "../pages/UpdateCustomer";
import AddCustomer from "../pages/AddCustomer";

export default function DefaultRouter() {
  return (
    <BrowserRouter>
      <DefaultLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/customers" element={<CustomerList />} />
          <Route path="/customer/update/:id" element={<UpdateCustomer />} />
          <Route path="/customer/add" element={<AddCustomer />} />
        </Routes>
      </DefaultLayout>
    </BrowserRouter>
  );
}
