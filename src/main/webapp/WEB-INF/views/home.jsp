<%-- 
    Document   : userSuccess
    Created on : Mar 26, 2025, 10:56:43 PM
    Author     : danysigha
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib uri="/WEB-INF/tlds/code-challenge" prefix="code"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link rel="stylesheet" href="/css/baseStyle.css">
        <link rel="stylesheet" href="/css/homeStyle.css">
        <title>HackerTracker</title>
    </head>

    <body>
        <nav>
            <img class="logo" src="/assets/hackertracker.png" alt="HackerTracker Logo">

            <div class="navigation-buttons">
                <a href="progress"><img src="" alt="Progress menu"></a>
                <button><img src="" alt="Night mode switch"></button>
                <a href="settings"><img src="" alt="Settings menu"></a>
            </div>
        </nav>

        <main>
            <code:challenge priorities="${priorities}"/>
        </main>
    </body>

</html>

<%--<!DOCTYPE html>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Debug Page</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--<h1>Debug Page</h1>--%>

<%--<p>Testing if JSP renders at all</p>--%>

<%--<c:if test="${user != null}">--%>
<%--    <p>User is not null: ${user}</p>--%>
<%--</c:if>--%>

<%--<c:if test="${user == null}">--%>
<%--    <p>User is not null!</p>--%>
<%--</c:if>--%>

<%--<c:if test="${priorities == null}">--%>
<%--    <p>priorities is null!</p>--%>
<%--</c:if>--%>

<%--<c:if test="${priorities != null}">--%>
<%--    <p>problemDtos is not null: ${priorities}</p>--%>
<%--</c:if>--%>

<%--</body>--%>
<%--</html>--%>