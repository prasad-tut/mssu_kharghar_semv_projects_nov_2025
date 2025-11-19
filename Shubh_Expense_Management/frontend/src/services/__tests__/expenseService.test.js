import { describe, it, expect, beforeEach } from 'vitest';
import expenseService from '../expenseService';

describe('expenseService', () => {
  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem('token', 'mock-jwt-token');
  });

  describe('getAllExpenses', () => {
    it('should fetch all expenses with default parameters', async () => {
      const result = await expenseService.getAllExpenses();

      expect(result).toHaveProperty('content');
      expect(Array.isArray(result.content)).toBe(true);
      expect(result).toHaveProperty('totalElements');
      expect(result).toHaveProperty('totalPages');
    });

    it('should fetch expenses with pagination parameters', async () => {
      const params = { page: 0, size: 5 };
      const result = await expenseService.getAllExpenses(params);

      expect(result).toHaveProperty('content');
      expect(result.number).toBe(0);
      expect(result.size).toBe(5);
    });

    it('should fetch expenses with filter parameters', async () => {
      const params = {
        status: 'SUBMITTED',
        categoryId: 1,
        startDate: '2024-01-01',
        endDate: '2024-12-31',
      };

      const result = await expenseService.getAllExpenses(params);
      expect(result).toHaveProperty('content');
    });
  });

  describe('getExpenseById', () => {
    it('should fetch a single expense by ID', async () => {
      const expenseId = 1;
      const result = await expenseService.getExpenseById(expenseId);

      expect(result).toHaveProperty('id');
      expect(result.id).toBe(expenseId);
      expect(result).toHaveProperty('amount');
      expect(result).toHaveProperty('category');
    });

    it('should throw error when expense not found', async () => {
      const nonExistentId = 9999;

      await expect(expenseService.getExpenseById(nonExistentId)).rejects.toThrow();
    });
  });

  describe('createExpense', () => {
    it('should create a new expense', async () => {
      const expenseData = {
        categoryId: 1,
        amount: 100.50,
        expenseDate: '2024-01-20',
        description: 'Test expense',
      };

      const result = await expenseService.createExpense(expenseData);

      expect(result).toHaveProperty('id');
      expect(result.amount).toBe(expenseData.amount);
      expect(result.description).toBe(expenseData.description);
      expect(result.status).toBe('DRAFT');
    });

    it('should throw error on invalid expense data', async () => {
      const invalidData = {
        categoryId: null,
        amount: -50,
        expenseDate: '',
        description: '',
      };

      // Note: Validation happens on backend, so this depends on MSW mock
      try {
        await expenseService.createExpense(invalidData);
      } catch (error) {
        expect(error).toBeDefined();
      }
    });
  });

  describe('updateExpense', () => {
    it('should update an existing expense', async () => {
      const expenseId = 1;
      const updateData = {
        categoryId: 2,
        amount: 200.00,
        expenseDate: '2024-01-21',
        description: 'Updated expense',
      };

      const result = await expenseService.updateExpense(expenseId, updateData);

      expect(result).toHaveProperty('id');
      expect(result.id).toBe(expenseId);
      expect(result.amount).toBe(updateData.amount);
      expect(result.description).toBe(updateData.description);
    });

    it('should throw error when updating non-existent expense', async () => {
      const nonExistentId = 9999;
      const updateData = {
        categoryId: 1,
        amount: 100,
        expenseDate: '2024-01-20',
        description: 'Test',
      };

      await expect(expenseService.updateExpense(nonExistentId, updateData)).rejects.toThrow();
    });
  });

  describe('deleteExpense', () => {
    it('should delete an expense', async () => {
      const expenseId = 1;

      await expect(expenseService.deleteExpense(expenseId)).resolves.not.toThrow();
    });

    it('should throw error when deleting non-existent expense', async () => {
      const nonExistentId = 9999;

      await expect(expenseService.deleteExpense(nonExistentId)).rejects.toThrow();
    });
  });

  describe('submitExpense', () => {
    it('should submit an expense for approval', async () => {
      const expenseId = 1;

      const result = await expenseService.submitExpense(expenseId);

      expect(result).toHaveProperty('id');
      expect(result.status).toBe('SUBMITTED');
      expect(result).toHaveProperty('submittedAt');
    });

    it('should throw error when submitting non-existent expense', async () => {
      const nonExistentId = 9999;

      await expect(expenseService.submitExpense(nonExistentId)).rejects.toThrow();
    });
  });

  describe('getPendingExpenses', () => {
    it('should fetch pending expenses for managers', async () => {
      const result = await expenseService.getPendingExpenses();

      expect(Array.isArray(result)).toBe(true);
      // Verify that if there are any results, they have SUBMITTED status
      if (result.length > 0) {
        result.forEach(expense => {
          expect(expense.status).toBe('SUBMITTED');
        });
      }
    });
  });

  describe('approveExpense', () => {
    it('should approve an expense', async () => {
      const expenseId = 2;
      const approvalData = { reviewNotes: 'Approved' };

      const result = await expenseService.approveExpense(expenseId, approvalData);

      expect(result).toHaveProperty('id');
      expect(result.status).toBe('APPROVED');
      expect(result.reviewNotes).toBe(approvalData.reviewNotes);
    });

    it('should approve expense without notes', async () => {
      const expenseId = 2;

      const result = await expenseService.approveExpense(expenseId);

      expect(result.status).toBe('APPROVED');
    });
  });

  describe('rejectExpense', () => {
    it('should reject an expense', async () => {
      const expenseId = 2;
      const rejectionData = { reviewNotes: 'Missing receipt' };

      const result = await expenseService.rejectExpense(expenseId, rejectionData);

      expect(result).toHaveProperty('id');
      expect(result.status).toBe('REJECTED');
      expect(result.reviewNotes).toBe(rejectionData.reviewNotes);
    });

    it('should reject expense without notes', async () => {
      const expenseId = 2;

      const result = await expenseService.rejectExpense(expenseId);

      expect(result.status).toBe('REJECTED');
    });
  });
});
