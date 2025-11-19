import { useState, useRef } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  IconButton,
  Alert,
} from '@mui/material';
import {
  CloudUpload as UploadIcon,
  Delete as DeleteIcon,
  InsertDriveFile as FileIcon,
} from '@mui/icons-material';

/**
 * FileUpload Component
 * Component for uploading receipt files with validation
 */
const FileUpload = ({ value, onChange, error, helperText, disabled = false }) => {
  const [dragActive, setDragActive] = useState(false);
  const [validationError, setValidationError] = useState('');
  const fileInputRef = useRef(null);

  // Allowed file types
  const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
  const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

  /**
   * Validate file type and size
   */
  const validateFile = (file) => {
    if (!file) return { valid: false, error: 'No file selected' };

    // Check file type
    if (!ALLOWED_TYPES.includes(file.type)) {
      return {
        valid: false,
        error: 'Invalid file type. Please upload JPEG, PNG, or PDF files only.',
      };
    }

    // Check file size
    if (file.size > MAX_FILE_SIZE) {
      return {
        valid: false,
        error: 'File size exceeds 5MB limit. Please choose a smaller file.',
      };
    }

    return { valid: true };
  };

  /**
   * Handle file selection
   */
  const handleFileChange = (file) => {
    if (!file) return;

    const validation = validateFile(file);
    if (!validation.valid) {
      setValidationError(validation.error);
      return;
    }

    setValidationError('');
    onChange(file);
  };

  /**
   * Handle file input change
   */
  const handleInputChange = (e) => {
    const file = e.target.files?.[0];
    handleFileChange(file);
  };

  /**
   * Handle drag events
   */
  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  /**
   * Handle drop event
   */
  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (disabled) return;

    const file = e.dataTransfer.files?.[0];
    handleFileChange(file);
  };

  /**
   * Handle click to open file dialog
   */
  const handleClick = () => {
    if (!disabled) {
      fileInputRef.current?.click();
    }
  };

  /**
   * Handle file removal
   */
  const handleRemove = () => {
    setValidationError('');
    onChange(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  /**
   * Format file size for display
   */
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  const displayError = validationError || helperText;

  return (
    <Box>
      <input
        ref={fileInputRef}
        type="file"
        accept=".jpg,.jpeg,.png,.pdf"
        onChange={handleInputChange}
        style={{ display: 'none' }}
        disabled={disabled}
      />

      {!value ? (
        <Paper
          variant="outlined"
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
          onClick={handleClick}
          sx={{
            p: 3,
            textAlign: 'center',
            cursor: disabled ? 'not-allowed' : 'pointer',
            backgroundColor: dragActive ? 'action.hover' : 'background.paper',
            borderColor: error || validationError ? 'error.main' : 'divider',
            borderStyle: dragActive ? 'solid' : 'dashed',
            borderWidth: 2,
            opacity: disabled ? 0.6 : 1,
            '&:hover': {
              backgroundColor: disabled ? 'background.paper' : 'action.hover',
            },
          }}
        >
          <UploadIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 1 }} />
          <Typography variant="body1" gutterBottom>
            {dragActive ? 'Drop file here' : 'Click to upload or drag and drop'}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            JPEG, PNG, or PDF (max 5MB)
          </Typography>
        </Paper>
      ) : (
        <Paper
          variant="outlined"
          sx={{
            p: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <FileIcon color="primary" />
            <Box>
              <Typography variant="body2" fontWeight="medium">
                {value.name}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {formatFileSize(value.size)}
              </Typography>
            </Box>
          </Box>
          <IconButton
            onClick={handleRemove}
            color="error"
            size="small"
            disabled={disabled}
          >
            <DeleteIcon />
          </IconButton>
        </Paper>
      )}

      {displayError && (
        <Alert severity="error" sx={{ mt: 1 }}>
          {displayError}
        </Alert>
      )}
    </Box>
  );
};

export default FileUpload;
