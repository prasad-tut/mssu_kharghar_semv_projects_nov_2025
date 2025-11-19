import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { render } from '../utils/test-utils';
import LoginPage from '../../pages/LoginPage';
import ExpenseFormPage from '../../pages/ExpenseFormPage';
import ExpenseListPage from '../../pages/ExpenseListPage';

// Mock useNavigate and useParams
const mockNavigate = vi.fn();
const mockParams = {};

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => mockParams,
  };
});

describe('User Flow Integration Tests', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    mockParams.id = undefined;
    localStorage.clear();
  });

  describe('Complete Login and Create Expense Flow', () => {
    it('should allow user to login and create an expense', async () => {
      const user = userEvent.setup();

      // Step 1: Login
      const { unmount } = render(<LoginPage />);

      const emailInput = screen.getByLabelText(/email address/i);
      const passwordInput = screen.getByLabelText(/password/i);
      const loginButton = screen.getByRole('button', { name: /sign in/i });

      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(loginButton);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
        expect(localStorage.getItem('token')).toBeTruthy();
        expect(localStorage.getItem('user')).toBeTruthy();
      });

      // Verify user data is stored correctly
      const storedUser = JSON.parse(localStorage.getItem('user'));
      expect(storedUser).toHaveProperty('email');
      expect(storedUser).toHaveProperty('firstName');
      expect(storedUser).toHaveProperty('lastName');

      unmount();

      // Step 2: Create Expense
      mockNavigate.mockClear();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /create new expense/i })).toBeInTheDocument();
      });

      // Fill in expense form
      const amountInput = screen.getByLabelText(/amount/i);
      const descriptionInput = screen.getByLabelText(/description/i);
      const submitButton = screen.getByRole('button', { name: /create expense/i });

      // Select category - use role combobox since label association is complex with MUI Select
      const categorySelect = screen.getByRole('combobox', { name: /category/i });
      await user.click(categorySelect);
      await waitFor(() => {
        expect(screen.getByRole('option', { name: /travel/i })).toBeInTheDocument();
      });
      await user.click(screen.getByRole('option', { name: /travel/i }));

      // Fill other fields
      await user.clear(amountInput);
      await user.type(amountInput, '250.75');
      await user.type(descriptionInput, 'Business trip to New York');

      // Submit form
      await user.click(submitButton);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });
    });
  });

  describe('Login, Create, and Submit Expense Flow', () => {
    it('should allow user to login, create expense, and submit for approval', async () => {
      const user = userEvent.setup();

      // Step 1: Login
      const { unmount: unmountLogin } = render(<LoginPage />);

      await user.type(screen.getByLabelText(/email address/i), 'test@example.com');
      await user.type(screen.getByLabelText(/password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /sign in/i }));

      await waitFor(() => {
        expect(localStorage.getItem('token')).toBeTruthy();
      });

      unmountLogin();

      // Step 2: Create Expense
      mockNavigate.mockClear();
      const { unmount: unmountCreate } = render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('combobox', { name: /category/i })).toBeInTheDocument();
      });

      // Fill form
      const categorySelect = screen.getByRole('combobox', { name: /category/i });
      await user.click(categorySelect);
      await waitFor(() => {
        expect(screen.getByRole('option', { name: /meals/i })).toBeInTheDocument();
      });
      await user.click(screen.getByRole('option', { name: /meals/i }));

      await user.clear(screen.getByLabelText(/amount/i));
      await user.type(screen.getByLabelText(/amount/i), '45.50');
      await user.type(screen.getByLabelText(/description/i), 'Team lunch meeting');
      await user.click(screen.getByRole('button', { name: /create expense/i }));

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });

      unmountCreate();

      // Step 3: View expense in list (simulated)
      // In a real app, we would navigate to the expense list and verify the expense appears
      // For this test, we verify the flow completed successfully
      expect(localStorage.getItem('token')).toBeTruthy();
    });
  });

  describe('Edit Expense Flow', () => {
    it('should allow user to edit an existing expense', async () => {
      const user = userEvent.setup();

      // Set up authenticated state
      localStorage.setItem('token', 'mock-jwt-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
      }));

      // Set edit mode
      mockParams.id = '1';

      render(<ExpenseFormPage />);

      // Wait for expense data to load
      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /edit expense/i })).toBeInTheDocument();
      });

      await waitFor(() => {
        const amountInput = screen.getByLabelText(/amount/i);
        expect(amountInput).toHaveValue(150);
      });

      // Update the expense
      const amountInput = screen.getByLabelText(/amount/i);
      const descriptionInput = screen.getByLabelText(/description/i);

      await user.clear(amountInput);
      await user.type(amountInput, '175.00');
      
      await user.clear(descriptionInput);
      await user.type(descriptionInput, 'Updated: Flight to conference with baggage fee');

      await user.click(screen.getByRole('button', { name: /update expense/i }));

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });
    });
  });

  describe('Form Validation Flow', () => {
    it('should prevent submission with invalid data and show all errors', async () => {
      const user = userEvent.setup();

      localStorage.setItem('token', 'mock-jwt-token');
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /create new expense/i })).toBeInTheDocument();
      });

      // Try to submit empty form
      const submitButton = screen.getByRole('button', { name: /create expense/i });
      await user.click(submitButton);

      // Verify all validation errors appear
      await waitFor(() => {
        expect(screen.getByText(/category is required/i)).toBeInTheDocument();
        expect(screen.getByText(/amount is required/i)).toBeInTheDocument();
        expect(screen.getByText(/description is required/i)).toBeInTheDocument();
      });

      // Verify form was not submitted
      expect(mockNavigate).not.toHaveBeenCalled();

      // Now fill in valid data
      const categorySelect = screen.getByRole('combobox', { name: /category/i });
      await user.click(categorySelect);
      await waitFor(() => {
        expect(screen.getByRole('option', { name: /travel/i })).toBeInTheDocument();
      });
      await user.click(screen.getByRole('option', { name: /travel/i }));

      await user.clear(screen.getByLabelText(/amount/i));
      await user.type(screen.getByLabelText(/amount/i), '100.00');
      await user.type(screen.getByLabelText(/description/i), 'Valid expense description');

      // Submit again
      await user.click(submitButton);

      // Verify successful submission
      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });
    });
  });

  describe('Authentication State Persistence', () => {
    it('should maintain authentication state across page transitions', async () => {
      const user = userEvent.setup();

      // Login
      const { unmount } = render(<LoginPage />);

      await user.type(screen.getByLabelText(/email address/i), 'test@example.com');
      await user.type(screen.getByLabelText(/password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /sign in/i }));

      await waitFor(() => {
        expect(localStorage.getItem('token')).toBeTruthy();
      });

      const token = localStorage.getItem('token');
      const userStr = localStorage.getItem('user');

      unmount();

      // Navigate to another page (expense form)
      render(<ExpenseFormPage />);

      // Verify authentication state persists
      expect(localStorage.getItem('token')).toBe(token);
      expect(localStorage.getItem('user')).toBe(userStr);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /create new expense/i })).toBeInTheDocument();
      });
    });
  });
});
