# Authentication Context Usage

## Overview
The AuthContext provides global authentication state management for the application.

## Setup

Wrap your application with the AuthProvider in your main entry point:

```jsx
import { AuthProvider } from './context/AuthContext';

function App() {
  return (
    <AuthProvider>
      {/* Your app components */}
    </AuthProvider>
  );
}
```

## Using the Auth Context

Import and use the `useAuth` hook in any component:

```jsx
import { useAuth } from '../context/AuthContext';

function MyComponent() {
  const { user, isAuthenticated, login, logout, register } = useAuth();

  // Check if user is authenticated
  if (!isAuthenticated) {
    return <div>Please log in</div>;
  }

  // Access user data
  return (
    <div>
      <p>Welcome, {user.firstName}!</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}
```

## Available Methods

- `register(userData)` - Register a new user
- `login(credentials)` - Login with email and password
- `logout()` - Logout current user
- `updateUser(updatedUser)` - Update user data
- `refreshToken()` - Refresh authentication token

## Available State

- `user` - Current user object (null if not authenticated)
- `isAuthenticated` - Boolean indicating authentication status
- `loading` - Boolean indicating if auth state is being initialized

## Authentication Service

The `authService` provides low-level authentication operations:

```javascript
import authService from '../services/authService';

// Login
const response = await authService.login({ email, password });

// Register
const response = await authService.register({ email, password, firstName, lastName });

// Logout
authService.logout();

// Check authentication
const isAuth = authService.isAuthenticated();

// Get current user
const user = authService.getCurrentUser();
```

## Token Management

Tokens are automatically:
- Stored in localStorage on login/register
- Added to all API requests via Axios interceptor
- Removed on logout or 401 responses
- Cleared when 401 Unauthorized is received

## Automatic Redirect on 401

The Axios interceptor in `api.js` automatically:
- Detects 401 Unauthorized responses
- Clears authentication data
- Redirects to `/login` page
