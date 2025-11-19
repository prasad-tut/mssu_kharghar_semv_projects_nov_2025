import { useState, useEffect } from 'react';
import { useTheme } from '@mui/material/styles';
import { useMediaQuery } from '@mui/material';
import { debounce } from './responsive';

/**
 * Custom hook for responsive behavior
 * Provides viewport information and responsive utilities
 * 
 * @returns {object} Responsive state and utilities
 */
export const useResponsive = () => {
  const theme = useTheme();
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
  });

  // Media queries using Material-UI breakpoints
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.between('sm', 'md'));
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const isLargeDesktop = useMediaQuery(theme.breakpoints.up('lg'));
  const isXLarge = useMediaQuery(theme.breakpoints.up('xl'));

  useEffect(() => {
    // Debounced resize handler
    const handleResize = debounce(() => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    }, 150);

    window.addEventListener('resize', handleResize);
    
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return {
    windowSize,
    isMobile,
    isTablet,
    isDesktop,
    isLargeDesktop,
    isXLarge,
    breakpoint: {
      xs: !isMobile && !isTablet && !isDesktop,
      sm: isMobile,
      md: isTablet,
      lg: isDesktop && !isLargeDesktop,
      xl: isXLarge,
    },
  };
};

/**
 * Custom hook for detecting viewport visibility
 * Useful for lazy loading and performance optimization
 * 
 * @param {object} ref - React ref to the element
 * @param {object} options - IntersectionObserver options
 * @returns {boolean} True if element is visible
 */
export const useIntersectionObserver = (ref, options = {}) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const observer = new IntersectionObserver(([entry]) => {
      setIsVisible(entry.isIntersecting);
    }, options);

    const currentRef = ref.current;
    if (currentRef) {
      observer.observe(currentRef);
    }

    return () => {
      if (currentRef) {
        observer.unobserve(currentRef);
      }
    };
  }, [ref, options]);

  return isVisible;
};

/**
 * Custom hook for detecting scroll position
 * Useful for sticky headers and scroll-based animations
 * 
 * @returns {object} Scroll position and direction
 */
export const useScrollPosition = () => {
  const [scrollPosition, setScrollPosition] = useState({
    x: 0,
    y: 0,
    direction: 'down',
  });

  useEffect(() => {
    let lastScrollY = window.pageYOffset;

    const handleScroll = debounce(() => {
      const currentScrollY = window.pageYOffset;
      setScrollPosition({
        x: window.pageXOffset,
        y: currentScrollY,
        direction: currentScrollY > lastScrollY ? 'down' : 'up',
      });
      lastScrollY = currentScrollY;
    }, 100);

    window.addEventListener('scroll', handleScroll, { passive: true });

    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  return scrollPosition;
};

/**
 * Custom hook for detecting online/offline status
 * Useful for showing connection status to users
 * 
 * @returns {boolean} True if online
 */
export const useOnlineStatus = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return isOnline;
};

/**
 * Custom hook for detecting touch device
 * Useful for adjusting UI for touch vs mouse interaction
 * 
 * @returns {boolean} True if touch device
 */
export const useTouchDevice = () => {
  const [isTouch, setIsTouch] = useState(false);

  useEffect(() => {
    const checkTouch = () => {
      setIsTouch(
        'ontouchstart' in window ||
        navigator.maxTouchPoints > 0 ||
        navigator.msMaxTouchPoints > 0
      );
    };

    checkTouch();
  }, []);

  return isTouch;
};

export default useResponsive;
