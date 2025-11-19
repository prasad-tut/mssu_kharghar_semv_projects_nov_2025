import api from './api';

/**
 * Expense service for handling expense-related operations
 */
const expenseService = {
  /**
   * Get all expenses for the authenticated user
   * @param {Object} params - Query parameters
   * @param {number} params.page - Page number (0-indexed)
   * @param {number} params.size - Page size
   * @param {string} params.sort - Sort field and direction (e.g., 'expenseDate,desc')
   * @param {string} params.status - Filter by status
   * @param {number} params.categoryId - Filter by category ID
   * @param {string} params.startDate - Filter by start date (YYYY-MM-DD)
   * @param {string} params.endDate - Filter by end date (YYYY-MM-DD)
   * @returns {Promise<Object>} Paginated expense list
   */
  getAllExpenses: async (params = {}) => {
    try {
      const response = await api.get('/expenses', { params });
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Get a single expense by ID
   * @param {number} id - Expense ID
   * @returns {Promise<Object>} Expense details
   */
  getExpenseById: async (id) => {
    try {
      const response = await api.get(`/expenses/${id}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Create a new expense
   * @param {Object} expenseData - Expense data
   * @param {number} expenseData.categoryId - Category ID
   * @param {number} expenseData.amount - Expense amount
   * @param {string} expenseData.expenseDate - Expense date (YYYY-MM-DD)
   * @param {string} expenseData.description - Expense description
   * @returns {Promise<Object>} Created expense
   */
  createExpense: async (expenseData) => {
    try {
      const response = await api.post('/expenses', expenseData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Update an existing expense
   * @param {number} id - Expense ID
   * @param {Object} expenseData - Updated expense data
   * @param {number} expenseData.categoryId - Category ID
   * @param {number} expenseData.amount - Expense amount
   * @param {string} expenseData.expenseDate - Expense date (YYYY-MM-DD)
   * @param {string} expenseData.description - Expense description
   * @returns {Promise<Object>} Updated expense
   */
  updateExpense: async (id, expenseData) => {
    try {
      const response = await api.put(`/expenses/${id}`, expenseData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Delete an expense
   * @param {number} id - Expense ID
   * @returns {Promise<void>}
   */
  deleteExpense: async (id) => {
    try {
      await api.delete(`/expenses/${id}`);
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Submit an expense for approval
   * @param {number} id - Expense ID
   * @returns {Promise<Object>} Updated expense with SUBMITTED status
   */
  submitExpense: async (id) => {
    try {
      const response = await api.post(`/expenses/${id}/submit`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Get pending expenses (manager only)
   * @returns {Promise<Array>} List of pending expenses
   */
  getPendingExpenses: async () => {
    try {
      const response = await api.get('/expenses/pending');
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Approve an expense (manager only)
   * @param {number} id - Expense ID
   * @param {Object} approvalData - Approval data
   * @param {string} approvalData.reviewNotes - Optional review notes
   * @returns {Promise<Object>} Updated expense with APPROVED status
   */
  approveExpense: async (id, approvalData = {}) => {
    try {
      const response = await api.post(`/expenses/${id}/approve`, approvalData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Reject an expense (manager only)
   * @param {number} id - Expense ID
   * @param {Object} rejectionData - Rejection data
   * @param {string} rejectionData.reviewNotes - Optional review notes
   * @returns {Promise<Object>} Updated expense with REJECTED status
   */
  rejectExpense: async (id, rejectionData = {}) => {
    try {
      const response = await api.post(`/expenses/${id}/reject`, rejectionData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};

export default expenseService;
