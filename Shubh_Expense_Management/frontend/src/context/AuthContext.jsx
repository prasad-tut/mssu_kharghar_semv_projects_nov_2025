import { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

// Create the authentication context
const AuthContext = createContext(null);

/**
 * Custom hook to use the authentication context
 * @returns {Object} Authentication context value
 * @throws {Error} If used outside of AuthProvider
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

/**
 * Authentication Provider Component
 * Manages global authentication state and provides auth methods to children
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Initialize authentication state on mount
  useEffect(() => {
    const initializeAuth = () => {
      try {
        const token = authService.getToken();
        const storedUser = authService.getUser();
        
        if (token && storedUser) {
          setUser(storedUser);
          setIsAuthenticated(true);
        } else {
          setUser(null);
          setIsAuthenticated(false);
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @returns {Promise<Object>} Registration response
   */
  const register = async (userData) => {
    try {
      const response = await authService.register(userData);
      setUser(response.user);
      setIsAuthenticated(true);
      return response;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  };

  /**
   * Login user with credentials
   * @param {Object} credentials - Login credentials
   * @returns {Promise<Object>} Login response
   */
  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials);
      setUser(response.user);
      setIsAuthenticated(true);
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  /**
   * Logout current user
   */
  const logout = () => {
    authService.logout();
    setUser(null);
    setIsAuthenticated(false);
  };

  /**
   * Update user data in state and storage
   * @param {Object} updatedUser - Updated user data
   */
  const updateUser = (updatedUser) => {
    authService.setUser(updatedUser);
    setUser(updatedUser);
  };

  /**
   * Refresh authentication token
   * @returns {Promise<Object>} Refresh response
   */
  const refreshToken = async () => {
    try {
      const response = await authService.refreshToken();
      return response;
    } catch (error) {
      console.error('Token refresh error:', error);
      logout();
      throw error;
    }
  };

  // Context value provided to consumers
  const value = {
    user,
    isAuthenticated,
    loading,
    register,
    login,
    logout,
    updateUser,
    refreshToken,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
