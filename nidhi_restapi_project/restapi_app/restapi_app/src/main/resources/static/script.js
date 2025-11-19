const API_BASE_URL = 'http://localhost:9080/api/visitors';

// Check-in Form Submission
document.getElementById('checkinForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const visitorData = {
        visitorName: document.getElementById('visitorName').value,
        contactNumber: document.getElementById('contactNumber').value,
        email: document.getElementById('email').value || null,
        purpose: document.getElementById('purpose').value,
        hostName: document.getElementById('hostName').value,
        department: document.getElementById('department').value || null,
        visitorType: document.getElementById('visitorType').value,
        idProofType: document.getElementById('idProofType').value || null,
        idProofNumber: document.getElementById('idProofNumber').value || null,
        remarks: document.getElementById('remarks').value || null
    };

    try {
        const response = await fetch(`${API_BASE_URL}/checkin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(visitorData)
        });

        if (response.ok) {
            const result = await response.json();
            showToast('Visitor checked in successfully!', 'success');
            document.getElementById('checkinForm').reset();
            getAllVisitors(); // Refresh the list
        } else {
            showToast('Failed to check in visitor', 'error');
        }
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
});

// Get All Visitors
async function getAllVisitors() {
    try {
        const response = await fetch(API_BASE_URL);
        const visitors = await response.json();
        displayVisitors(visitors);
    } catch (error) {
        showToast('Error loading visitors: ' + error.message, 'error');
    }
}

// Get Currently Checked-in Visitors
async function getCurrentVisitors() {
    try {
        const response = await fetch(`${API_BASE_URL}/current`);
        const visitors = await response.json();
        displayVisitors(visitors);
        showToast(`Found ${visitors.length} currently checked-in visitors`, 'info');
    } catch (error) {
        showToast('Error loading current visitors: ' + error.message, 'error');
    }
}

// Search by Name
async function searchByName() {
    const searchTerm = document.getElementById('searchName').value;
    if (!searchTerm) {
        showToast('Please enter a name to search', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(searchTerm)}`);
        const visitors = await response.json();
        displayVisitors(visitors);
        showToast(`Found ${visitors.length} visitor(s)`, 'info');
    } catch (error) {
        showToast('Error searching visitors: ' + error.message, 'error');
    }
}

// Check-out Visitor
async function checkoutVisitor(id) {
    if (!confirm('Are you sure you want to check out this visitor?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/checkout/${id}`, {
            method: 'PUT'
        });

        if (response.ok) {
            showToast('Visitor checked out successfully!', 'success');
            getAllVisitors(); // Refresh the list
        } else {
            const error = await response.json();
            showToast(error.message || 'Failed to check out visitor', 'error');
        }
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

// Delete Visitor
async function deleteVisitor(id) {
    if (!confirm('Are you sure you want to delete this visitor record?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Visitor record deleted successfully!', 'success');
            getAllVisitors(); // Refresh the list
        } else {
            showToast('Failed to delete visitor', 'error');
        }
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

// Display Visitors
function displayVisitors(visitors) {
    const visitorsList = document.getElementById('visitorsList');
    
    if (!visitors || visitors.length === 0) {
        visitorsList.innerHTML = '<p class="no-data">No visitors found</p>';
        return;
    }

    visitorsList.innerHTML = visitors.map(visitor => `
        <div class="visitor-card ${visitor.checkOutTime ? 'checked-out' : ''}">
            <div class="visitor-header">
                <div>
                    <div class="visitor-name">${visitor.visitorName}</div>
                    <span class="visitor-type type-${visitor.visitorType}">${visitor.visitorType}</span>
                </div>
                <span class="status-badge ${visitor.checkOutTime ? 'status-checked-out' : 'status-checked-in'}">
                    ${visitor.checkOutTime ? '✓ Checked Out' : '● Checked In'}
                </span>
            </div>
            
            <div class="visitor-details">
                <div class="detail-item">
                    <span class="detail-label">Contact</span>
                    <span class="detail-value">${visitor.contactNumber}</span>
                </div>
                ${visitor.email ? `
                <div class="detail-item">
                    <span class="detail-label">Email</span>
                    <span class="detail-value">${visitor.email}</span>
                </div>
                ` : ''}
                <div class="detail-item">
                    <span class="detail-label">Host</span>
                    <span class="detail-value">${visitor.hostName}</span>
                </div>
                ${visitor.department ? `
                <div class="detail-item">
                    <span class="detail-label">Department</span>
                    <span class="detail-value">${visitor.department}</span>
                </div>
                ` : ''}
                <div class="detail-item">
                    <span class="detail-label">Purpose</span>
                    <span class="detail-value">${visitor.purpose}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Check-in Time</span>
                    <span class="detail-value">${formatDateTime(visitor.checkInTime)}</span>
                </div>
                ${visitor.checkOutTime ? `
                <div class="detail-item">
                    <span class="detail-label">Check-out Time</span>
                    <span class="detail-value">${formatDateTime(visitor.checkOutTime)}</span>
                </div>
                ` : ''}
                ${visitor.idProofType ? `
                <div class="detail-item">
                    <span class="detail-label">ID Proof</span>
                    <span class="detail-value">${visitor.idProofType}: ${visitor.idProofNumber || 'N/A'}</span>
                </div>
                ` : ''}
                ${visitor.remarks ? `
                <div class="detail-item">
                    <span class="detail-label">Remarks</span>
                    <span class="detail-value">${visitor.remarks}</span>
                </div>
                ` : ''}
            </div>
            
            <div class="visitor-actions">
                ${!visitor.checkOutTime ? `
                    <button onclick="checkoutVisitor(${visitor.id})" class="btn btn-warning">Check Out</button>
                ` : ''}
                <button onclick="deleteVisitor(${visitor.id})" class="btn btn-danger">Delete</button>
            </div>
        </div>
    `).join('');
}

// Format DateTime
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Show Toast Notification
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Search on Enter key
document.getElementById('searchName').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        searchByName();
    }
});
