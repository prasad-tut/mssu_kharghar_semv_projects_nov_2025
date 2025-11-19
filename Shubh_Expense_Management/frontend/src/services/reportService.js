import api from './api';

/**
 * Report service for handling report-related operations
 */
const reportService = {
  /**
   * Get expense report summary with filters
   * @param {Object} params - Query parameters
   * @param {string} params.startDate - Filter by start date (YYYY-MM-DD)
   * @param {string} params.endDate - Filter by end date (YYYY-MM-DD)
   * @param {number} params.categoryId - Filter by category ID
   * @param {string} params.status - Filter by status
   * @returns {Promise<Object>} Report summary with expenses and totals
   */
  getReportSummary: async (params = {}) => {
    try {
      const response = await api.get('/reports/summary', { params });
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Export report as CSV or PDF
   * @param {Object} params - Query parameters
   * @param {string} params.format - Export format ('csv' or 'pdf')
   * @param {string} params.startDate - Filter by start date (YYYY-MM-DD)
   * @param {string} params.endDate - Filter by end date (YYYY-MM-DD)
   * @param {number} params.categoryId - Filter by category ID
   * @param {string} params.status - Filter by status
   * @returns {Promise<Blob>} File blob for download
   */
  exportReport: async (params = {}) => {
    try {
      const response = await api.get('/reports/export', {
        params,
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};

export default reportService;
