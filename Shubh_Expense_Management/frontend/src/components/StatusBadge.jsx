import { Chip } from '@mui/material';
import {
  Edit as DraftIcon,
  Send as SendIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';

/**
 * StatusBadge Component
 * Displays expense status as a colored badge with icon
 * 
 * @param {Object} props
 * @param {string} props.status - Expense status (DRAFT, SUBMITTED, APPROVED, REJECTED)
 * @param {string} props.size - Size of the badge (small, medium)
 */
const StatusBadge = ({ status, size = 'medium' }) => {
  const getStatusConfig = (status) => {
    const statusUpper = status?.toUpperCase();
    
    switch (statusUpper) {
      case 'DRAFT':
        return {
          label: 'Draft',
          color: 'default',
          icon: <DraftIcon />,
        };
      case 'SUBMITTED':
        return {
          label: 'Submitted',
          color: 'info',
          icon: <SendIcon />,
        };
      case 'APPROVED':
        return {
          label: 'Approved',
          color: 'success',
          icon: <CheckCircleIcon />,
        };
      case 'REJECTED':
        return {
          label: 'Rejected',
          color: 'error',
          icon: <CancelIcon />,
        };
      default:
        return {
          label: status || 'Unknown',
          color: 'default',
          icon: null,
        };
    }
  };

  const config = getStatusConfig(status);

  return (
    <Chip
      label={config.label}
      color={config.color}
      icon={config.icon}
      size={size}
      sx={{ fontWeight: 500 }}
    />
  );
};

export default StatusBadge;
