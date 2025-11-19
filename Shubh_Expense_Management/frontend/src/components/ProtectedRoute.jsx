import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

/**
 * ProtectedRoute Component
 * Wrapper component that protects routes requiring authentication
 * Optionally restricts access based on user roles
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components to render if authorized
 * @param {string[]} props.allowedRoles - Optional array of roles allowed to access this route
 * @param {string} props.redirectTo - Path to redirect unauthorized users (default: /login)
 */
const ProtectedRoute = ({ children, allowedRoles = [], redirectTo = '/login' }) => {
  const { isAuthenticated, user, loading } = useAuth();
  const location = useLocation();

  // Show loading spinner while checking authentication
  if (loading) {
    return <LoadingSpinner fullScreen message="Checking authentication..." />;
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to={redirectTo} state={{ from: location }} replace />;
  }

  // Check role-based authorization if roles are specified
  if (allowedRoles.length > 0 && user) {
    const hasRequiredRole = allowedRoles.includes(user.role);
    
    if (!hasRequiredRole) {
      // Redirect to dashboard or show unauthorized page
      return <Navigate to="/dashboard" replace />;
    }
  }

  // User is authenticated and authorized
  return children;
};

export default ProtectedRoute;
