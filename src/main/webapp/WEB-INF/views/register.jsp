<%-- 
    Document   : register
    Created on : Mar 26, 2025, 9:48:57 PM
    Author     : danysigha
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="/css/baseStyle.css">
        <link rel="stylesheet" href="/css/loginRegisterStyle.css">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>HackerTracker</title>
    </head>

    <body>
        <nav>
            <img class="logo" src="/assets/hackertracker.png" alt="HackerTracker Logo">

            <div class="menu">
                <a href="#why">Why learn DSA?</a>
                <a href="#demo">Demo</a>
                <a href="#instructions">Instructions</a>
            </div>

            <div class="navigation-buttons">
                <a href="login">Log In</a>
                <a href="register">Register</a>
            </div>
        </nav>

        <main>
            <form:form modelAttribute = "user" method = "POST">
                <div class="login-form">
                    <img class="logo" src="/assets/hackertracker.png" alt="HackerTracker Logo">
                    <h1>Register for Hackertracker</h1>
                    <form:input path = "firstName" class="input"  placeholder = "First Name"/> <form:errors path = "firstName" cssClass="error"/>
                    <form:input path = "lastName" class="input"  placeholder = "Last Name"/> <form:errors path = "lastName" cssClass="error"/>
                    <form:input path = "email" class="input"  placeholder = "Email"/> <form:errors path = "email" cssClass="error"/>
                    <form:input path = "userName" class="input"  placeholder = "Username"/> <form:errors path = "userName" cssClass="error"/>
                    <form:password path = "password" class="input"  placeholder = "Password"/> <form:errors path = "password" cssClass="error"/>
                    <input type="submit" value="Register" class="register-button"/>
                </div>
            </form:form>
        </main>

    </body>
</html>

