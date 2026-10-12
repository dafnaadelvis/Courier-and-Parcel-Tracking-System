<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2 class="mb-4">Dashboard</h2>
<c:if test="${sessionScope.user.admin}">
    <div class="row g-3 mb-4">
        <div class="col-md-4"><div class="card p-3"><div class="text-muted">Parcels</div><div class="fs-3">${parcelCount}</div></div></div>
        <div class="col-md-4"><div class="card p-3"><div class="text-muted">Pickups today</div><div class="fs-3">${todayPickups}</div></div></div>
        <div class="col-md-4"><div class="card p-3"><div class="text-muted">Invoice total</div><div class="fs-3">₹ ${invoiceTotal}</div></div></div>
    </div>
    <div class="card p-3">
        <h5>Parcels by status</h5>
        <ul class="mb-0">
            <c:forEach items="${statusCounts}" var="entry">
                <li>${entry.key}: ${entry.value}</li>
            </c:forEach>
            <c:if test="${empty statusCounts}"><li>No parcels yet.</li></c:if>
        </ul>
    </div>
</c:if>
<c:if test="${sessionScope.user.customer}">
    <p>Your recent parcels</p>
    <div class="table-responsive card">
        <table class="table mb-0">
            <thead><tr><th>Tracking</th><th>Receiver</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach items="${parcels}" var="p">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/app/tracking?number=${p.trackingNumber}">${p.trackingNumber}</a></td>
                    <td>${p.receiverName}</td>
                    <td><span class="badge bg-secondary status-badge">${p.status}</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>
<c:if test="${sessionScope.user.agent}">
    <p>Assigned deliveries</p>
    <div class="table-responsive card">
        <table class="table mb-0">
            <thead><tr><th>Tracking</th><th>Receiver</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach items="${parcels}" var="p">
                <tr>
                    <td>${p.trackingNumber}</td>
                    <td>${p.receiverName}</td>
                    <td><span class="badge bg-secondary status-badge">${p.status}</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <a class="btn btn-primary mt-3" href="${pageContext.request.contextPath}/app/deliveries">Update statuses</a>
</c:if>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
