<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Track shipment" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Track shipment</h2>
<form class="row g-2 mb-4" method="get">
    <div class="col-md-6"><input class="form-control" name="number" value="${param.number}" placeholder="Tracking number" required></div>
    <div class="col-auto"><button class="btn btn-primary" type="submit">Track</button></div>
</form>
<c:if test="${not empty parcel}">
    <div class="card p-3 mb-3">
        <h5>${parcel.trackingNumber}</h5>
        <p class="mb-1">From ${parcel.senderName} to ${parcel.receiverName}</p>
        <p class="mb-0">Current status: <span class="badge bg-primary status-badge">${parcel.status}</span></p>
    </div>
    <div class="table-responsive card">
        <table class="table mb-0">
            <thead><tr><th>When</th><th>Status</th><th>Location</th><th>Remarks</th><th>By</th></tr></thead>
            <tbody>
            <c:forEach items="${events}" var="e">
                <tr>
                    <td>${e.updatedAt}</td>
                    <td>${e.status}</td>
                    <td>${e.location}</td>
                    <td>${e.remarks}</td>
                    <td>${e.updatedByName}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
