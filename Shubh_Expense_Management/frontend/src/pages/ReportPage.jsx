import { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Paper,
  Grid,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
  Card,
  CardContent,
  Divider,
} from '@mui/material';
import {
  FileDownload as DownloadIcon,
  Assessment as AssessmentIcon,
  TrendingUp as TrendingUpIcon,
  Receipt as ReceiptIcon,
} from '@mui/icons-material';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js';
import { Pie, Bar } from 'react-chartjs-2';
import { LoadingSpinner, StatusBadge } from '../components';
import { reportService, categoryService } from '../services';

// Register Chart.js components
ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

/**
 * ReportPage Component
 * Displays expense reports with filtering and export functionality
 */
const ReportPage = () => {
  // State for filters
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    categoryId: '',
    status: '',
  });

  // State for report data
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // State for categories
  const [categories, setCategories] = useState([]);

  // State for export
  const [exporting, setExporting] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

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
   * Handle filter change
   */
  const handleFilterChange = (field, value) => {
    setFilters((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  /**
   * Generate report with current filters
   */
  const handleGenerateReport = async () => {
    setLoading(true);
    setError('');

    try {
      // Build params object with only non-empty filters
      const params = {};
      if (filters.startDate) params.startDate = filters.startDate;
      if (filters.endDate) params.endDate = filters.endDate;
      if (filters.categoryId) params.categoryId = filters.categoryId;
      if (filters.status) params.status = filters.status;

      const data = await reportService.getReportSummary(params);
      setReportData(data);
    } catch (err) {
      console.error('Failed to generate report:', err);
      setError(err.message || 'Failed to generate report. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Clear all filters and report data
   */
  const handleClearFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      categoryId: '',
      status: '',
    });
    setReportData(null);
    setError('');
  };

  /**
   * Export report as CSV
   */
  const handleExportCSV = async () => {
    await handleExport('csv');
  };

  /**
   * Export report as PDF
   */
  const handleExportPDF = async () => {
    await handleExport('pdf');
  };

  /**
   * Export report with specified format
   */
  const handleExport = async (format) => {
    setExporting(true);
    setError('');

    try {
      // Build params object with only non-empty filters
      const params = { format };
      if (filters.startDate) params.startDate = filters.startDate;
      if (filters.endDate) params.endDate = filters.endDate;
      if (filters.categoryId) params.categoryId = filters.categoryId;
      if (filters.status) params.status = filters.status;

      const blob = await reportService.exportReport(params);

      // Create download link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `expense-report-${new Date().toISOString().split('T')[0]}.${format}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to export report:', err);
      setError(err.message || 'Failed to export report. Please try again.');
    } finally {
      setExporting(false);
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
   * Calculate category breakdown from report data
   */
  const getCategoryBreakdown = () => {
    if (!reportData || !reportData.expenses) return {};

    const breakdown = {};
    reportData.expenses.forEach((expense) => {
      const categoryName = expense.category?.name || 'Uncategorized';
      if (!breakdown[categoryName]) {
        breakdown[categoryName] = {
          count: 0,
          amount: 0,
        };
      }
      breakdown[categoryName].count += 1;
      breakdown[categoryName].amount += expense.amount || 0;
    });

    return breakdown;
  };

  /**
   * Calculate status breakdown from report data
   */
  const getStatusBreakdown = () => {
    if (!reportData || !reportData.expenses) return {};

    const breakdown = {};
    reportData.expenses.forEach((expense) => {
      const status = expense.status || 'Unknown';
      if (!breakdown[status]) {
        breakdown[status] = {
          count: 0,
          amount: 0,
        };
      }
      breakdown[status].count += 1;
      breakdown[status].amount += expense.amount || 0;
    });

    return breakdown;
  };

  // Prepare chart data
  const categoryBreakdown = getCategoryBreakdown();
  const statusBreakdown = getStatusBreakdown();

  const categoryPieChartData = {
    labels: Object.keys(categoryBreakdown),
    datasets: [
      {
        label: 'Expense Amount',
        data: Object.values(categoryBreakdown).map((cat) => cat.amount),
        backgroundColor: [
          'rgba(255, 99, 132, 0.6)',
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)',
          'rgba(75, 192, 192, 0.6)',
          'rgba(153, 102, 255, 0.6)',
          'rgba(255, 159, 64, 0.6)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)',
        ],
        borderWidth: 1,
      },
    ],
  };

  const categoryBarChartData = {
    labels: Object.keys(categoryBreakdown),
    datasets: [
      {
        label: 'Total Amount',
        data: Object.values(categoryBreakdown).map((cat) => cat.amount),
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1,
      },
    ],
  };

  const statusBarChartData = {
    labels: Object.keys(statusBreakdown),
    datasets: [
      {
        label: 'Total Amount',
        data: Object.values(statusBreakdown).map((stat) => stat.amount),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        position: 'bottom',
      },
      tooltip: {
        callbacks: {
          label: function (context) {
            return `${context.label}: ${formatCurrency(context.parsed || context.parsed.y || 0)}`;
          },
        },
      },
    },
  };

  const barChartOptions = {
    ...chartOptions,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            return formatCurrency(value);
          },
        },
      },
    },
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <AssessmentIcon sx={{ fontSize: 40, mr: 2, color: 'primary.main' }} />
        <Typography variant="h4">
          Expense Reports
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {/* Filters Section */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Report Filters
        </Typography>
        <Grid container spacing={2} sx={{ mb: 2 }}>
          {/* Start Date Filter */}
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
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
              label="End Date"
              type="date"
              value={filters.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>

          {/* Category Filter */}
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>Category</InputLabel>
              <Select
                value={filters.categoryId}
                label="Category"
                onChange={(e) => handleFilterChange('categoryId', e.target.value)}
              >
                <MenuItem value="">All Categories</MenuItem>
                {categories.map((category) => (
                  <MenuItem key={category.id} value={category.id}>
                    {category.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Status Filter */}
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>Status</InputLabel>
              <Select
                value={filters.status}
                label="Status"
                onChange={(e) => handleFilterChange('status', e.target.value)}
              >
                <MenuItem value="">All Statuses</MenuItem>
                <MenuItem value="DRAFT">Draft</MenuItem>
                <MenuItem value="SUBMITTED">Submitted</MenuItem>
                <MenuItem value="APPROVED">Approved</MenuItem>
                <MenuItem value="REJECTED">Rejected</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        {/* Action Buttons */}
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleGenerateReport}
            disabled={loading}
          >
            {loading ? 'Generating...' : 'Generate Report'}
          </Button>
          <Button
            variant="outlined"
            onClick={handleClearFilters}
            disabled={loading}
          >
            Clear Filters
          </Button>
        </Box>
      </Paper>

      {/* Loading State */}
      {loading && <LoadingSpinner message="Generating report..." />}

      {/* Report Results */}
      {!loading && reportData && (
        <>
          {/* Summary Statistics */}
          <Grid container spacing={3} sx={{ mb: 3 }}>
            {/* Total Expenses Card */}
            <Grid item xs={12} sm={6} md={4}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <ReceiptIcon color="primary" sx={{ mr: 1 }} />
                    <Typography color="text.secondary" variant="body2">
                      Total Expenses
                    </Typography>
                  </Box>
                  <Typography variant="h4" component="div">
                    {reportData.count || 0}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Expense records
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            {/* Total Amount Card */}
            <Grid item xs={12} sm={6} md={4}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <TrendingUpIcon color="success" sx={{ mr: 1 }} />
                    <Typography color="text.secondary" variant="body2">
                      Total Amount
                    </Typography>
                  </Box>
                  <Typography variant="h4" component="div">
                    {formatCurrency(reportData.totalAmount || 0)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Sum of all expenses
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            {/* Average Amount Card */}
            <Grid item xs={12} sm={6} md={4}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <AssessmentIcon color="info" sx={{ mr: 1 }} />
                    <Typography color="text.secondary" variant="body2">
                      Average Amount
                    </Typography>
                  </Box>
                  <Typography variant="h4" component="div">
                    {formatCurrency(
                      reportData.count > 0 ? reportData.totalAmount / reportData.count : 0
                    )}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Per expense
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Export Buttons */}
          <Paper sx={{ p: 2, mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Typography variant="body1" sx={{ flexGrow: 1 }}>
                Export Report
              </Typography>
              <Button
                variant="contained"
                startIcon={<DownloadIcon />}
                onClick={handleExportCSV}
                disabled={exporting}
              >
                Export CSV
              </Button>
              <Button
                variant="contained"
                startIcon={<DownloadIcon />}
                onClick={handleExportPDF}
                disabled={exporting}
              >
                Export PDF
              </Button>
            </Box>
          </Paper>

          {/* Charts Section */}
          {reportData.expenses && reportData.expenses.length > 0 && (
            <Grid container spacing={3} sx={{ mb: 3 }}>
              {/* Category Pie Chart */}
              <Grid item xs={12} md={6}>
                <Paper sx={{ p: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Expenses by Category
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  {Object.keys(categoryBreakdown).length > 0 ? (
                    <Box sx={{ height: 300, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                      <Pie data={categoryPieChartData} options={chartOptions} />
                    </Box>
                  ) : (
                    <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                      No data available
                    </Typography>
                  )}
                </Paper>
              </Grid>

              {/* Category Bar Chart */}
              <Grid item xs={12} md={6}>
                <Paper sx={{ p: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Category Spending
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  {Object.keys(categoryBreakdown).length > 0 ? (
                    <Box sx={{ height: 300 }}>
                      <Bar data={categoryBarChartData} options={barChartOptions} />
                    </Box>
                  ) : (
                    <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                      No data available
                    </Typography>
                  )}
                </Paper>
              </Grid>

              {/* Status Bar Chart */}
              <Grid item xs={12}>
                <Paper sx={{ p: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Expenses by Status
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  {Object.keys(statusBreakdown).length > 0 ? (
                    <Box sx={{ height: 300 }}>
                      <Bar data={statusBarChartData} options={barChartOptions} />
                    </Box>
                  ) : (
                    <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                      No data available
                    </Typography>
                  )}
                </Paper>
              </Grid>
            </Grid>
          )}

          {/* Expense Details Table */}
          {reportData.expenses && reportData.expenses.length > 0 && (
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Expense Details
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Date</TableCell>
                      <TableCell>Description</TableCell>
                      <TableCell>Category</TableCell>
                      <TableCell align="right">Amount</TableCell>
                      <TableCell>Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {reportData.expenses.map((expense) => (
                      <TableRow key={expense.id} hover>
                        <TableCell>{formatDate(expense.expenseDate)}</TableCell>
                        <TableCell>{expense.description || 'No description'}</TableCell>
                        <TableCell>{expense.category?.name || 'N/A'}</TableCell>
                        <TableCell align="right">{formatCurrency(expense.amount)}</TableCell>
                        <TableCell>
                          <StatusBadge status={expense.status} size="small" />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          )}

          {/* No Data Message */}
          {reportData.expenses && reportData.expenses.length === 0 && (
            <Paper sx={{ p: 4 }}>
              <Typography variant="body1" color="text.secondary" align="center">
                No expenses found matching the selected filters.
              </Typography>
            </Paper>
          )}
        </>
      )}

      {/* Initial State Message */}
      {!loading && !reportData && (
        <Paper sx={{ p: 4 }}>
          <Typography variant="body1" color="text.secondary" align="center">
            Select filters and click "Generate Report" to view expense data.
          </Typography>
        </Paper>
      )}
    </Container>
  );
};

export default ReportPage;
