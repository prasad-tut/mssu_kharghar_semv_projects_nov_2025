// API Base URL
const API_URL = 'http://localhost:8081/api';

// Load all medicines when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadAllMedicines();
    
    // Form submit handler
    document.getElementById('medicineForm').addEventListener('submit', function(e) {
        e.preventDefault();
        handleFormSubmit();
    });
});

// Load all medicines
function loadAllMedicines() {
    fetch(`${API_URL}/medicines`)
        .then(response => response.json())
        .then(data => {
            displayMedicines(data);
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error loading medicines', 'error');
        });
}

// Display medicines in table
function displayMedicines(medicines) {
    const tableBody = document.getElementById('medicinesTableBody');
    const noDataMessage = document.getElementById('noDataMessage');
    
    if (medicines.length === 0) {
        tableBody.innerHTML = '';
        noDataMessage.style.display = 'block';
        return;
    }
    
    noDataMessage.style.display = 'none';
    
    tableBody.innerHTML = medicines.map(medicine => `
        <tr>
            <td>${medicine.medicineId}</td>
            <td>${medicine.medicineName}</td>
            <td>${medicine.manufacturer}</td>
            <td>${medicine.quantity}</td>
            <td>$${medicine.price.toFixed(2)}</td>
            <td>${formatDate(medicine.expiryDate)}</td>
            <td>${medicine.category}</td>
            <td>
                <div class="action-buttons">
                    <button onclick="editMedicine(${medicine.medicineId})" class="btn btn-edit">Edit</button>
                    <button onclick="deleteMedicine(${medicine.medicineId})" class="btn btn-delete">Delete</button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Format date from timestamp
function formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toISOString().split('T')[0];
}

// Handle form submit (Add or Update)
function handleFormSubmit() {
    const medicineId = document.getElementById('medicineId').value;
    const medicineData = {
        medicineName: document.getElementById('medicineName').value,
        manufacturer: document.getElementById('manufacturer').value,
        quantity: document.getElementById('quantity').value,
        price: document.getElementById('price').value,
        expiryDate: document.getElementById('expiryDate').value,
        category: document.getElementById('category').value
    };
    
    if (medicineId) {
        // Update existing medicine
        medicineData.medicineId = medicineId;
        updateMedicine(medicineData);
    } else {
        // Add new medicine
        addMedicine(medicineData);
    }
}

// Add new medicine
function addMedicine(medicineData) {
    fetch(`${API_URL}/medicine/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(medicineData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast(data.message, 'success');
            resetForm();
            loadAllMedicines();
        } else {
            showToast(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Error adding medicine', 'error');
    });
}

// Update medicine
function updateMedicine(medicineData) {
    fetch(`${API_URL}/medicine/update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(medicineData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast(data.message, 'success');
            resetForm();
            loadAllMedicines();
        } else {
            showToast(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Error updating medicine', 'error');
    });
}

// Edit medicine - populate form with medicine data
function editMedicine(medicineId) {
    fetch(`${API_URL}/medicines`)
        .then(response => response.json())
        .then(medicines => {
            const medicine = medicines.find(m => m.medicineId === medicineId);
            if (medicine) {
                document.getElementById('medicineId').value = medicine.medicineId;
                document.getElementById('medicineName').value = medicine.medicineName;
                document.getElementById('manufacturer').value = medicine.manufacturer;
                document.getElementById('quantity').value = medicine.quantity;
                document.getElementById('price').value = medicine.price;
                document.getElementById('expiryDate').value = formatDate(medicine.expiryDate);
                document.getElementById('category').value = medicine.category;
                
                // Change form title and button text
                document.getElementById('formTitle').textContent = 'Update Medicine';
                document.getElementById('submitBtn').textContent = 'Update Medicine';
                
                // Scroll to form
                document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error loading medicine data', 'error');
        });
}

// Delete medicine
function deleteMedicine(medicineId) {
    if (confirm('Are you sure you want to delete this medicine?')) {
        fetch(`${API_URL}/medicine/delete?id=${medicineId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast(data.message, 'success');
                loadAllMedicines();
            } else {
                showToast(data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error deleting medicine', 'error');
        });
    }
}

// Search medicine by name
function searchMedicine() {
    const searchTerm = document.getElementById('searchInput').value.trim();
    
    if (searchTerm === '') {
        showToast('Please enter a search term', 'error');
        return;
    }
    
    fetch(`${API_URL}/medicine/search?name=${encodeURIComponent(searchTerm)}`)
        .then(response => response.json())
        .then(data => {
            displayMedicines(data);
            if (data.length === 0) {
                showToast('No medicines found', 'error');
            } else {
                showToast(`Found ${data.length} medicine(s)`, 'success');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error searching medicines', 'error');
        });
}

// Reset form
function resetForm() {
    document.getElementById('medicineForm').reset();
    document.getElementById('medicineId').value = '';
    document.getElementById('formTitle').textContent = 'Add New Medicine';
    document.getElementById('submitBtn').textContent = 'Add Medicine';
}

// Show toast notification
function showToast(message, type) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    
    setTimeout(() => {
        toast.className = 'toast';
    }, 3000);
}

// Allow search on Enter key
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchMedicine();
        }
    });
});