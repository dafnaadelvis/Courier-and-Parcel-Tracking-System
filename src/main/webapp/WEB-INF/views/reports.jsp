<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Reports</h2>
<div class="row g-3 mb-4">
    <div class="col-md-4"><div class="card p-3"><div class="text-muted">Pickups today</div><div class="fs-3">${todayPickups}</div></div></div>
    <div class="col-md-4"><div class="card p-3"><div class="text-muted">Invoice totals</div><div class="fs-3">₹ ${invoiceTotal}</div></div></div>
</div>
<div class="card p-3 mb-4">
    <h5>Parcels by status</h5>
    <ul class="mb-0">
        <c:forEach items="${statusCounts}" var="entry">
            <li>${entry.key}: ${entry.value}</li>
        </c:forEach>
    </ul>
</div>
<div class="table-responsive card mb-4">
    <table class="table mb-0">
        <thead><tr><th>Tracking</th><th>Status</th><th>Customer</th></tr></thead>
        <tbody>
        <c:forEach items="${parcels}" var="p">
            <tr><td>${p.trackingNumber}</td><td>${p.status}</td><td>${p.customerName}</td></tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
