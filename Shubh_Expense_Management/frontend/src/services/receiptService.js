import api from './api';

/**
 * Receipt service for handling receipt file operations
 */
const receiptService = {
  /**
   * Upload a receipt file
   * @param {number} expenseId - The expense ID to attach the receipt to
   * @param {File} file - The receipt file to upload
   * @returns {Promise<Object>} Receipt details including ID
   */
  uploadReceipt: async (expenseId, file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await api.post('/receipts', formData, {
        params: {
          expenseId: expenseId,
        },
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Download a receipt file
   * @param {number} id - Receipt ID
   * @returns {Promise<Blob>} Receipt file blob
   */
  downloadReceipt: async (id) => {
    try {
      const response = await api.get(`/receipts/${id}`, {
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Delete a receipt
   * @param {number} id - Receipt ID
   * @returns {Promise<void>}
   */
  deleteReceipt: async (id) => {
    try {
      await api.delete(`/receipts/${id}`);
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};

export default receiptService;
