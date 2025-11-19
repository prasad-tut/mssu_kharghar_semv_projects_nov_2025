# Frontend Project Structure

## Overview
This document describes the organization of the Expense Management System frontend application.

## Directory Structure

```
frontend/
├── public/                      # Static assets
│   └── vite.svg
├── src/
│   ├── assets/                  # Images, fonts, and other assets
│   ├── components/              # Reusable UI components
│   │   └── .gitkeep
│   ├── context/                 # React Context providers
│   │   └── .gitkeep
│   ├── pages/                   # Page-level components
│   │   └── .gitkeep
│   ├── services/                # API service modules
│   │   ├── api.js              # Configured Axios instance
│   │   └── .gitkeep
│   ├── styles/                  # Global styles and themes
│   │   └── .gitkeep
│   ├── utils/                   # Utility functions and helpers
│   │   └── .gitkeep
│   ├── App.css                  # App component styles
│   ├── App.jsx                  # Main application component
│   ├── index.css                # Global styles
│   └── main.jsx                 # Application entry point
├── .env.development             # Development environment variables
├── .env.production              # Production environment variables
├── .gitignore
├── eslint.config.js             # ESLint configuration
├── index.html                   # HTML template
├── package.json                 # Dependencies and scripts
├── README.md                    # Project documentation
├── vite.config.js               # Vite configuration
└── PROJECT_STRUCTURE.md         # This file

```

## Key Files

### Configuration Files

- **vite.config.js**: Vite build tool configuration
- **eslint.config.js**: Code linting rules
- **.env.development**: Development API URL configuration
- **.env.production**: Production API URL configuration

### Source Files

- **src/main.jsx**: Application entry point, renders the root component
- **src/App.jsx**: Main application component with Router setup
- **src/services/api.js**: Axios instance with request/response interceptors

## Installed Dependencies

### Core Dependencies
- **react** (19.2.0): UI library
- **react-dom** (19.2.0): React DOM rendering
- **react-router-dom** (7.9.6): Client-side routing

### UI & Styling
- **@mui/material** (7.3.5): Material-UI component library
- **@mui/icons-material** (7.3.5): Material-UI icons
- **@emotion/react** (11.14.0): CSS-in-JS library (required by MUI)
- **@emotion/styled** (11.14.1): Styled components (required by MUI)

### Data & Forms
- **axios** (1.13.2): HTTP client for API requests
- **react-hook-form** (7.66.0): Form state management and validation

### Visualization
- **chart.js** (4.5.1): Charting library
- **react-chartjs-2** (5.3.1): React wrapper for Chart.js

## Next Steps

The following components and features will be implemented in subsequent tasks:

1. **Authentication**
   - AuthContext for global auth state
   - authService for API calls
   - LoginPage and RegisterPage components

2. **Expense Management**
   - ExpenseListPage with filtering and sorting
   - ExpenseFormPage for create/edit
   - expenseService for CRUD operations

3. **UI Components**
   - Navbar with navigation
   - ExpenseCard/ExpenseTable for display
   - StatusBadge for expense status
   - LoadingSpinner and ErrorAlert

4. **Additional Features**
   - Receipt upload and management
   - Approval workflow for managers
   - Reporting with charts and export
   - Responsive design implementation
