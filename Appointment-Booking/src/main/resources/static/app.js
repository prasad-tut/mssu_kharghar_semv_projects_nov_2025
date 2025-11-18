const API_BASE_URL = 'http://localhost:9080/appointments';

let isEditMode = false;

// Load appointments on page load
document.addEventListener('DOMContentLoaded', () => {
    loadAppointments();
    setupFormHandlers();
});

// Setup form event handlers
function setupFormHandlers() {
    const form = document.getElementById('appointment-form');
    const cancelBtn = document.getElementById('cancel-btn');

    form.addEventListener('submit', handleFormSubmit);
    cancelBtn.addEventListener('click', resetForm);
}

// Load all appointments
async function loadAppointments() {
    try {
        const response = await fetch(`${API_BASE_URL}/get`);
        const appointments = await response.json();
        displayAppointments(appointments);
    } catch (error) {
        console.error('Error loading appointments:', error);
        showError('Failed to load appointments');
    }
}

// Display appointments in the list
function displayAppointments(appointments) {
    const listContainer = document.getElementById('appointments-list');
    
    if (appointments.length === 0) {
        listContainer.innerHTML = `
            <div class="empty-state">
                <div style="font-size: 48px;">📭</div>
                <p>No appointments found</p>
            </div>
        `;
        return;
    }

    listContainer.innerHTML = appointments.map(appointment => `
        <div class="appointment-card">
            <div class="appointment-header">
                <div class="appointment-id">Appointment #${appointment.appointmentId}</div>
                <div class="appointment-status status-${appointment.status.toLowerCase()}">
                    ${appointment.status}
                </div>
            </div>
            <div class="appointment-details">
                <div class="detail-item">
                    <span class="detail-label">Client ID:</span> ${appointment.clientId}
                </div>
                <div class="detail-item">
                    <span class="detail-label">Provider ID:</span> ${appointment.providerId}
                </div>
                <div class="detail-item">
                    <span class="detail-label">Service ID:</span> ${appointment.serviceId}
                </div>
                <div class="detail-item">
                    <span class="detail-label">Date:</span> ${appointment.appointmentDate}
                </div>
                <div class="detail-item">
                    <span class="detail-label">Time:</span> ${appointment.appointmentTime}
                </div>
                <div class="detail-item">
                    <span class="detail-label">Created:</span> ${appointment.createdAt || 'N/A'}
                </div>
            </div>
            <div class="appointment-actions">
                <button class="btn btn-edit" onclick="editAppointment(${appointment.appointmentId})">
                    ✏️ Edit
                </button>
                <button class="btn btn-delete" onclick="deleteAppointment(${appointment.appointmentId})">
                    🗑️ Delete
                </button>
            </div>
        </div>
    `).join('');
}

// Handle form submission
async function handleFormSubmit(e) {
    e.preventDefault();

    const appointment = {
        clientId: parseInt(document.getElementById('clientId').value),
        providerId: parseInt(document.getElementById('providerId').value),
        serviceId: parseInt(document.getElementById('serviceId').value),
        appointmentDate: document.getElementById('appointmentDate').value,
        appointmentTime: document.getElementById('appointmentTime').value,
        status: document.getElementById('status').value
    };

    try {
        if (isEditMode) {
            appointment.appointmentId = parseInt(document.getElementById('appointmentId').value);
            await updateAppointment(appointment);
        } else {
            await addAppointment(appointment);
        }
        
        resetForm();
        loadAppointments();
    } catch (error) {
        console.error('Error saving appointment:', error);
        showError('Failed to save appointment');
    }
}

// Add new appointment
async function addAppointment(appointment) {
    const response = await fetch(`${API_BASE_URL}/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(appointment)
    });

    if (!response.ok) {
        throw new Error('Failed to add appointment');
    }
}

// Update existing appointment
async function updateAppointment(appointment) {
    const response = await fetch(`${API_BASE_URL}/edit`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(appointment)
    });

    if (!response.ok) {
        throw new Error('Failed to update appointment');
    }
}

// Edit appointment
async function editAppointment(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/get/${id}`);
        const appointment = await response.json();

        document.getElementById('appointmentId').value = appointment.appointmentId;
        document.getElementById('clientId').value = appointment.clientId;
        document.getElementById('providerId').value = appointment.providerId;
        document.getElementById('serviceId').value = appointment.serviceId;
        document.getElementById('appointmentDate').value = appointment.appointmentDate;
        document.getElementById('appointmentTime').value = appointment.appointmentTime;
        document.getElementById('status').value = appointment.status;

        document.getElementById('form-title').textContent = 'Edit Appointment';
        document.getElementById('submit-btn').textContent = 'Update Appointment';
        document.getElementById('cancel-btn').style.display = 'block';
        
        isEditMode = true;

        // Scroll to form
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error loading appointment:', error);
        showError('Failed to load appointment details');
    }
}

// Delete appointment
async function deleteAppointment(id) {
    if (!confirm('Are you sure you want to delete this appointment?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/delete/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete appointment');
        }

        loadAppointments();
    } catch (error) {
        console.error('Error deleting appointment:', error);
        showError('Failed to delete appointment');
    }
}

// Reset form
function resetForm() {
    document.getElementById('appointment-form').reset();
    document.getElementById('appointmentId').value = '';
    document.getElementById('form-title').textContent = 'Add New Appointment';
    document.getElementById('submit-btn').textContent = 'Add Appointment';
    document.getElementById('cancel-btn').style.display = 'none';
    isEditMode = false;
}

// Show error message
function showError(message) {
    alert(message);
}
