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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>HackerTracker</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        <form:form modelAttribute = "user" method = "POST">
            First Name: <form:input path = "firstName"/> <form:errors path = "firstName" />
            Last Name: <form:input path = "lastName"/> <form:errors path = "lastName" />
            Email: <form:input path = "email"/> <form:errors path = "email" />
            Username: <form:input path = "userName"/> <form:errors path = "userName" />
            Password: <form:password path = "password"/> <form:errors path = "password" />
            <input type="submit" value="Register"/>
        </form:form>
    </body>
</html>

