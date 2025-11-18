import { create } from "zustand";

const useCustomerStore = create((set) => ({
  customers: [],
  setCustomers: (data) => set({ customers: data }),
}));

export default useCustomerStore;
