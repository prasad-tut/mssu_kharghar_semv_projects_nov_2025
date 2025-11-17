const API_BASE_URL = '/api/courses';

// DOM Elements
const courseForm = document.getElementById('courseForm');
const editForm = document.getElementById('editForm');
const coursesList = document.getElementById('coursesList');
const emptyState = document.getElementById('emptyState');
const editModal = document.getElementById('editModal');
const toast = document.getElementById('toast');
const refreshBtn = document.getElementById('refreshBtn');
const cancelBtn = document.getElementById('cancelBtn');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadCourses();
    setupEventListeners();
});

// Event Listeners
function setupEventListeners() {
    courseForm.addEventListener('submit', handleAddCourse);
    editForm.addEventListener('submit', handleUpdateCourse);
    refreshBtn.addEventListener('click', loadCourses);
    cancelBtn.addEventListener('click', resetForm);
    
    // Modal close handlers
    document.querySelector('.close').addEventListener('click', closeModal);
    document.querySelector('.modal-close').addEventListener('click', closeModal);
    
    window.addEventListener('click', (e) => {
        if (e.target === editModal) {
            closeModal();
        }
    });
}

// Load all courses
async function loadCourses() {
    try {
        const response = await fetch(API_BASE_URL);
        
        if (!response.ok) {
            throw new Error('Failed to fetch courses');
        }
        
        const courses = await response.json();
        displayCourses(courses);
    } catch (error) {
        showToast('Error loading courses: ' + error.message, 'error');
        console.error('Error:', error);
    }
}

// Display courses
function displayCourses(courses) {
    if (courses.length === 0) {
        coursesList.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }
    
    coursesList.style.display = 'grid';
    emptyState.style.display = 'none';
    
    coursesList.innerHTML = courses.map(course => `
        <div class="course-card">
            <h3>${escapeHtml(course.title)}</h3>
            <p><strong>Instructor:</strong> ${escapeHtml(course.instructor)}</p>
            <p><strong>Description:</strong> ${escapeHtml(course.description || 'No description')}</p>
            <div class="course-meta">
                <span>Duration: ${course.duration} hours</span>
                <span>ID: ${course.id}</span>
            </div>
            <div class="course-actions">
                <button class="btn btn-edit" onclick="openEditModal(${course.id})">Edit</button>
                <button class="btn btn-delete" onclick="deleteCourse(${course.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

// Add new course
async function handleAddCourse(e) {
    e.preventDefault();
    
    const formData = new FormData(courseForm);
    const courseData = {
        title: formData.get('title'),
        description: formData.get('description'),
        instructor: formData.get('instructor'),
        duration: parseInt(formData.get('duration'))
    };
    
    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(courseData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create course');
        }
        
        showToast('Course added successfully!', 'success');
        resetForm();
        loadCourses();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Error:', error);
    }
}

// Open edit modal
async function openEditModal(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch course details');
        }
        
        const course = await response.json();
        
        document.getElementById('editId').value = course.id;
        document.getElementById('editTitle').value = course.title;
        document.getElementById('editDescription').value = course.description || '';
        document.getElementById('editInstructor').value = course.instructor;
        document.getElementById('editDuration').value = course.duration;
        
        editModal.style.display = 'block';
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Error:', error);
    }
}

// Update course
async function handleUpdateCourse(e) {
    e.preventDefault();
    
    const id = document.getElementById('editId').value;
    const formData = new FormData(editForm);
    const courseData = {
        title: formData.get('title'),
        description: formData.get('description'),
        instructor: formData.get('instructor'),
        duration: parseInt(formData.get('duration'))
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(courseData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update course');
        }
        
        showToast('Course updated successfully!', 'success');
        closeModal();
        loadCourses();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Error:', error);
    }
}

// Delete course
async function deleteCourse(id) {
    if (!confirm('Are you sure you want to delete this course?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete course');
        }
        
        showToast('Course deleted successfully!', 'success');
        loadCourses();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Error:', error);
    }
}

// Close modal
function closeModal() {
    editModal.style.display = 'none';
    editForm.reset();
}

// Reset form
function resetForm() {
    courseForm.reset();
}

// Show toast notification
function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
