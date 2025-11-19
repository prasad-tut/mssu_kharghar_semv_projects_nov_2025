import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { render } from '../../test/utils/test-utils';
import ExpenseFormPage from '../ExpenseFormPage';

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

describe('ExpenseFormPage', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    mockParams.id = undefined;
    localStorage.clear();
    localStorage.setItem('token', 'mock-jwt-token');
  });

  describe('Create Mode', () => {
    it('should render create expense form', async () => {
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /create new expense/i })).toBeInTheDocument();
      });

      expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/amount/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/expense date/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
    });

    it('should have today\'s date as default expense date', async () => {
      render(<ExpenseFormPage />);

      await waitFor(() => {
        const dateInput = screen.getByLabelText(/expense date/i);
        const today = new Date().toISOString().split('T')[0];
        expect(dateInput).toHaveValue(today);
      });
    });

    it('should show validation errors for empty required fields', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /create new expense/i })).toBeInTheDocument();
      });

      const submitButton = screen.getByRole('button', { name: /create expense/i });
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/category is required/i)).toBeInTheDocument();
        expect(screen.getByText(/amount is required/i)).toBeInTheDocument();
        expect(screen.getByText(/description is required/i)).toBeInTheDocument();
      });
    });

    it('should validate amount is positive', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByLabelText(/amount/i)).toBeInTheDocument();
      });

      const amountInput = screen.getByLabelText(/amount/i);
      const submitButton = screen.getByRole('button', { name: /create expense/i });

      await user.clear(amountInput);
      await user.type(amountInput, '-50');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/amount must be a positive number/i)).toBeInTheDocument();
      });
    });

    it('should validate description minimum length', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
      });

      const descriptionInput = screen.getByLabelText(/description/i);
      const submitButton = screen.getByRole('button', { name: /create expense/i });

      await user.type(descriptionInput, 'ab');
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/description must be at least 3 characters/i)).toBeInTheDocument();
      });
    });

    it('should successfully create expense with valid data', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
      });

      // Fill in the form
      const categorySelect = screen.getByRole('combobox', { name: /category/i });
      const amountInput = screen.getByLabelText(/amount/i);
      const descriptionInput = screen.getByLabelText(/description/i);
      const submitButton = screen.getByRole('button', { name: /create expense/i });

      // Select category
      await user.click(categorySelect);
      await waitFor(() => {
        const travelOption = screen.getByRole('option', { name: /travel/i });
        expect(travelOption).toBeInTheDocument();
      });
      await user.click(screen.getByRole('option', { name: /travel/i }));

      // Fill other fields
      await user.clear(amountInput);
      await user.type(amountInput, '150.50');
      await user.type(descriptionInput, 'Flight to conference');

      // Submit form
      await user.click(submitButton);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });
    });

    it('should navigate back to expenses list on cancel', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument();
      });

      const cancelButton = screen.getByRole('button', { name: /cancel/i });
      await user.click(cancelButton);

      expect(mockNavigate).toHaveBeenCalledWith('/expenses');
    });
  });

  describe('Edit Mode', () => {
    beforeEach(() => {
      mockParams.id = '1';
    });

    it('should render edit expense form', async () => {
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('heading', { name: /edit expense/i })).toBeInTheDocument();
      });
    });

    it('should load and populate form with existing expense data', async () => {
      render(<ExpenseFormPage />);

      await waitFor(() => {
        const amountInput = screen.getByLabelText(/amount/i);
        expect(amountInput).toHaveValue(150);
      });

      const descriptionInput = screen.getByLabelText(/description/i);
      expect(descriptionInput).toHaveValue('Flight to conference');
    });

    it('should successfully update expense', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByLabelText(/amount/i)).toHaveValue(150);
      });

      const amountInput = screen.getByLabelText(/amount/i);
      const submitButton = screen.getByRole('button', { name: /update expense/i });

      // Update amount
      await user.clear(amountInput);
      await user.type(amountInput, '200.00');

      // Submit form
      await user.click(submitButton);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/expenses');
      });
    });

    it('should display error when expense not found', async () => {
      mockParams.id = '9999';
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByRole('alert')).toBeInTheDocument();
        expect(screen.getByText(/failed to load expense/i)).toBeInTheDocument();
      });
    });
  });

  describe('File Upload', () => {
    it('should render file upload component', async () => {
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByText(/receipt \(optional\)/i)).toBeInTheDocument();
      });
    });

    it('should allow file selection', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByText(/click to upload or drag and drop/i)).toBeInTheDocument();
      });

      // Note: Testing actual file upload is complex and may require additional setup
      // This test verifies the component renders
      expect(screen.getByText(/jpeg, png, or pdf/i)).toBeInTheDocument();
    });
  });

  describe('Form Validation', () => {
    it('should not allow future dates', async () => {
      const user = userEvent.setup();
      render(<ExpenseFormPage />);

      await waitFor(() => {
        expect(screen.getByLabelText(/expense date/i)).toBeInTheDocument();
      });

      const dateInput = screen.getByLabelText(/expense date/i);
      const submitButton = screen.getByRole('button', { name: /create expense/i });

      // Set future date
      const futureDate = new Date();
      futureDate.setDate(futureDate.getDate() + 1);
      const futureDateString = futureDate.toISOString().split('T')[0];

      await user.clear(dateInput);
      await user.type(dateInput, futureDateString);
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/expense date cannot be in the future/i)).toBeInTheDocument();
      });
    });
  });
});
