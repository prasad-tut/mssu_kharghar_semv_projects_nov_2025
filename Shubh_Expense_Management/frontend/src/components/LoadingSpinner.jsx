import { Box, CircularProgress, Typography } from '@mui/material';

/**
 * LoadingSpinner Component
 * Displays a centered loading indicator with optional message
 * Responsive and accessible loading state
 * 
 * @param {Object} props
 * @param {string} props.message - Optional loading message to display
 * @param {string} props.size - Size of the spinner (small, medium, large) or number
 * @param {boolean} props.fullScreen - Whether to display as full screen overlay
 */
const LoadingSpinner = ({ message = 'Loading...', size = 40, fullScreen = false }) => {
  const containerStyles = fullScreen
    ? {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        backdropFilter: 'blur(4px)',
        zIndex: 9999,
      }
    : {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: { xs: 2, sm: 3 },
        minHeight: { xs: 150, sm: 200 },
      };

  return (
    <Box sx={containerStyles} role="status" aria-live="polite" aria-label={message}>
      <CircularProgress 
        size={size} 
        sx={{
          color: 'primary.main',
        }}
      />
      {message && (
        <Typography 
          variant="body1" 
          sx={{ 
            mt: 2,
            fontSize: { xs: '0.875rem', sm: '1rem' },
            textAlign: 'center',
            px: 2,
          }} 
          color="text.secondary"
        >
          {message}
        </Typography>
      )}
    </Box>
  );
};

export default LoadingSpinner;
