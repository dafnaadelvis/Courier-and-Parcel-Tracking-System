<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Parcels" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Parcels</h2>
<c:if test="${sessionScope.user.admin}">
<div class="card p-3 mb-4">
    <h5>${empty editParcel ? 'Add parcel' : 'Edit parcel'}</h5>
    <form method="post" class="row g-3">
        <c:if test="${not empty editParcel}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${editParcel.id}">
        </c:if>
        <c:if test="${empty editParcel}">
            <div class="col-md-4">
                <label class="form-label">Customer</label>
                <select class="form-select" name="customerId" required>
                    <c:forEach items="${customers}" var="cust">
                        <option value="${cust.id}">${cust.user.fullName}</option>
                    </c:forEach>
                </select>
            </div>
        </c:if>
        <div class="col-md-4"><label class="form-label">Sender name</label><input class="form-control" name="senderName" value="${editParcel.senderName}" required></div>
        <div class="col-md-8"><label class="form-label">Sender address</label><input class="form-control" name="senderAddress" value="${editParcel.senderAddress}" required></div>
        <div class="col-md-4"><label class="form-label">Receiver name</label><input class="form-control" name="receiverName" value="${editParcel.receiverName}" required></div>
        <div class="col-md-8"><label class="form-label">Receiver address</label><input class="form-control" name="receiverAddress" value="${editParcel.receiverAddress}" required></div>
        <div class="col-md-4"><label class="form-label">Receiver phone</label><input class="form-control" name="receiverPhone" value="${editParcel.receiverPhone}"></div>
        <div class="col-md-2"><label class="form-label">Weight kg</label><input class="form-control" name="weightKg" value="${editParcel.weightKg}"></div>
        <div class="col-md-3"><label class="form-label">Type</label><input class="form-control" name="parcelType" value="${editParcel.parcelType}"></div>
        <c:if test="${not empty editParcel}">
            <div class="col-md-3">
                <label class="form-label">Status</label>
                <input class="form-control" name="status" value="${editParcel.status}">
            </div>
        </c:if>
        <div class="col-12"><button class="btn btn-primary" type="submit">Save</button></div>
    </form>
</div>
</c:if>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>Tracking</th><th>Customer</th><th>Receiver</th><th>Status</th><th>Agent</th><c:if test="${sessionScope.user.admin}"><th></th></c:if></tr></thead>
        <tbody>
        <c:forEach items="${parcels}" var="p">
            <tr>
                <td><a href="${pageContext.request.contextPath}/app/tracking?number=${p.trackingNumber}">${p.trackingNumber}</a></td>
                <td>${p.customerName}</td>
                <td>${p.receiverName}</td>
                <td><span class="badge bg-secondary status-badge">${p.status}</span></td>
                <td>${p.assignedAgentName}</td>
                <c:if test="${sessionScope.user.admin}"><td><a href="?edit=${p.id}">Edit</a></td></c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
