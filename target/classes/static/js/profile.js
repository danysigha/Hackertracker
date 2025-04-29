document.addEventListener('DOMContentLoaded', function() {
    const editButton = document.getElementById('editButton');
    const saveButton = document.getElementById('saveButton');
    const inputs = document.querySelectorAll('input');

    // Toggle between edit and view mode
    editButton.addEventListener('click', function() {
        // Show save button and hide edit button
        editButton.style.display = 'none';
        saveButton.style.display = 'block';

        // Make fields editable
        inputs.forEach(input => {
            if (input.id !== 'email') { // Optionally keep email read-only
                input.readOnly = false;
            }

            // Clear password field for security
            if (input.id === 'password') {
                input.value = '';
                input.placeholder = 'Enter new password (leave empty to keep current)';
            }
        });
    });

    // Handle form submission
    document.getElementById('profileForm').addEventListener('submit', function(e) {
        // You could add client-side validation here

        // If password field is empty, remove it from the form submission
        const passwordField = document.getElementById('password');
        if (passwordField.value.trim() === '') {
            passwordField.disabled = true;
        }

        // Form will submit to the controller action
        return true;
    });
});