/**
 * Bug Tracker System - JavaScript
 * Frontend functionality and utilities
 */

// Document ready function
document.addEventListener('DOMContentLoaded', function() {
    console.log('🐛 Bug Tracker System loaded');
    initializeEventListeners();
    initializeTooltips();
});

/**
 * Initialize event listeners
 */
function initializeEventListeners() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (alert) {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        }
    });

    // Form validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

/**
 * Initialize Bootstrap tooltips
 */
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Format date to YYYY-MM-DD
 * @param {Date} date - Date object
 * @returns {string} Formatted date
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * Get today's date in YYYY-MM-DD format
 * @returns {string} Today's date
 */
function getTodayDate() {
    return formatDate(new Date());
}

/**
 * Set maximum date on date inputs to today
 */
function setMaxDateToToday() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    dateInputs.forEach(input => {
        input.max = getTodayDate();
    });
}

/**
 * Show loading spinner
 * @param {string} message - Message to display
 */
function showLoadingSpinner(message = 'Loading...') {
    const spinner = document.createElement('div');
    spinner.className = 'spinner-border text-primary';
    spinner.role = 'status';
    spinner.innerHTML = `<span class="visually-hidden">${message}</span>`;
    document.body.appendChild(spinner);
}

/**
 * Hide loading spinner
 */
function hideLoadingSpinner() {
    const spinners = document.querySelectorAll('.spinner-border');
    spinners.forEach(spinner => spinner.remove());
}

/**
 * Validate bug form
 * @returns {boolean} True if valid
 */
function validateBugForm() {
    const description = document.getElementById('description');
    const priority = document.getElementById('priority');
    const severity = document.getElementById('severity');
    const detectedOn = document.getElementById('detectedOn') || document.getElementById('detected_on');

    if (!description || !description.value.trim()) {
        alert('Description is required!');
        return false;
    }

    if (!priority || !priority.value) {
        alert('Priority is required!');
        return false;
    }

    if (!severity || !severity.value) {
        alert('Severity is required!');
        return false;
    }

    if (!detectedOn || !detectedOn.value) {
        alert('Detected On date is required!');
        return false;
    }

    return true;
}

/**
 * Format priority value with emoji
 * @param {string} priority - Priority value
 * @returns {string} Formatted priority with emoji
 */
function formatPriority(priority) {
    const priorityMap = {
        'HIGH': '🔴 HIGH',
        'MEDIUM': '🟡 MEDIUM',
        'LOW': '🟢 LOW'
    };
    return priorityMap[priority] || priority;
}

/**
 * Format severity value with emoji
 * @param {string} severity - Severity value
 * @returns {string} Formatted severity with emoji
 */
function formatSeverity(severity) {
    const severityMap = {
        'CRITICAL': '⚡ CRITICAL',
        'MARGINAL': '⚠️ MARGINAL',
        'NEGLIGIBLE': '✓ NEGLIGIBLE'
    };
    return severityMap[severity] || severity;
}

/**
 * Format status value with emoji
 * @param {string} status - Status value
 * @returns {string} Formatted status with emoji
 */
function formatStatus(status) {
    const statusMap = {
        'OPEN': '🔴 OPEN',
        'IN_PROGRESS': '🟡 IN PROGRESS',
        'FIXED': '🟢 FIXED'
    };
    return statusMap[status] || status;
}

/**
 * Get status badge class
 * @param {string} status - Status value
 * @returns {string} Bootstrap badge class
 */
function getStatusBadgeClass(status) {
    const statusMap = {
        'OPEN': 'bg-danger',
        'IN_PROGRESS': 'bg-warning text-dark',
        'FIXED': 'bg-success'
    };
    return statusMap[status] || 'bg-secondary';
}

/**
 * Get priority badge class
 * @param {string} priority - Priority value
 * @returns {string} Bootstrap badge class
 */
function getPriorityBadgeClass(priority) {
    const priorityMap = {
        'HIGH': 'bg-danger',
        'MEDIUM': 'bg-warning text-dark',
        'LOW': 'bg-success'
    };
    return priorityMap[priority] || 'bg-secondary';
}

/**
 * Get severity badge class
 * @param {string} severity - Severity value
 * @returns {string} Bootstrap badge class
 */
function getSeverityBadgeClass(severity) {
    const severityMap = {
        'CRITICAL': 'bg-danger',
        'MARGINAL': 'bg-warning text-dark',
        'NEGLIGIBLE': 'bg-info'
    };
    return severityMap[severity] || 'bg-secondary';
}

/**
 * Fetch bugs from API and display
 */
async function fetchAndDisplayBugs() {
    try {
        const response = await fetch('/api/bugs');
        if (!response.ok) throw new Error('Failed to fetch bugs');
        
        const bugs = await response.json();
        console.log('Fetched bugs:', bugs);
        
        // Process bugs here
        return bugs;
    } catch (error) {
        console.error('Error fetching bugs:', error);
    }
}

/**
 * Fetch single bug by ID
 * @param {number} id - Bug ID
 */
async function fetchBugById(id) {
    try {
        const response = await fetch(`/api/bug/${id}`);
        if (!response.ok) throw new Error('Failed to fetch bug');
        
        const bug = await response.json();
        console.log('Fetched bug:', bug);
        
        return bug;
    } catch (error) {
        console.error('Error fetching bug:', error);
    }
}

/**
 * Fetch bugs by status
 * @param {string} status - Status filter
 */
async function fetchBugsByStatus(status) {
    try {
        const response = await fetch(`/api/bugs/status/${status}`);
        if (!response.ok) throw new Error('Failed to fetch bugs');
        
        const bugs = await response.json();
        console.log(`Fetched ${status} bugs:`, bugs);
        
        return bugs;
    } catch (error) {
        console.error('Error fetching bugs by status:', error);
    }
}

/**
 * Show success message
 * @param {string} message - Message text
 */
function showSuccessMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        <i class="fas fa-check-circle"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);
    
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 5000);
}

/**
 * Show error message
 * @param {string} message - Message text
 */
function showErrorMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        <i class="fas fa-exclamation-circle"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.querySelector('main').insertAdjacentElement('afterbegin', alertDiv);
    
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 5000);
}

/**
 * Truncate string
 * @param {string} str - String to truncate
 * @param {number} length - Max length
 * @returns {string} Truncated string
 */
function truncateString(str, length = 50) {
    if (!str) return '';
    return str.length > length ? str.substring(0, length) + '...' : str;
}

/**
 * Log application information
 */
function logApplicationInfo() {
    console.log('%c🐛 Bug Tracker System', 'font-size: 16px; font-weight: bold; color: #0d6efd;');
    console.log('Version: 1.0.0');
    console.log('Framework: Spring Boot 3.2.0');
    console.log('Database: AWS RDS MySQL');
    console.log('Frontend: Bootstrap 5 + Thymeleaf');
}

// Log on page load
logApplicationInfo();
