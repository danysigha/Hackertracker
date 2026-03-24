<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>

    <script>
        document.documentElement.classList.add('loading');
    </script>

    <style>
        html.loading {
            visibility: hidden;
        }
    </style>

    <script src="/js/theme.js"></script>

    <meta charset="UTF-8">
    <title>User Profile</title>
    <link rel="stylesheet" href="/css/profile.css">
    <link rel="icon" type="image/svg+xml" href="/assets/favicon/favicon.svg">

</head>
<body>
    <nav id="main-nav">
        <img id="logo" src="/assets/hackertracker.svg" alt="HackerTracker Logo">

        <div class="navigation-buttons">

            <a href="/dashboard">
                <button>
                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#1f1f1f" preserveAspectRatio="xMidYMid meet"><path d="M220-180h150v-250h220v250h150v-390L480-765 220-570v390Zm-60 60v-480l320-240 320 240v480H530v-250H430v250H160Zm320-353Z"/></svg>
                </button>
            </a>

            <a href="">
                <button id="lightSwitch">
                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" version="1.1" preserveAspectRatio="xMidYMid meet" viewBox="5.0 1.0 90.0 90.0">
                        <path d="m50 27.707c-5.9141 0-11.582 2.3516-15.762 6.5312-4.1797 4.1797-6.5312 9.8477-6.5312 15.762s2.3516 11.582 6.5312 15.762c4.1797 4.1797 9.8477 6.5312 15.762 6.5312s11.582-2.3516 15.762-6.5312c4.1797-4.1797 6.5312-9.8477 6.5312-15.762-0.011719-5.9102-2.3633-11.574-6.543-15.75-4.1758-4.1797-9.8398-6.5312-15.75-6.543zm0 38.961c-4.3398-0.054688-8.4844-1.8242-11.527-4.918-3.043-3.0977-4.7383-7.2695-4.7227-11.609 0.019531-4.3398 1.7539-8.5 4.8203-11.57 3.0703-3.0664 7.2305-4.8008 11.57-4.8203 4.3398-0.015625 8.5117 1.6797 11.609 4.7227 3.0938 3.043 4.8633 7.1875 4.918 11.527 0 4.4219-1.7578 8.6602-4.8828 11.785s-7.3633 4.8828-11.785 4.8828zm2.918 13.707v8.375c0 1.6094-1.3086 2.918-2.918 2.918s-2.918-1.3086-2.918-2.918v-8.582c0-1.6133 1.3086-2.918 2.918-2.918s2.918 1.3047 2.918 2.918zm-22.168-11.125c1.1445 1.1523 1.1445 3.0117 0 4.168l-6.082 6.0391c-0.55078 0.5625-1.3008 0.875-2.0859 0.875-0.76953 0-1.5078-0.31641-2.0391-0.875-0.57031-0.54297-0.89453-1.293-0.89453-2.082s0.32422-1.5391 0.89453-2.082l6.082-6.125c1.1719-1.0938 3-1.0586 4.125 0.082031zm-10.918-16.332h-8.582c-1.6094 0-2.918-1.3086-2.918-2.918s1.3086-2.918 2.918-2.918h8.582c1.6133 0 2.918 1.3086 2.918 2.918s-1.3047 2.918-2.918 2.918zm0.70703-28.25h0.003907c-1.1523-1.1523-1.1523-3.0156 0-4.168 1.1484-1.1523 3.0156-1.1523 4.1641 0l6.043 6.125c1.1445 1.1523 1.1445 3.0117 0 4.168-0.56641 0.52734-1.3086 0.82422-2.082 0.83203-0.76562-0.003906-1.4961-0.30469-2.043-0.83203zm26.543-4.8359v-8.582c0-1.6094 1.3086-2.918 2.918-2.918s2.918 1.3086 2.918 2.918v8.582c0 1.6133-1.3086 2.918-2.918 2.918s-2.918-1.3047-2.918-2.918zm22.168 10.918c-1.1445-1.1523-1.1445-3.0117 0-4.168l6.082-6.082c1.1523-1.1523 3.0156-1.1523 4.168 0s1.1523 3.0156 0 4.168l-6.125 6.082c-0.54688 0.53125-1.2773 0.82812-2.043 0.83203-0.77344-0.003906-1.5156-0.30078-2.082-0.83203zm22.418 19.25c0 1.6094-1.3086 2.918-2.918 2.918h-8.582c-1.6133 0-2.918-1.3086-2.918-2.918s1.3047-2.918 2.918-2.918h8.582c0.77344 0 1.5156 0.30859 2.0625 0.85547s0.85547 1.2891 0.85547 2.0625zm-12.207 25.332h-0.003907c1.1445 1.1562 1.1445 3.0156 0 4.168-0.52734 0.5625-1.2695 0.87891-2.0391 0.875-0.78516 0-1.5352-0.31641-2.0859-0.875l-6.082-6.082c-1.1523-1.1523-1.1523-3.0156 0-4.168s3.0156-1.1523 4.168 0z"/>
                    </svg>
                </button>
            </a>

            <a href="/progress">
                <button>
                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960" preserveAspectRatio="xMidYMid meet" fill="#1f1f1f">
                        <path d="M480-55q-90.2 0-167.57-32.58-77.37-32.57-134.82-90.03-57.46-57.45-90.03-134.82Q55-389.8 55-480q0-90.14 32.56-167.38 32.57-77.24 89.87-134.98 57.31-57.74 134.79-90.69Q389.7-906 480-906q19 0 33 14.59t14 33.5Q527-839 513-825q-14 14-33 14-138.01 0-234.51 96.49Q149-618.02 149-480.01t96.49 234.51q96.49 96.5 234.5 96.5t234.51-96.49Q811-341.99 811-480q0-19 14-33t32.91-14q18.91 0 33.5 14T909-480q0 90.2-32.96 167.68-32.95 77.49-90.46 134.84-57.51 57.34-134.78 89.91Q574.53-55 480-55Z"/>
                    </svg>
                </button>
            </a>

            <a href="/logout">
                <button>
                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" preserveAspectRatio="xMidYMid meet" width="48px" fill="#1f1f1f"><path d="M180-120q-24 0-42-18t-18-42v-600q0-24 18-42t42-18h299v60H180v600h299v60H180Zm486-185-43-43 102-102H360v-60h363L621-612l43-43 176 176-174 174Z"/></svg>
                </button>
            </a>
        </div>
    </nav>
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

    <script src="/js/profile.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>