// API Configuration from environment
const API_BASE = window.ENV?.API_BASE_URL || 'http://localhost:7890';
const API_URL = `${API_BASE}/bills`;

let allBills = [];
let editingBillId = null;
let deleteModal, toast;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeComponents();
    loadBills();
    setupFormSubmit();
    setDefaultDate();
});

// Initialize Bootstrap components
function initializeComponents() {
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    toast = new bootstrap.Toast(document.getElementById('toast'));
}

// Set default date to today
function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('billDate').value = today;
}

// Setup form submission
function setupFormSubmit() {
    const form = document.getElementById('billForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const billData = {
            biller: document.getElementById('biller').value.trim(),
            description: document.getElementById('description').value.trim(),
            amount: document.getElementById('amount').value,
            paymentMode: document.getElementById('paymentMode').value,
            paymentStatus: document.getElementById('paymentStatus').value
        };

        if (editingBillId) {
            billData.id = editingBillId;
            await updateBill(billData);
        } else {
            await addBill(billData);
        }
    });
}

// Load all bills
async function loadBills() {
    try {
        showLoading();
        const response = await fetch(`${API_URL}/get`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch bills');
        }
        
        allBills = await response.json();
        displayBills(allBills);
        updateDashboard(allBills);
        hideLoading();
    } catch (error) {
        console.error('Error loading bills:', error);
        showToast('Error', 'Failed to load bills. Please check if the backend is running.', 'danger');
        hideLoading();
    }
}

