import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Box, 
  IconButton, 
  Menu, 
  MenuItem, 
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  useMediaQuery,
  useTheme
} from '@mui/material';
import { 
  Menu as MenuIcon, 
  AccountCircle,
  Dashboard as DashboardIcon,
  Receipt as ReceiptIcon,
  Assessment as ReportIcon,
  CheckCircle as ApprovalIcon,
  Close as CloseIcon
} from '@mui/icons-material';
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Navbar Component
 * Displays navigation links and user menu
 * Adapts based on authentication state and user role
 * Includes mobile drawer menu for small screens
 */
const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  const [anchorEl, setAnchorEl] = useState(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleMobileMenuToggle = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const handleMobileMenuClose = () => {
    setMobileMenuOpen(false);
  };

  const handleLogout = () => {
    handleMenuClose();
    handleMobileMenuClose();
    logout();
    navigate('/login');
  };

  const handleNavigation = (path) => {
    navigate(path);
    handleMobileMenuClose();
  };

  // Don't show navbar on login/register pages
  if (location.pathname === '/login' || location.pathname === '/register') {
    return null;
  }

  if (!isAuthenticated) {
    return null;
  }

  const isManager = user?.role === 'MANAGER' || user?.role === 'ADMIN';

  // Navigation items for mobile drawer
  const navigationItems = [
    { label: 'Dashboard', path: '/dashboard', icon: <DashboardIcon /> },
    { label: 'Expenses', path: '/expenses', icon: <ReceiptIcon /> },
    { label: 'Reports', path: '/reports', icon: <ReportIcon /> },
  ];

  if (isManager) {
    navigationItems.push({ label: 'Approvals', path: '/approvals', icon: <ApprovalIcon /> });
  }

  return (
    <>
      <AppBar position="sticky" className="no-print">
        <Toolbar>
          {/* Mobile menu button */}
          {isMobile && (
            <IconButton
              color="inherit"
              aria-label="open menu"
              edge="start"
              onClick={handleMobileMenuToggle}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
          )}

          {/* Logo/Title */}
          <Typography
            variant="h6"
            component="div"
            sx={{ 
              flexGrow: { xs: 1, md: 0 }, 
              mr: { md: 4 }, 
              cursor: 'pointer',
              fontSize: { xs: '1rem', sm: '1.25rem' }
            }}
            onClick={() => handleNavigation('/dashboard')}
          >
            Expense Manager
          </Typography>

          {/* Desktop navigation */}
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            <Button
              color="inherit"
              onClick={() => handleNavigation('/dashboard')}
              sx={{ mx: 1 }}
            >
              Dashboard
            </Button>
            <Button
              color="inherit"
              onClick={() => handleNavigation('/expenses')}
              sx={{ mx: 1 }}
            >
              Expenses
            </Button>
            <Button
              color="inherit"
              onClick={() => handleNavigation('/reports')}
              sx={{ mx: 1 }}
            >
              Reports
            </Button>
            {isManager && (
              <Button
                color="inherit"
                onClick={() => handleNavigation('/approvals')}
                sx={{ mx: 1 }}
              >
                Approvals
              </Button>
            )}
          </Box>

          {/* User menu */}
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography 
              variant="body2" 
              sx={{ mr: 2, display: { xs: 'none', sm: 'block' } }}
            >
              {user?.firstName} {user?.lastName}
            </Typography>
            <IconButton
              size="large"
              aria-label="account menu"
              aria-controls="user-menu"
              aria-haspopup="true"
              onClick={handleMenuOpen}
              color="inherit"
            >
              <AccountCircle />
            </IconButton>
            <Menu
              id="user-menu"
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
            >
              <MenuItem disabled>
                <Typography variant="body2" color="text.secondary">
                  {user?.email}
                </Typography>
              </MenuItem>
              <MenuItem disabled>
                <Typography variant="caption" color="text.secondary">
                  Role: {user?.role}
                </Typography>
              </MenuItem>
              <Divider />
              <MenuItem onClick={handleLogout}>Logout</MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Mobile drawer menu */}
      <Drawer
        anchor="left"
        open={mobileMenuOpen}
        onClose={handleMobileMenuClose}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { 
            width: 280,
            boxSizing: 'border-box'
          },
        }}
      >
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" component="div">
            Menu
          </Typography>
          <IconButton onClick={handleMobileMenuClose}>
            <CloseIcon />
          </IconButton>
        </Box>
        <Divider />
        
        {/* User info in mobile menu */}
        <Box sx={{ p: 2, bgcolor: 'primary.main', color: 'white' }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 500 }}>
            {user?.firstName} {user?.lastName}
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.9 }}>
            {user?.email}
          </Typography>
          <Typography variant="caption" sx={{ opacity: 0.8 }}>
            Role: {user?.role}
          </Typography>
        </Box>
        <Divider />

        {/* Navigation items */}
        <List>
          {navigationItems.map((item) => (
            <ListItem key={item.path} disablePadding>
              <ListItemButton 
                onClick={() => handleNavigation(item.path)}
                selected={location.pathname === item.path}
              >
                <ListItemIcon>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
        
        {/* Logout button */}
        <List>
          <ListItem disablePadding>
            <ListItemButton onClick={handleLogout}>
              <ListItemText primary="Logout" />
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>
    </>
  );
};

export default Navbar;
