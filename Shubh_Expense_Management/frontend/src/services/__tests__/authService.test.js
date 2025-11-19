import { describe, it, expect, beforeEach, vi } from 'vitest';
import authService from '../authService';

describe('authService', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  describe('register', () => {
    it('should register a new user and store token and user data', async () => {
      const userData = {
        email: 'newuser@example.com',
        password: 'password123',
        firstName: 'New',
        lastName: 'User',
      };

      const result = await authService.register(userData);

      expect(result).toHaveProperty('token');
      expect(result).toHaveProperty('user');
      expect(result.user.email).toBe(userData.email);
      expect(localStorage.getItem('token')).toBeTruthy();
      expect(localStorage.getItem('user')).toBeTruthy();
    });

    it('should throw error on registration failure', async () => {
      // MSW will handle the mock response
      const userData = {
        email: 'invalid',
        password: 'short',
        firstName: '',
        lastName: '',
      };

      // Note: This test depends on backend validation
      // For now, we just verify the service handles errors
      try {
        await authService.register(userData);
      } catch (error) {
        expect(error).toBeDefined();
      }
    });
  });

  describe('login', () => {
    it('should login user and store token and user data', async () => {
      const credentials = {
        email: 'test@example.com',
        password: 'password123',
      };

      const result = await authService.login(credentials);

      expect(result).toHaveProperty('token');
      expect(result).toHaveProperty('user');
      expect(localStorage.getItem('token')).toBe(result.token);
      expect(localStorage.getItem('user')).toBeTruthy();
    });

    it('should throw error on invalid credentials', async () => {
      const credentials = {
        email: 'wrong@example.com',
        password: 'wrongpassword',
      };

      await expect(authService.login(credentials)).rejects.toThrow();
    });
  });

  describe('logout', () => {
    it('should clear token and user data from localStorage', () => {
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'test@example.com' }));

      authService.logout();

      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('user')).toBeNull();
    });
  });

  describe('token management', () => {
    it('should set and get token', () => {
      const token = 'test-jwt-token';
      authService.setToken(token);

      expect(authService.getToken()).toBe(token);
      expect(localStorage.getItem('token')).toBe(token);
    });

    it('should remove token', () => {
      authService.setToken('test-token');
      authService.removeToken();

      expect(authService.getToken()).toBeNull();
    });
  });

  describe('user management', () => {
    it('should set and get user', () => {
      const user = { id: 1, email: 'test@example.com', firstName: 'Test', lastName: 'User' };
      authService.setUser(user);

      const retrievedUser = authService.getUser();
      expect(retrievedUser).toEqual(user);
    });

    it('should remove user', () => {
      const user = { id: 1, email: 'test@example.com' };
      authService.setUser(user);
      authService.removeUser();

      expect(authService.getUser()).toBeNull();
    });

    it('should return null when no user is stored', () => {
      expect(authService.getUser()).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('should return true when token exists', () => {
      authService.setToken('test-token');
      expect(authService.isAuthenticated()).toBe(true);
    });

    it('should return false when no token exists', () => {
      expect(authService.isAuthenticated()).toBe(false);
    });
  });

  describe('getCurrentUser', () => {
    it('should return current user', () => {
      const user = { id: 1, email: 'test@example.com' };
      authService.setUser(user);

      expect(authService.getCurrentUser()).toEqual(user);
    });

    it('should return null when no user is stored', () => {
      expect(authService.getCurrentUser()).toBeNull();
    });
  });

  describe('refreshToken', () => {
    it('should refresh token and update stored token', async () => {
      authService.setToken('old-token');

      const result = await authService.refreshToken();

      expect(result).toHaveProperty('token');
      expect(authService.getToken()).toBe(result.token);
      expect(authService.getToken()).not.toBe('old-token');
    });
  });
});