// Display bills in table
function displayBills(bills) {
    const tbody = document.getElementById('billsTableBody');
    
    if (bills.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center py-5">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <p class="mb-0">No bills found. Add your first bill to get started!</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = bills.map(bill => `
        <tr>
            <td>${bill.id}</td>
            <td>
                <strong>${bill.biller}</strong><br>
                <small class="text-muted">${bill.description}</small>
            </td>
            <td><strong>$${parseFloat(bill.amount).toFixed(2)}</strong></td>
            <td><span class="badge badge-${bill.paymentStatus.toLowerCase()}">${bill.paymentStatus}</span></td>
            <td><span class="badge badge-${bill.paymentMode.toLowerCase()}">${bill.paymentMode}</span></td>
            <td><small>${formatDate(bill.billDate)}</small></td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn" onclick="editBill(${bill.id})" title="Edit">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger action-btn" onclick="confirmDelete(${bill.id})" title="Delete">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Update dashboard statistics
function updateDashboard(bills) {
    const totalBills = bills.length;
    const completed = bills.filter(b => b.paymentStatus === 'COMPLETED');
    const pending = bills.filter(b => b.paymentStatus === 'PENDING');
    const failed = bills.filter(b => b.paymentStatus === 'FAILED');
    
    const paidAmount = completed.reduce((sum, b) => sum + parseFloat(b.amount), 0);
    const pendingAmount = pending.reduce((sum, b) => sum + parseFloat(b.amount), 0);
    const failedAmount = failed.reduce((sum, b) => sum + parseFloat(b.amount), 0);
    
    document.getElementById('totalBills').textContent = totalBills;
    document.getElementById('paidAmount').textContent = `$${paidAmount.toFixed(2)}`;
    document.getElementById('pendingAmount').textContent = `$${pendingAmount.toFixed(2)}`;
    document.getElementById('failedAmount').textContent = `$${failedAmount.toFixed(2)}`;
}

// Filter bills
function filterBills() {
    const statusFilter = document.getElementById('statusFilter').value;
    const modeFilter = document.getElementById('modeFilter').value;
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    let filtered = allBills;
    
    if (statusFilter) {
        filtered = filtered.filter(b => b.paymentStatus === statusFilter);
    }
    
    if (modeFilter) {
        filtered = filtered.filter(b => b.paymentMode === modeFilter);
    }
    
    if (searchTerm) {
        filtered = filtered.filter(b => 
            b.biller.toLowerCase().includes(searchTerm) ||
            b.description.toLowerCase().includes(searchTerm) ||
            b.amount.toString().includes(searchTerm)
        );
    }
    
    displayBills(filtered);
}

// Add new bill
async function addBill(billData) {
    try {
        showLoading();
        const response = await fetch(`${API_URL}/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(billData)
        });

        if (response.ok) {
            showToast('Success', 'Bill added successfully!', 'success');
            resetForm();
            await loadBills();
        } else {
            const errorText = await response.text();
            showToast('Error', 'Failed to add bill: ' + errorText, 'danger');
        }
        hideLoading();
    } catch (error) {
        console.error('Error adding bill:', error);
        showToast('Error', 'Failed to add bill: ' + error.message, 'danger');
        hideLoading();
    }
}

// Edit bill
async function editBill(id) {
    try {
        showLoading();
        const response = await fetch(`${API_URL}/get/${id}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch bill details');
        }
        
        const bill = await response.json();

        document.getElementById('billId').value = bill.id;
        document.getElementById('biller').value = bill.biller;
        document.getElementById('description').value = bill.description;
        document.getElementById('amount').value = bill.amount;
        document.getElementById('paymentMode').value = bill.paymentMode;
        document.getElementById('paymentStatus').value = bill.paymentStatus;

        editingBillId = id;
        document.getElementById('formTitle').textContent = 'Edit Bill';
        document.getElementById('submitBtnText').textContent = 'Update Bill';
        document.getElementById('cancelBtn').style.display = 'block';
        
        // Scroll to form
        document.querySelector('.col-lg-4').scrollIntoView({ behavior: 'smooth' });
        hideLoading();
    } catch (error) {
        console.error('Error loading bill:', error);
        showToast('Error', 'Failed to load bill details', 'danger');
        hideLoading();
    }
}

// Update bill
async function updateBill(billData) {
    try {
        showLoading();
        const response = await fetch(`${API_URL}/edit`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(billData)
        });

        if (response.ok) {
            showToast('Success', 'Bill updated successfully!', 'success');
            resetForm();
            await loadBills();
        } else {
            const errorText = await response.text();
            showToast('Error', 'Failed to update bill: ' + errorText, 'danger');
        }
        hideLoading();
    } catch (error) {
        console.error('Error updating bill:', error);
        showToast('Error', 'Failed to update bill: ' + error.message, 'danger');
        hideLoading();
    }
}

// Confirm delete
function confirmDelete(id) {
    document.getElementById('confirmDeleteBtn').onclick = () => deleteBill(id);
    deleteModal.show();
}

// Delete bill
async function deleteBill(id) {
    try {
        showLoading();
        const response = await fetch(`${API_URL}/delete/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Success', 'Bill deleted successfully!', 'success');
            deleteModal.hide();
            await loadBills();
        } else {
            showToast('Error', 'Failed to delete bill', 'danger');
        }
        hideLoading();
    } catch (error) {
        console.error('Error deleting bill:', error);
        showToast('Error', 'Failed to delete bill: ' + error.message, 'danger');
        hideLoading();
    }
}

// Reset form
function resetForm() {
    document.getElementById('billForm').reset();
    document.getElementById('billId').value = '';
    editingBillId = null;
    document.getElementById('formTitle').textContent = 'Add New Bill';
    document.getElementById('submitBtnText').textContent = 'Save Bill';
    document.getElementById('cancelBtn').style.display = 'none';
    setDefaultDate();
}

// Export to CSV
function exportToCSV() {
    if (allBills.length === 0) {
        showToast('Warning', 'No bills to export', 'warning');
        return;
    }

    const headers = ['ID', 'Biller', 'Description', 'Amount', 'Payment Mode', 'Payment Status', 'Bill Date'];
    const rows = allBills.map(bill => [
        bill.id,
        bill.biller,
        bill.description,
        bill.amount,
        bill.paymentMode,
        bill.paymentStatus,
        formatDate(bill.billDate)
    ]);

    let csvContent = headers.join(',') + '\n';
    rows.forEach(row => {
        csvContent += row.map(cell => `"${cell}"`).join(',') + '\n';
    });

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `bills_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
    
    showToast('Success', 'Bills exported successfully!', 'success');
}

// Show toast notification
function showToast(title, message, type = 'info') {
    const toastEl = document.getElementById('toast');
    const toastTitle = document.getElementById('toastTitle');
    const toastMessage = document.getElementById('toastMessage');
    
    toastTitle.textContent = title;
    toastMessage.textContent = message;
    
    // Update toast color based on type
    toastEl.className = 'toast';
    if (type === 'success') {
        toastEl.classList.add('bg-success', 'text-white');
    } else if (type === 'danger') {
        toastEl.classList.add('bg-danger', 'text-white');
    } else if (type === 'warning') {
        toastEl.classList.add('bg-warning');
    }
    
    toast.show();
}

// Show loading spinner
function showLoading() {
    const spinner = document.createElement('div');
    spinner.id = 'loadingSpinner';
    spinner.className = 'spinner-overlay';
    spinner.innerHTML = '<div class="spinner-border text-light" role="status"><span class="visually-hidden">Loading...</span></div>';
    document.body.appendChild(spinner);
}

// Hide loading spinner
function hideLoading() {
    const spinner = document.getElementById('loadingSpinner');
    if (spinner) {
        spinner.remove();
    }
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Refresh bills every 30 seconds
setInterval(() => {
    loadBills();
}, 30000);
