<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Courier and Parcel Tracking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<div class="hero p-5 mb-4">
    <h1 class="display-5">Courier and Parcel Tracking System</h1>
    <p class="lead mb-4">Schedule pickups, assign delivery agents, track shipments, and generate invoices.</p>
    <form class="row g-2" action="${pageContext.request.contextPath}/track" method="get">
        <div class="col-md-6">
            <input class="form-control form-control-lg" name="number" placeholder="Enter tracking number (e.g. CPT123456)" required>
        </div>
        <div class="col-auto">
            <button class="btn btn-light btn-lg" type="submit">Track shipment</button>
        </div>
    </form>
</div>
<div class="row g-3">
    <div class="col-md-4">
        <div class="card h-100 p-3">
            <h5>Customers</h5>
            <p class="text-muted mb-0">Register, schedule pickups, and view invoices.</p>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 p-3">
            <h5>Delivery agents</h5>
            <p class="text-muted mb-0">See assigned parcels and update delivery status.</p>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 p-3">
            <h5>Administrators</h5>
            <p class="text-muted mb-0">Manage users, assign agents, and run reports.</p>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
