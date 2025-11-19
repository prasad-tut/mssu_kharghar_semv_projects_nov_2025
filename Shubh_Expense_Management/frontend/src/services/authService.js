import api from './api';

/**
 * Authentication service for handling user authentication operations
 */
const authService = {
  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @param {string} userData.email - User email
   * @param {string} userData.password - User password
   * @param {string} userData.firstName - User first name
   * @param {string} userData.lastName - User last name
   * @returns {Promise<Object>} Authentication response with token and user data
   */
  register: async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      if (response.data.token) {
        authService.setToken(response.data.token);
        authService.setUser(response.data.user);
      }
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Login user with credentials
   * @param {Object} credentials - Login credentials
   * @param {string} credentials.email - User email
   * @param {string} credentials.password - User password
   * @returns {Promise<Object>} Authentication response with token and user data
   */
  login: async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials);
      if (response.data.token) {
        authService.setToken(response.data.token);
        authService.setUser(response.data.user);
      }
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  /**
   * Logout user and clear stored authentication data
   */
  logout: () => {
    authService.removeToken();
    authService.removeUser();
  },

  /**
   * Store JWT token in localStorage
   * @param {string} token - JWT token
   */
  setToken: (token) => {
    localStorage.setItem('token', token);
  },

  /**
   * Retrieve JWT token from localStorage
   * @returns {string|null} JWT token or null if not found
   */
  getToken: () => {
    return localStorage.getItem('token');
  },

  /**
   * Remove JWT token from localStorage
   */
  removeToken: () => {
    localStorage.removeItem('token');
  },

  /**
   * Store user data in localStorage
   * @param {Object} user - User data object
   */
  setUser: (user) => {
    localStorage.setItem('user', JSON.stringify(user));
  },

  /**
   * Retrieve user data from localStorage
   * @returns {Object|null} User data object or null if not found
   */
  getUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  /**
   * Remove user data from localStorage
   */
  removeUser: () => {
    localStorage.removeItem('user');
  },

  /**
   * Check if user is authenticated
   * @returns {boolean} True if user has a valid token
   */
  isAuthenticated: () => {
    return !!authService.getToken();
  },

  /**
   * Get current authenticated user
   * @returns {Object|null} Current user object or null
   */
  getCurrentUser: () => {
    return authService.getUser();
  },

  /**
   * Refresh authentication token
   * @returns {Promise<Object>} New authentication response
   */
  refreshToken: async () => {
    try {
      const response = await api.post('/auth/refresh');
      if (response.data.token) {
        authService.setToken(response.data.token);
      }
      return response.data;
    } catch (error) {
      authService.logout();
      throw error.response?.data || error;
    }
  },
};

export default authService;
