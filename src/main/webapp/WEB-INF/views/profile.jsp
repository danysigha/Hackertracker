<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Profile</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <style>
        .container {
            max-width: 600px;
            margin-top: 50px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .profile-header {
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="profile-header text-center">
        <h2>Hi, ${user.firstName} ${user.lastName}</h2>
        <p class="text-muted">Your Profile Information</p>
    </div>

    <form id="profileForm" action="/user/update" method="post">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" class="form-control" id="email" name="email"
                   value="${user.email}" readonly>
        </div>

        <div class="form-group">
            <label for="userName">Username</label>
            <input type="text" class="form-control" id="userName" name="userName"
                   value="${user.userName}" readonly>
        </div>

        <div class="form-group">
            <label for="firstName">First Name</label>
            <input type="text" class="form-control" id="firstName" name="firstName"
                   value="${user.firstName}" readonly>
        </div>

        <div class="form-group">
            <label for="lastName">Last Name</label>
            <input type="text" class="form-control" id="lastName" name="lastName"
                   value="${user.lastName}" readonly>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" class="form-control" id="password" name="password"
                   value="********" readonly>
        </div>

        <div class="d-flex justify-content-between mt-4">
            <button type="button" id="editButton" class="btn btn-primary">Edit Profile</button>
            <button type="submit" id="saveButton" class="btn btn-success" style="display: none;">Save Changes</button>
        </div>
    </form>
</div>

<script>
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
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>