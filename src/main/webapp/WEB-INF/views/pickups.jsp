<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Pickups" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Pickup scheduling</h2>
<div class="card p-3 mb-4">
    <h5>Schedule a pickup</h5>
    <form method="post" class="row g-3">
        <c:if test="${sessionScope.user.admin}">
            <div class="col-md-4">
                <label class="form-label">Customer</label>
                <select class="form-select" name="customerId" required>
                    <c:forEach items="${customers}" var="cust">
                        <option value="${cust.id}">${cust.user.fullName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-4"><label class="form-label">Sender name</label><input class="form-control" name="senderName" required></div>
        </c:if>
        <div class="col-md-4"><label class="form-label">Pickup date</label><input class="form-control" type="date" name="scheduledDate" required></div>
        <div class="col-md-4"><label class="form-label">Pickup time</label><input class="form-control" type="time" name="scheduledTime" required></div>
        <div class="col-md-8"><label class="form-label">Pickup address</label><input class="form-control" name="pickupAddress" required></div>
        <div class="col-md-4"><label class="form-label">Receiver name</label><input class="form-control" name="receiverName" required></div>
        <div class="col-md-8"><label class="form-label">Receiver address</label><input class="form-control" name="receiverAddress" required></div>
        <div class="col-md-4"><label class="form-label">Receiver phone</label><input class="form-control" name="receiverPhone"></div>
        <div class="col-md-2"><label class="form-label">Weight kg</label><input class="form-control" name="weightKg" value="1"></div>
        <div class="col-md-3"><label class="form-label">Type</label><input class="form-control" name="parcelType" value="Document"></div>
        <div class="col-12"><button class="btn btn-primary" type="submit">Schedule</button></div>
    </form>
</div>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>Tracking</th><th>Customer</th><th>Date</th><th>Time</th><th>Address</th><th>Status</th></tr></thead>
        <tbody>
        <c:forEach items="${pickups}" var="pk">
            <tr>
                <td>${pk.trackingNumber}</td>
                <td>${pk.customerName}</td>
                <td>${pk.scheduledDate}</td>
                <td>${pk.scheduledTime}</td>
                <td>${pk.pickupAddress}</td>
                <td>${pk.status}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
