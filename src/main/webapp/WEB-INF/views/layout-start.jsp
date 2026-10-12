<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? 'Courier Tracking' : pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/app.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">CPT Courier</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="nav">
            <ul class="navbar-nav me-auto">
                <c:if test="${empty sessionScope.user}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/track">Track</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/register">Register</a></li>
                </c:if>
                <c:if test="${not empty sessionScope.user}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a></li>
                    <c:if test="${sessionScope.user.admin}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/customers">Customers</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/agents">Agents</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/parcels">Parcels</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/pickups">Pickups</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/assign">Assign</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/invoices">Invoices</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/reports">Reports</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/users">Users</a></li>
                    </c:if>
                    <c:if test="${sessionScope.user.customer}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/pickups">Schedule pickup</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/parcels">My parcels</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/tracking">Track</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/invoices">Invoices</a></li>
                    </c:if>
                    <c:if test="${sessionScope.user.agent}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/app/deliveries">Deliveries</a></li>
                    </c:if>
                </c:if>
            </ul>
            <c:if test="${not empty sessionScope.user}">
                <span class="navbar-text text-white me-3">${sessionScope.user.fullName} (${sessionScope.user.role})</span>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a>
            </c:if>
        </div>
    </div>
</nav>
<div class="container mb-5">
    <c:if test="${not empty flash}">
        <div class="alert alert-success">${flash}</div>
    </c:if>
    <c:if test="${not empty flashError}">
        <div class="alert alert-danger">${flashError}</div>
    </c:if>
