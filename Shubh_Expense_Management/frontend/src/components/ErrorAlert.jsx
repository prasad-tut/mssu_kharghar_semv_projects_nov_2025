import { Alert, AlertTitle, Collapse, IconButton } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { useState } from 'react';

/**
 * ErrorAlert Component
 * Displays error messages with optional title and close button
 * Responsive and accessible error display
 * 
 * @param {Object} props
 * @param {string|Object} props.error - Error message or error object
 * @param {string} props.title - Optional title for the alert
 * @param {string} props.severity - Alert severity (error, warning, info, success)
 * @param {Function} props.onClose - Optional callback when alert is closed
 * @param {boolean} props.dismissible - Whether the alert can be dismissed
 */
const ErrorAlert = ({ 
  error, 
  title, 
  severity = 'error', 
  onClose, 
  dismissible = true 
}) => {
  const [open, setOpen] = useState(true);

  if (!error) {
    return null;
  }

  const handleClose = () => {
    setOpen(false);
    if (onClose) {
      onClose();
    }
  };

  // Extract error message from various error formats
  const getErrorMessage = (err) => {
    if (typeof err === 'string') {
      return err;
    }
    
    if (err?.response?.data?.message) {
      return err.response.data.message;
    }
    
    if (err?.response?.data?.error) {
      return err.response.data.error;
    }
    
    if (err?.message) {
      return err.message;
    }
    
    return 'An unexpected error occurred. Please try again.';
  };

  const errorMessage = getErrorMessage(error);

  return (
    <Collapse in={open}>
      <Alert
        severity={severity}
        action={
          dismissible && (
            <IconButton
              aria-label="close"
              color="inherit"
              size="small"
              onClick={handleClose}
              sx={{
                padding: { xs: 1, sm: 0.5 },
              }}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          )
        }
        sx={{ 
          mb: 2,
          fontSize: { xs: '0.875rem', sm: '1rem' },
          '& .MuiAlert-message': {
            width: '100%',
            wordBreak: 'break-word',
          },
        }}
        role="alert"
      >
        {title && <AlertTitle sx={{ fontSize: { xs: '1rem', sm: '1.125rem' } }}>{title}</AlertTitle>}
        {errorMessage}
      </Alert>
    </Collapse>
  );
};

export default ErrorAlert;
