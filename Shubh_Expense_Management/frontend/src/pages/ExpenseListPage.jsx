import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  IconButton,
  Button,
  TextField,
  MenuItem,
  Grid,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Tooltip,
  FormControl,
  InputLabel,
  Select,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Send as SendIcon,
  Add as AddIcon,
  FilterList as FilterIcon,
  AttachFile as AttachFileIcon,
  Visibility as VisibilityIcon,
  Download as DownloadIcon,
} from '@mui/icons-material';
import { LoadingSpinner, StatusBadge } from '../components';
import { expenseService, categoryService, receiptService } from '../services';

/**
 * ExpenseListPage Component
 * Displays all user expenses with filtering, sorting, and pagination
 */
const ExpenseListPage = () => {
  const navigate = useNavigate();
  
  // State for expenses and pagination
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  
  // State for filters
  const [filters, setFilters] = useState({
    status: '',
    categoryId: '',
    startDate: '',
    endDate: '',
  });
  const [showFilters, setShowFilters] = useState(false);
  
  // State for sorting
  const [sortField, setSortField] = useState('expenseDate');
  const [sortDirection, setSortDirection] = useState('desc');
  
  // State for categories
  const [categories, setCategories] = useState([]);
  
  // State for delete confirmation dialog
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [expenseToDelete, setExpenseToDelete] = useState(null);
  
  // State for receipt preview dialog
  const [receiptPreviewOpen, setReceiptPreviewOpen] = useState(false);
  const [currentReceipt, setCurrentReceipt] = useState(null);
  const [receiptBlob, setReceiptBlob] = useState(null);
  const [loadingReceipt, setLoadingReceipt] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchExpenses();
  }, [page, rowsPerPage, sortField, sortDirection, filters]);

  /**
   * Fetch all categories for filter dropdown
   */
  const fetchCategories = async () => {
    try {
      const data = await categoryService.getAllCategories();
      setCategories(data);
    } catch (err) {
      console.error('Failed to fetch categories:', err);
    }
  };

  /**
   * Fetch expenses with current filters, sorting, and pagination
   */
  const fetchExpenses = async () => {
    setLoading(true);
    setError('');

    try {
      const params = {
        page,
        size: rowsPerPage,
        sort: `${sortField},${sortDirection}`,
      };

      // Add filters if they are set
      if (filters.status) {
        params.status = filters.status;
      }
      if (filters.categoryId) {
        params.categoryId = filters.categoryId;
      }
      if (filters.startDate) {
        params.startDate = filters.startDate;
      }
      if (filters.endDate) {
        params.endDate = filters.endDate;
      }

      const response = await expenseService.getAllExpenses(params);
      setExpenses(response.content || []);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      console.error('Failed to fetch expenses:', err);
      setError(err.message || 'Failed to load expenses. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle page change
   */
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  /**
   * Handle rows per page change
   */
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  /**
   * Handle filter change
   */
  const handleFilterChange = (field, value) => {
    setFilters((prev) => ({
      ...prev,
      [field]: value,
    }));
    setPage(0); // Reset to first page when filters change
  };

  /**
   * Handle sort change
   */
  const handleSortChange = (field) => {
    if (sortField === field) {
      // Toggle direction if same field
      setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      // Set new field with default descending
      setSortField(field);
      setSortDirection('desc');
    }
    setPage(0); // Reset to first page when sort changes
  };

  /**
   * Clear all filters
   */
  const handleClearFilters = () => {
    setFilters({
      status: '',
      categoryId: '',
      startDate: '',
      endDate: '',
    });
    setPage(0);
  };

  /**
   * Handle edit expense
   */
  const handleEdit = (expenseId) => {
    navigate(`/expenses/edit/${expenseId}`);
  };

  /**
   * Handle delete expense - open confirmation dialog
   */
  const handleDeleteClick = (expense) => {
    setExpenseToDelete(expense);
    setDeleteDialogOpen(true);
  };

  /**
   * Confirm delete expense
   */
  const handleDeleteConfirm = async () => {
    if (!expenseToDelete) return;

    try {
      await expenseService.deleteExpense(expenseToDelete.id);
      setDeleteDialogOpen(false);
      setExpenseToDelete(null);
      fetchExpenses(); // Refresh the list
    } catch (err) {
      console.error('Failed to delete expense:', err);
      setError(err.message || 'Failed to delete expense. Please try again.');
      setDeleteDialogOpen(false);
    }
  };

  /**
   * Cancel delete
   */
  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setExpenseToDelete(null);
  };

  /**
   * Handle submit expense for approval
   */
  const handleSubmit = async (expenseId) => {
    try {
      await expenseService.submitExpense(expenseId);
      fetchExpenses(); // Refresh the list
    } catch (err) {
      console.error('Failed to submit expense:', err);
      setError(err.message || 'Failed to submit expense. Please try again.');
    }
  };

  /**
   * Format currency value
   */
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  /**
   * Format date value
   */
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  /**
   * Check if expense can be edited (only DRAFT status)
   */
  const canEdit = (status) => {
    return status === 'DRAFT';
  };

  /**
   * Check if expense can be deleted (only DRAFT status)
   */
  const canDelete = (status) => {
    return status === 'DRAFT';
  };

  /**
   * Check if expense can be submitted (only DRAFT status)
   */
  const canSubmit = (status) => {
    return status === 'DRAFT';
  };

  /**
   * Handle receipt preview
   */
  const handleReceiptPreview = async (receipt) => {
    if (!receipt) return;

    setCurrentReceipt(receipt);
    setReceiptPreviewOpen(true);
    setLoadingReceipt(true);

    try {
      const blob = await receiptService.downloadReceipt(receipt.id);
      setReceiptBlob(blob);
    } catch (err) {
      console.error('Failed to load receipt:', err);
      setError('Failed to load receipt. Please try again.');
      setReceiptPreviewOpen(false);
    } finally {
      setLoadingReceipt(false);
    }
  };

  /**
   * Handle receipt download
   */
  const handleReceiptDownload = async (receipt) => {
    if (!receipt) return;

    try {
      const blob = await receiptService.downloadReceipt(receipt.id);
      
      // Create a download link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = receipt.fileName || 'receipt';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to download receipt:', err);
      setError('Failed to download receipt. Please try again.');
    }
  };

  /**
   * Close receipt preview dialog
   */
  const handleCloseReceiptPreview = () => {
    setReceiptPreviewOpen(false);
    setCurrentReceipt(null);
    setReceiptBlob(null);
  };

  /**
   * Get receipt preview URL
   */
  const getReceiptPreviewUrl = () => {
    if (!receiptBlob) return null;
    return window.URL.createObjectURL(receiptBlob);
  };

  /**
   * Check if receipt is an image
   */
  const isImageReceipt = (receipt) => {
    if (!receipt) return false;
    const imageTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    return imageTypes.includes(receipt.fileType);
  };

  if (loading && expenses.length === 0) {
    return <LoadingSpinner message="Loading expenses..." />;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          My Expenses
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => navigate('/expenses/new')}
        >
          Create Expense
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {/* Filters Section */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: showFilters ? 2 : 0 }}>
          <Button
            startIcon={<FilterIcon />}
            onClick={() => setShowFilters(!showFilters)}
          >
            {showFilters ? 'Hide Filters' : 'Show Filters'}
          </Button>
          {showFilters && (
            <Button onClick={handleClearFilters} size="small">
              Clear Filters
            </Button>
          )}
        </Box>

        {showFilters && (
          <Grid container spacing={2}>
            {/* Status Filter */}
            <Grid item xs={12} sm={6} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Status</InputLabel>
                <Select
                  value={filters.status}
                  label="Status"
                  onChange={(e) => handleFilterChange('status', e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  <MenuItem value="DRAFT">Draft</MenuItem>
                  <MenuItem value="SUBMITTED">Submitted</MenuItem>
                  <MenuItem value="APPROVED">Approved</MenuItem>
                  <MenuItem value="REJECTED">Rejected</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            {/* Category Filter */}
            <Grid item xs={12} sm={6} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Category</InputLabel>
                <Select
                  value={filters.categoryId}
                  label="Category"
                  onChange={(e) => handleFilterChange('categoryId', e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  {categories.map((category) => (
                    <MenuItem key={category.id} value={category.id}>
                      {category.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {/* Start Date Filter */}
            <Grid item xs={12} sm={6} md={3}>
              <TextField
                fullWidth
                size="small"
                label="Start Date"
                type="date"
                value={filters.startDate}
                onChange={(e) => handleFilterChange('startDate', e.target.value)}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>

            {/* End Date Filter */}
            <Grid item xs={12} sm={6} md={3}>
              <TextField
                fullWidth
                size="small"
                label="End Date"
                type="date"
                value={filters.endDate}
                onChange={(e) => handleFilterChange('endDate', e.target.value)}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
          </Grid>
        )}
      </Paper>

      {/* Sorting Options */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Sort by:
          </Typography>
          <Button
            size="small"
            variant={sortField === 'expenseDate' ? 'contained' : 'outlined'}
            onClick={() => handleSortChange('expenseDate')}
          >
            Date {sortField === 'expenseDate' && (sortDirection === 'asc' ? '↑' : '↓')}
          </Button>
          <Button
            size="small"
            variant={sortField === 'amount' ? 'contained' : 'outlined'}
            onClick={() => handleSortChange('amount')}
          >
            Amount {sortField === 'amount' && (sortDirection === 'asc' ? '↑' : '↓')}
          </Button>
          <Button
            size="small"
            variant={sortField === 'status' ? 'contained' : 'outlined'}
            onClick={() => handleSortChange('status')}
          >
            Status {sortField === 'status' && (sortDirection === 'asc' ? '↑' : '↓')}
          </Button>
        </Box>
      </Paper>

      {/* Expenses Table */}
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Category</TableCell>
                <TableCell align="right">Amount</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="center">Receipt</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    <LoadingSpinner message="Loading..." />
                  </TableCell>
                </TableRow>
              ) : expenses.length > 0 ? (
                expenses.map((expense) => (
                  <TableRow key={expense.id} hover>
                    <TableCell>{formatDate(expense.expenseDate)}</TableCell>
                    <TableCell>{expense.description || 'No description'}</TableCell>
                    <TableCell>{expense.category?.name || 'N/A'}</TableCell>
                    <TableCell align="right">{formatCurrency(expense.amount)}</TableCell>
                    <TableCell>
                      <StatusBadge status={expense.status} size="small" />
                    </TableCell>
                    <TableCell align="center">
                      {expense.receipt ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                          <Tooltip title="Preview Receipt">
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={() => handleReceiptPreview(expense.receipt)}
                            >
                              <VisibilityIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Download Receipt">
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={() => handleReceiptDownload(expense.receipt)}
                            >
                              <DownloadIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </Box>
                      ) : (
                        <Typography variant="caption" color="text.secondary">
                          No receipt
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="center">
                      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
                        {canEdit(expense.status) && (
                          <Tooltip title="Edit">
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={() => handleEdit(expense.id)}
                            >
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                        {canDelete(expense.status) && (
                          <Tooltip title="Delete">
                            <IconButton
                              size="small"
                              color="error"
                              onClick={() => handleDeleteClick(expense)}
                            >
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                        {canSubmit(expense.status) && (
                          <Tooltip title="Submit for Approval">
                            <IconButton
                              size="small"
                              color="success"
                              onClick={() => handleSubmit(expense.id)}
                            >
                              <SendIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                      </Box>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    <Typography variant="body2" color="text.secondary" sx={{ py: 4 }}>
                      No expenses found. Create your first expense to get started!
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        <TablePagination
          rowsPerPageOptions={[5, 10, 25, 50]}
          component="div"
          count={totalElements}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteCancel}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this expense?
            {expenseToDelete && (
              <>
                <br />
                <br />
                <strong>Description:</strong> {expenseToDelete.description || 'No description'}
                <br />
                <strong>Amount:</strong> {formatCurrency(expenseToDelete.amount)}
                <br />
                <strong>Date:</strong> {formatDate(expenseToDelete.expenseDate)}
              </>
            )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} color="primary">
            Cancel
          </Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Receipt Preview Dialog */}
      <Dialog
        open={receiptPreviewOpen}
        onClose={handleCloseReceiptPreview}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Receipt Preview
          {currentReceipt && (
            <Typography variant="caption" display="block" color="text.secondary">
              {currentReceipt.fileName}
            </Typography>
          )}
        </DialogTitle>
        <DialogContent>
          {loadingReceipt ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <LoadingSpinner message="Loading receipt..." />
            </Box>
          ) : receiptBlob && currentReceipt ? (
            <Box sx={{ textAlign: 'center' }}>
              {isImageReceipt(currentReceipt) ? (
                <Box
                  component="img"
                  src={getReceiptPreviewUrl()}
                  alt="Receipt"
                  sx={{
                    maxWidth: '100%',
                    maxHeight: '70vh',
                    objectFit: 'contain',
                  }}
                />
              ) : (
                <Box sx={{ py: 4 }}>
                  <AttachFileIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="body1" gutterBottom>
                    PDF Preview
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {currentReceipt.fileName}
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<DownloadIcon />}
                    onClick={() => handleReceiptDownload(currentReceipt)}
                    sx={{ mt: 2 }}
                  >
                    Download PDF
                  </Button>
                </Box>
              )}
            </Box>
          ) : (
            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
              Failed to load receipt
            </Typography>
          )}
        </DialogContent>
        <DialogActions>
          {currentReceipt && receiptBlob && (
            <Button
              startIcon={<DownloadIcon />}
              onClick={() => handleReceiptDownload(currentReceipt)}
            >
              Download
            </Button>
          )}
          <Button onClick={handleCloseReceiptPreview} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ExpenseListPage;
