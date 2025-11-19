/**
 * Responsive Utility Functions
 * Helper functions for responsive design and breakpoint management
 */

/**
 * Material-UI breakpoint values
 */
export const breakpoints = {
  xs: 0,
  sm: 600,
  md: 900,
  lg: 1200,
  xl: 1536,
};

/**
 * Check if current viewport is mobile size
 * @param {number} width - Current viewport width
 * @returns {boolean} True if mobile size
 */
export const isMobile = (width = window.innerWidth) => {
  return width < breakpoints.sm;
};

/**
 * Check if current viewport is tablet size
 * @param {number} width - Current viewport width
 * @returns {boolean} True if tablet size
 */
export const isTablet = (width = window.innerWidth) => {
  return width >= breakpoints.sm && width < breakpoints.md;
};

/**
 * Check if current viewport is desktop size
 * @param {number} width - Current viewport width
 * @returns {boolean} True if desktop size
 */
export const isDesktop = (width = window.innerWidth) => {
  return width >= breakpoints.md;
};

/**
 * Get current breakpoint name
 * @param {number} width - Current viewport width
 * @returns {string} Breakpoint name (xs, sm, md, lg, xl)
 */
export const getCurrentBreakpoint = (width = window.innerWidth) => {
  if (width < breakpoints.sm) return 'xs';
  if (width < breakpoints.md) return 'sm';
  if (width < breakpoints.lg) return 'md';
  if (width < breakpoints.xl) return 'lg';
  return 'xl';
};

/**
 * Format currency for display
 * @param {number} amount - Amount to format
 * @param {string} currency - Currency code (default: USD)
 * @param {string} locale - Locale for formatting (default: en-US)
 * @returns {string} Formatted currency string
 */
export const formatCurrency = (amount, currency = 'USD', locale = 'en-US') => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
};

/**
 * Format date for display
 * @param {string|Date} date - Date to format
 * @param {string} locale - Locale for formatting (default: en-US)
 * @param {object} options - Intl.DateTimeFormat options
 * @returns {string} Formatted date string
 */
export const formatDate = (date, locale = 'en-US', options = {}) => {
  if (!date) return 'N/A';
  
  const defaultOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    ...options,
  };
  
  return new Date(date).toLocaleDateString(locale, defaultOptions);
};

/**
 * Format date and time for display
 * @param {string|Date} date - Date to format
 * @param {string} locale - Locale for formatting (default: en-US)
 * @returns {string} Formatted date and time string
 */
export const formatDateTime = (date, locale = 'en-US') => {
  if (!date) return 'N/A';
  
  return new Date(date).toLocaleString(locale, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Truncate text with ellipsis
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length before truncation
 * @returns {string} Truncated text
 */
export const truncateText = (text, maxLength = 50) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return `${text.substring(0, maxLength)}...`;
};

/**
 * Get responsive table columns based on viewport
 * @param {Array} allColumns - All available columns
 * @param {number} width - Current viewport width
 * @returns {Array} Filtered columns for current viewport
 */
export const getResponsiveColumns = (allColumns, width = window.innerWidth) => {
  const breakpoint = getCurrentBreakpoint(width);
  
  return allColumns.filter(column => {
    if (!column.hideOn) return true;
    return !column.hideOn.includes(breakpoint);
  });
};

/**
 * Debounce function for performance optimization
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in milliseconds
 * @returns {Function} Debounced function
 */
export const debounce = (func, wait = 300) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

/**
 * Throttle function for performance optimization
 * @param {Function} func - Function to throttle
 * @param {number} limit - Time limit in milliseconds
 * @returns {Function} Throttled function
 */
export const throttle = (func, limit = 300) => {
  let inThrottle;
  return function executedFunction(...args) {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
};

/**
 * Check if device supports touch
 * @returns {boolean} True if touch is supported
 */
export const isTouchDevice = () => {
  return (
    'ontouchstart' in window ||
    navigator.maxTouchPoints > 0 ||
    navigator.msMaxTouchPoints > 0
  );
};

/**
 * Get optimal image size based on viewport
 * @param {number} width - Current viewport width
 * @returns {string} Image size identifier (small, medium, large)
 */
export const getOptimalImageSize = (width = window.innerWidth) => {
  if (width < breakpoints.sm) return 'small';
  if (width < breakpoints.lg) return 'medium';
  return 'large';
};

/**
 * Calculate responsive font size
 * @param {number} baseSize - Base font size in pixels
 * @param {number} width - Current viewport width
 * @returns {number} Calculated font size
 */
export const getResponsiveFontSize = (baseSize, width = window.innerWidth) => {
  const breakpoint = getCurrentBreakpoint(width);
  const scaleFactor = {
    xs: 0.875,
    sm: 0.9375,
    md: 1,
    lg: 1.0625,
    xl: 1.125,
  };
  
  return baseSize * (scaleFactor[breakpoint] || 1);
};

/**
 * Get responsive spacing value
 * @param {number} baseSpacing - Base spacing value (8px units)
 * @param {number} width - Current viewport width
 * @returns {number} Calculated spacing in pixels
 */
export const getResponsiveSpacing = (baseSpacing, width = window.innerWidth) => {
  const breakpoint = getCurrentBreakpoint(width);
  const multiplier = {
    xs: 1,
    sm: 1.25,
    md: 1.5,
    lg: 1.75,
    xl: 2,
  };
  
  return baseSpacing * 8 * (multiplier[breakpoint] || 1);
};

/**
 * Check if user prefers reduced motion
 * @returns {boolean} True if reduced motion is preferred
 */
export const prefersReducedMotion = () => {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

/**
 * Check if user prefers dark mode
 * @returns {boolean} True if dark mode is preferred
 */
export const prefersDarkMode = () => {
  return window.matchMedia('(prefers-color-scheme: dark)').matches;
};

/**
 * Get safe area insets for notched devices
 * @returns {object} Safe area insets
 */
export const getSafeAreaInsets = () => {
  const style = getComputedStyle(document.documentElement);
  return {
    top: parseInt(style.getPropertyValue('env(safe-area-inset-top)') || '0'),
    right: parseInt(style.getPropertyValue('env(safe-area-inset-right)') || '0'),
    bottom: parseInt(style.getPropertyValue('env(safe-area-inset-bottom)') || '0'),
    left: parseInt(style.getPropertyValue('env(safe-area-inset-left)') || '0'),
  };
};

export default {
  breakpoints,
  isMobile,
  isTablet,
  isDesktop,
  getCurrentBreakpoint,
  formatCurrency,
  formatDate,
  formatDateTime,
  truncateText,
  getResponsiveColumns,
  debounce,
  throttle,
  isTouchDevice,
  getOptimalImageSize,
  getResponsiveFontSize,
  getResponsiveSpacing,
  prefersReducedMotion,
  prefersDarkMode,
  getSafeAreaInsets,
};
