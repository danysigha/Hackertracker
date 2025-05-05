<%--
    Document   : register
    Created on : Mar 26, 2025, 9:48:57 PM
    Author     : danysigha
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="/css/baseStyle.css">
        <link rel="stylesheet" href="/css/loginRegisterStyle.css">
        <link rel="icon" href="/assets/favicon/favicon.ico" />
        <!-- Alternative PNG favicon in different sizes -->
<%--        <link rel="icon" type="image/png" sizes="96x96" href="/assets/favicon/favicon-96x96.png">--%>
        <!-- SVG Icon -->
        <link rel="icon" type="image/svg+xml" href="/assets/favicon/favicon.svg">
        <!-- Apple Touch Icon -->
<%--        <link rel="apple-touch-icon" sizes="180x180" href="/assets/favicon/apple-touch-icon.png">--%>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>HackerTracker</title>
    </head>
    <body>
        <nav>
            <img class="logo" src="/assets/hackertracker.svg" alt="HackerTracker Logo">

            <div class="navigation-buttons">
                <a href="login"> <button> Log In </button> </a>
                <a href="register"> <button> Register </button> </a>
            </div>

        </nav>

        <main>

        <%--    <form:form modelAttribute = "userDto" method = "POST">--%>
        <%--    <form:form modelAttribute="userDto" method="POST" action="/login">--%>
            <form:form modelAttribute = "user" method = "POST">
                <div class="login-form">
                    <img class="logo" src="/assets/hackertracker.svg" alt="HackerTracker Logo">
                    <h1>Log in to Hackertracker</h1>
                    <form:errors path="*" element="div" cssClass="error" />
                    <form:input path = "userName" placeholder = "Username" class="input"/>
                    <form:password path = "password" placeholder = "Password" class="input"/>
                    <input type="submit" value="Login" class="login-button"/>
                </div>

            </form:form>

        </main>

    </body>
</html>

