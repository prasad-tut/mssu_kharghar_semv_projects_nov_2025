import api from './api';

/**
 * Category service for handling category-related operations
 */
const categoryService = {
  /**
   * Get all available expense categories
   * @returns {Promise<Array>} List of categories
   */
  getAllCategories: async () => {
    try {
      const response = await api.get('/categories');
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Get a single category by ID
   * @param {number} id - Category ID
   * @returns {Promise<Object>} Category details
   */
  getCategoryById: async (id) => {
    try {
      const response = await api.get(`/categories/${id}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};

export default categoryService;
