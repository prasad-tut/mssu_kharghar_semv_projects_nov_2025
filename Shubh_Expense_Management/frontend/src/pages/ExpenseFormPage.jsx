import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import {
  Container,
  Box,
  Typography,
  Paper,
  TextField,
  Button,
  Alert,
  Grid,
} from '@mui/material';
import {
  Save as SaveIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';
import { LoadingSpinner, CategorySelect, FileUpload } from '../components';
import { expenseService, receiptService } from '../services';

/**
 * ExpenseFormPage Component
 * Form for creating and editing expenses
 */
const ExpenseFormPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = Boolean(id);

  const [loading, setLoading] = useState(isEditMode);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [uploadingReceipt, setUploadingReceipt] = useState(false);
  const [receiptFile, setReceiptFile] = useState(null);
  const [existingReceipt, setExistingReceipt] = useState(null);

  // Initialize react-hook-form
  const {
    control,
    handleSubmit,
    formState: { errors },
    setValue,
    reset,
  } = useForm({
    defaultValues: {
      categoryId: '',
      amount: '',
      expenseDate: new Date().toISOString().split('T')[0], // Today's date
      description: '',
    },
  });

  useEffect(() => {
    if (isEditMode) {
      fetchExpense();
    }
  }, [id]);

  /**
   * Fetch expense data for editing
   */
  const fetchExpense = async () => {
    setLoading(true);
    setError('');

    try {
      const expense = await expenseService.getExpenseById(id);
      
      // Populate form with expense data
      setValue('categoryId', expense.category?.id || '');
      setValue('amount', expense.amount || '');
      setValue('expenseDate', expense.expenseDate || '');
      setValue('description', expense.description || '');

      // Store existing receipt info if available
      if (expense.receipt) {
        setExistingReceipt(expense.receipt);
      }
    } catch (err) {
      console.error('Failed to fetch expense:', err);
      setError(err.message || 'Failed to load expense. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle form submission
   */
  const onSubmit = async (data) => {
    setSaving(true);
    setError('');

    try {
      // Prepare expense data
      const expenseData = {
        categoryId: parseInt(data.categoryId, 10),
        amount: parseFloat(data.amount),
        expenseDate: data.expenseDate,
        description: data.description.trim(),
      };

      // Create or update expense
      let expenseId;
      if (isEditMode) {
        await expenseService.updateExpense(id, expenseData);
        expenseId = id;
      } else {
        const createdExpense = await expenseService.createExpense(expenseData);
        expenseId = createdExpense.id;
      }

      // Upload receipt file if a new one was selected
      if (receiptFile && expenseId) {
        setUploadingReceipt(true);
        try {
          await receiptService.uploadReceipt(expenseId, receiptFile);
        } catch (err) {
          console.error('Failed to upload receipt:', err);
          // Don't fail the entire operation if receipt upload fails
          // The expense was already created/updated
          setError('Expense saved, but receipt upload failed. You can try uploading it again by editing the expense.');
        } finally {
          setUploadingReceipt(false);
        }
      }

      // Navigate back to expense list
      navigate('/expenses');
    } catch (err) {
      console.error('Failed to save expense:', err);
      setError(err.message || 'Failed to save expense. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  /**
   * Handle cancel button
   */
  const handleCancel = () => {
    navigate('/expenses');
  };

  /**
   * Handle receipt file change
   */
  const handleReceiptChange = (file) => {
    setReceiptFile(file);
  };

  /**
   * Validate amount is positive
   */
  const validateAmount = (value) => {
    const num = parseFloat(value);
    if (isNaN(num) || num <= 0) {
      return 'Amount must be a positive number';
    }
    return true;
  };

  /**
   * Validate date is not in the future
   */
  const validateDate = (value) => {
    const selectedDate = new Date(value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate > today) {
      return 'Expense date cannot be in the future';
    }
    return true;
  };

  if (loading) {
    return <LoadingSpinner message="Loading expense..." />;
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4">
          {isEditMode ? 'Edit Expense' : 'Create New Expense'}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          {isEditMode
            ? 'Update the expense details below'
            : 'Fill in the details to create a new expense'}
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={3}>
            {/* Category */}
            <Grid item xs={12} sm={6}>
              <Controller
                name="categoryId"
                control={control}
                rules={{ required: 'Category is required' }}
                render={({ field }) => (
                  <CategorySelect
                    value={field.value}
                    onChange={field.onChange}
                    error={Boolean(errors.categoryId)}
                    helperText={errors.categoryId?.message}
                    disabled={saving}
                  />
                )}
              />
            </Grid>

            {/* Amount */}
            <Grid item xs={12} sm={6}>
              <Controller
                name="amount"
                control={control}
                rules={{
                  required: 'Amount is required',
                  validate: validateAmount,
                }}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Amount"
                    type="number"
                    inputProps={{
                      step: '0.01',
                      min: '0.01',
                    }}
                    error={Boolean(errors.amount)}
                    helperText={errors.amount?.message}
                    disabled={saving}
                    required
                  />
                )}
              />
            </Grid>

            {/* Expense Date */}
            <Grid item xs={12} sm={6}>
              <Controller
                name="expenseDate"
                control={control}
                rules={{
                  required: 'Expense date is required',
                  validate: validateDate,
                }}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Expense Date"
                    type="date"
                    InputLabelProps={{
                      shrink: true,
                    }}
                    error={Boolean(errors.expenseDate)}
                    helperText={errors.expenseDate?.message}
                    disabled={saving}
                    required
                  />
                )}
              />
            </Grid>

            {/* Description */}
            <Grid item xs={12}>
              <Controller
                name="description"
                control={control}
                rules={{
                  required: 'Description is required',
                  minLength: {
                    value: 3,
                    message: 'Description must be at least 3 characters',
                  },
                  maxLength: {
                    value: 500,
                    message: 'Description must not exceed 500 characters',
                  },
                }}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Description"
                    multiline
                    rows={4}
                    error={Boolean(errors.description)}
                    helperText={errors.description?.message}
                    disabled={saving}
                    required
                  />
                )}
              />
            </Grid>

            {/* Receipt Upload */}
            <Grid item xs={12}>
              <Typography variant="subtitle2" gutterBottom>
                Receipt (Optional)
              </Typography>
              {existingReceipt && !receiptFile && (
                <Alert severity="info" sx={{ mb: 2 }}>
                  Current receipt: {existingReceipt.fileName}
                  {' '}
                  (Upload a new file to replace it)
                </Alert>
              )}
              <FileUpload
                value={receiptFile}
                onChange={handleReceiptChange}
                disabled={saving}
              />
              {uploadingReceipt && (
                <Box sx={{ mt: 2 }}>
                  <LoadingSpinner message="Uploading receipt..." />
                </Box>
              )}
            </Grid>

            {/* Action Buttons */}
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button
                  variant="outlined"
                  startIcon={<CancelIcon />}
                  onClick={handleCancel}
                  disabled={saving}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={<SaveIcon />}
                  disabled={saving || uploadingReceipt}
                >
                  {saving ? 'Saving...' : isEditMode ? 'Update Expense' : 'Create Expense'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Container>
  );
};

export default ExpenseFormPage;
