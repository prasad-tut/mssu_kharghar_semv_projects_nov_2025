import { useState, useEffect } from 'react';
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
  Button,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
  Chip,
  IconButton,
  Tooltip,
  Card,
  CardContent,
  Grid,
} from '@mui/material';
import {
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
  Visibility as ViewIcon,
  AttachFile as AttachFileIcon,
  HourglassEmpty as PendingIcon,
} from '@mui/icons-material';
import { LoadingSpinner, StatusBadge } from '../components';
import { expenseService, receiptService } from '../services';

/**
 * ApprovalPage Component
 * Displays pending expenses for managers to approve or reject
 * Only accessible to users with MANAGER or ADMIN role
 */
const ApprovalPage = () => {
  // State for pending expenses
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // State for approval/rejection dialog
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogType, setDialogType] = useState(''); // 'approve' or 'reject'
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [reviewNotes, setReviewNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // State for expense details dialog
  const [detailsDialogOpen, setDetailsDialogOpen] = useState(false);
  const [expenseDetails, setExpenseDetails] = useState(null);

  useEffect(() => {
    fetchPendingExpenses();
  }, []);

  /**
   * Fetch all pending expenses
   */
  const fetchPendingExpenses = async () => {
    setLoading(true);
    setError('');

    try {
      const data = await expenseService.getPendingExpenses();
      setExpenses(data);
    } catch (err) {
      console.error('Failed to fetch pending expenses:', err);
      setError(err.message || 'Failed to load pending expenses. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Open approval dialog
   */
  const handleApproveClick = (expense) => {
    setSelectedExpense(expense);
    setDialogType('approve');
    setReviewNotes('');
    setDialogOpen(true);
  };

  /**
   * Open rejection dialog
   */
  const handleRejectClick = (expense) => {
    setSelectedExpense(expense);
    setDialogType('reject');
    setReviewNotes('');
    setDialogOpen(true);
  };

  /**
   * Close approval/rejection dialog
   */
  const handleDialogClose = () => {
    setDialogOpen(false);
    setSelectedExpense(null);
    setReviewNotes('');
    setDialogType('');
  };

  /**
   * Confirm approval or rejection
   */
  const handleConfirm = async () => {
    if (!selectedExpense) return;

    setSubmitting(true);
    setError('');

    try {
      const requestData = reviewNotes ? { reviewNotes } : {};

      if (dialogType === 'approve') {
        await expenseService.approveExpense(selectedExpense.id, requestData);
        setSuccessMessage(`Expense approved successfully!`);
      } else if (dialogType === 'reject') {
        await expenseService.rejectExpense(selectedExpense.id, requestData);
        setSuccessMessage(`Expense rejected successfully!`);
      }

      // Close dialog and refresh list
      handleDialogClose();
      fetchPendingExpenses();

      // Clear success message after 5 seconds
      setTimeout(() => setSuccessMessage(''), 5000);
    } catch (err) {
      console.error(`Failed to ${dialogType} expense:`, err);
      setError(err.message || `Failed to ${dialogType} expense. Please try again.`);
    } finally {
      setSubmitting(false);
    }
  };

  /**
   * View expense details
   */
  const handleViewDetails = (expense) => {
    setExpenseDetails(expense);
    setDetailsDialogOpen(true);
  };

  /**
   * Close details dialog
   */
  const handleDetailsDialogClose = () => {
    setDetailsDialogOpen(false);
    setExpenseDetails(null);
  };

  /**
   * Download receipt
   */
  const handleDownloadReceipt = async (receipt) => {
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
   * Format date and time value
   */
  const formatDateTime = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  /**
   * Calculate summary statistics
   */
  const calculateSummary = () => {
    const totalCount = expenses.length;
    const totalAmount = expenses.reduce((sum, expense) => sum + (expense.amount || 0), 0);
    return { totalCount, totalAmount };
  };

  const summary = calculateSummary();

  if (loading) {
    return <LoadingSpinner message="Loading pending expenses..." />;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" gutterBottom>
          Expense Approvals
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Review and approve or reject pending expense submissions
        </Typography>
      </Box>

      {/* Success Message */}
      {successMessage && (
        <Alert severity="success" sx={{ mb: 3 }} onClose={() => setSuccessMessage('')}>
          {successMessage}
        </Alert>
      )}

      {/* Error Message */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <PendingIcon color="info" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Pending Expenses
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {summary.totalCount}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Awaiting review
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <PendingIcon color="warning" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Total Amount
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {formatCurrency(summary.totalAmount)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Pending approval
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Pending Expenses Table */}
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Submitted</TableCell>
                <TableCell>Employee</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Category</TableCell>
                <TableCell align="right">Amount</TableCell>
                <TableCell align="center">Receipt</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {expenses.length > 0 ? (
                expenses.map((expense) => (
                  <TableRow key={expense.id} hover>
                    <TableCell>{formatDateTime(expense.submittedAt)}</TableCell>
                    <TableCell>
                      {expense.user ? `${expense.user.firstName} ${expense.user.lastName}` : 'N/A'}
                    </TableCell>
                    <TableCell>{formatDate(expense.expenseDate)}</TableCell>
                    <TableCell>{expense.description || 'No description'}</TableCell>
                    <TableCell>{expense.category?.name || 'N/A'}</TableCell>
                    <TableCell align="right">
                      <Typography variant="body2" fontWeight="medium">
                        {formatCurrency(expense.amount)}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      {expense.receipt ? (
                        <Tooltip title="Download Receipt">
                          <IconButton
                            size="small"
                            color="primary"
                            onClick={() => handleDownloadReceipt(expense.receipt)}
                          >
                            <AttachFileIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      ) : (
                        <Typography variant="caption" color="text.secondary">
                          No receipt
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="center">
                      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
                        <Tooltip title="View Details">
                          <IconButton
                            size="small"
                            color="info"
                            onClick={() => handleViewDetails(expense)}
                          >
                            <ViewIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Approve">
                          <IconButton
                            size="small"
                            color="success"
                            onClick={() => handleApproveClick(expense)}
                          >
                            <ApproveIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Reject">
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() => handleRejectClick(expense)}
                          >
                            <RejectIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={8} align="center">
                    <Typography variant="body2" color="text.secondary" sx={{ py: 4 }}>
                      No pending expenses to review. All caught up!
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      {/* Approval/Rejection Dialog */}
      <Dialog
        open={dialogOpen}
        onClose={handleDialogClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          {dialogType === 'approve' ? 'Approve Expense' : 'Reject Expense'}
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            {selectedExpense && (
              <>
                <strong>Employee:</strong>{' '}
                {selectedExpense.user
                  ? `${selectedExpense.user.firstName} ${selectedExpense.user.lastName}`
                  : 'N/A'}
                <br />
                <strong>Description:</strong> {selectedExpense.description || 'No description'}
                <br />
                <strong>Amount:</strong> {formatCurrency(selectedExpense.amount)}
                <br />
                <strong>Date:</strong> {formatDate(selectedExpense.expenseDate)}
                <br />
                <strong>Category:</strong> {selectedExpense.category?.name || 'N/A'}
              </>
            )}
          </DialogContentText>
          <TextField
            fullWidth
            multiline
            rows={4}
            label="Review Notes (Optional)"
            value={reviewNotes}
            onChange={(e) => setReviewNotes(e.target.value)}
            placeholder={
              dialogType === 'approve'
                ? 'Add any notes about this approval...'
                : 'Provide a reason for rejection...'
            }
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} disabled={submitting}>
            Cancel
          </Button>
          <Button
            onClick={handleConfirm}
            variant="contained"
            color={dialogType === 'approve' ? 'success' : 'error'}
            disabled={submitting}
            startIcon={dialogType === 'approve' ? <ApproveIcon /> : <RejectIcon />}
          >
            {submitting
              ? 'Processing...'
              : dialogType === 'approve'
              ? 'Approve'
              : 'Reject'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Expense Details Dialog */}
      <Dialog
        open={detailsDialogOpen}
        onClose={handleDetailsDialogClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Expense Details</DialogTitle>
        <DialogContent>
          {expenseDetails && (
            <Box sx={{ pt: 1 }}>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Employee
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {expenseDetails.user
                      ? `${expenseDetails.user.firstName} ${expenseDetails.user.lastName}`
                      : 'N/A'}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {expenseDetails.user?.email || 'N/A'}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Expense Date
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {formatDate(expenseDetails.expenseDate)}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Submitted At
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {formatDateTime(expenseDetails.submittedAt)}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Category
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {expenseDetails.category?.name || 'N/A'}
                  </Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Amount
                  </Typography>
                  <Typography variant="h6" color="primary" gutterBottom>
                    {formatCurrency(expenseDetails.amount)}
                  </Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Description
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    {expenseDetails.description || 'No description provided'}
                  </Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Status
                  </Typography>
                  <Box sx={{ mt: 1 }}>
                    <StatusBadge status={expenseDetails.status} />
                  </Box>
                </Grid>
                {expenseDetails.receipt && (
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Receipt
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Chip
                        icon={<AttachFileIcon />}
                        label={expenseDetails.receipt.fileName}
                        size="small"
                      />
                      <Button
                        size="small"
                        variant="outlined"
                        onClick={() => handleDownloadReceipt(expenseDetails.receipt)}
                      >
                        Download
                      </Button>
                    </Box>
                  </Grid>
                )}
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDetailsDialogClose}>Close</Button>
          {expenseDetails && (
            <>
              <Button
                variant="contained"
                color="success"
                startIcon={<ApproveIcon />}
                onClick={() => {
                  handleDetailsDialogClose();
                  handleApproveClick(expenseDetails);
                }}
              >
                Approve
              </Button>
              <Button
                variant="contained"
                color="error"
                startIcon={<RejectIcon />}
                onClick={() => {
                  handleDetailsDialogClose();
                  handleRejectClick(expenseDetails);
                }}
              >
                Reject
              </Button>
            </>
          )}
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ApprovalPage;
