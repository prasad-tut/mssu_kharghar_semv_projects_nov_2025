import axiosClient from "./axiosClient";

const customerClient = {
  getAll: () => axiosClient.get("/customer/get"),
  getById: (id) => axiosClient.get(`/customer/get/${id}`),
  add: (data) => axiosClient.post("/customer/add", data),
  update: (id, data) => axiosClient.put(`/customer/update/${id}`, data),
  delete: (id) => axiosClient.delete(`/customer/delete/${id}`),
};

export default customerClient;
