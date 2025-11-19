import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  HourglassEmpty as PendingIcon,
  CheckCircle as ApprovedIcon,
  Receipt as ReceiptIcon,
} from '@mui/icons-material';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js';
import { Pie, Bar } from 'react-chartjs-2';
import { LoadingSpinner, StatusBadge } from '../components';
import { expenseService } from '../services';

// Register Chart.js components
ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

/**
 * DashboardPage Component
 * Displays summary statistics, recent expenses, and expense breakdown charts
 */
const DashboardPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expenses, setExpenses] = useState([]);
  const [statistics, setStatistics] = useState({
    total: 0,
    totalAmount: 0,
    pending: 0,
    pendingAmount: 0,
    approved: 0,
    approvedAmount: 0,
  });
  const [categoryBreakdown, setCategoryBreakdown] = useState({});

  useEffect(() => {
    fetchDashboardData();
  }, []);

  /**
   * Fetch all dashboard data including expenses and statistics
   */
  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');

    try {
      // Fetch all expenses (paginated)
      const response = await expenseService.getAllExpenses({
        page: 0,
        size: 100,
        sort: 'expenseDate,desc',
      });

      const allExpenses = response.content || [];
      setExpenses(allExpenses.slice(0, 10)); // Keep only recent 10 for display

      // Calculate statistics
      const stats = calculateStatistics(allExpenses);
      setStatistics(stats);

      // Calculate category breakdown
      const breakdown = calculateCategoryBreakdown(allExpenses);
      setCategoryBreakdown(breakdown);
    } catch (err) {
      console.error('Failed to fetch dashboard data:', err);
      setError(err.message || 'Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Calculate expense statistics
   * @param {Array} expenses - List of expenses
   * @returns {Object} Statistics object
   */
  const calculateStatistics = (expenses) => {
    const stats = {
      total: expenses.length,
      totalAmount: 0,
      pending: 0,
      pendingAmount: 0,
      approved: 0,
      approvedAmount: 0,
    };

    expenses.forEach((expense) => {
      stats.totalAmount += expense.amount || 0;

      if (expense.status === 'SUBMITTED') {
        stats.pending += 1;
        stats.pendingAmount += expense.amount || 0;
      } else if (expense.status === 'APPROVED') {
        stats.approved += 1;
        stats.approvedAmount += expense.amount || 0;
      }
    });

    return stats;
  };

  /**
   * Calculate expense breakdown by category
   * @param {Array} expenses - List of expenses
   * @returns {Object} Category breakdown object
   */
  const calculateCategoryBreakdown = (expenses) => {
    const breakdown = {};

    expenses.forEach((expense) => {
      const categoryName = expense.categoryName || 'Uncategorized';
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
   * Format currency value
   * @param {number} amount - Amount to format
   * @returns {string} Formatted currency string
   */
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  /**
   * Format date value
   * @param {string} dateString - Date string to format
   * @returns {string} Formatted date string
   */
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  // Prepare chart data for category breakdown (Pie chart)
  const pieChartData = {
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

  // Prepare chart data for category breakdown (Bar chart)
  const barChartData = {
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

  if (loading) {
    return <LoadingSpinner message="Loading dashboard..." />;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Summary Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {/* Total Expenses Card */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <ReceiptIcon color="primary" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Total Expenses
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {statistics.total}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formatCurrency(statistics.totalAmount)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Pending Expenses Card */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <PendingIcon color="info" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Pending Expenses
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {statistics.pending}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formatCurrency(statistics.pendingAmount)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Approved Expenses Card */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <ApprovedIcon color="success" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Approved Expenses
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {statistics.approved}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formatCurrency(statistics.approvedAmount)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Average Expense Card */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <TrendingUpIcon color="warning" sx={{ mr: 1 }} />
                <Typography color="text.secondary" variant="body2">
                  Average Expense
                </Typography>
              </Box>
              <Typography variant="h5" component="div">
                {formatCurrency(statistics.total > 0 ? statistics.totalAmount / statistics.total : 0)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Per expense
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {/* Pie Chart */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Expense Breakdown by Category
            </Typography>
            {Object.keys(categoryBreakdown).length > 0 ? (
              <Box sx={{ height: 300, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <Pie data={pieChartData} options={chartOptions} />
              </Box>
            ) : (
              <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                No expense data available
              </Typography>
            )}
          </Paper>
        </Grid>

        {/* Bar Chart */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Category Spending
            </Typography>
            {Object.keys(categoryBreakdown).length > 0 ? (
              <Box sx={{ height: 300 }}>
                <Bar data={barChartData} options={barChartOptions} />
              </Box>
            ) : (
              <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
                No expense data available
              </Typography>
            )}
          </Paper>
        </Grid>
      </Grid>

      {/* Recent Expenses Table */}
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Recent Expenses
        </Typography>
        {expenses.length > 0 ? (
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
                {expenses.map((expense) => (
                  <TableRow
                    key={expense.id}
                    hover
                    sx={{ cursor: 'pointer' }}
                    onClick={() => navigate(`/expenses/${expense.id}`)}
                  >
                    <TableCell>{formatDate(expense.expenseDate)}</TableCell>
                    <TableCell>{expense.description}</TableCell>
                    <TableCell>{expense.categoryName || 'N/A'}</TableCell>
                    <TableCell align="right">{formatCurrency(expense.amount)}</TableCell>
                    <TableCell>
                      <StatusBadge status={expense.status} size="small" />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        ) : (
          <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 4 }}>
            No expenses found. Create your first expense to get started!
          </Typography>
        )}
      </Paper>
    </Container>
  );
};

export default DashboardPage;
