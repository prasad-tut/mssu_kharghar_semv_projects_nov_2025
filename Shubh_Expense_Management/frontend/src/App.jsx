import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar, ProtectedRoute } from './components';
import { LoginPage, RegisterPage, DashboardPage, ExpenseListPage, ExpenseFormPage, ApprovalPage, ReportPage } from './pages';
import './App.css';
import './styles/responsive.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/expenses" 
            element={
              <ProtectedRoute>
                <ExpenseListPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/expenses/new" 
            element={
              <ProtectedRoute>
                <ExpenseFormPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/expenses/edit/:id" 
            element={
              <ProtectedRoute>
                <ExpenseFormPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/approvals" 
            element={
              <ProtectedRoute allowedRoles={['MANAGER', 'ADMIN']}>
                <ApprovalPage />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/reports" 
            element={
              <ProtectedRoute>
                <ReportPage />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
