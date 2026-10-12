<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Invoices" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Invoices</h2>
<c:if test="${sessionScope.user.admin}">
<div class="card p-3 mb-4">
    <form method="post" class="row g-3 align-items-end">
        <div class="col-md-8">
            <label class="form-label">Generate for parcel</label>
            <select class="form-select" name="parcelId" required>
                <c:forEach items="${parcels}" var="p">
                    <option value="${p.id}">${p.trackingNumber} — ${p.customerName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-4"><button class="btn btn-primary" type="submit">Generate invoice</button></div>
    </form>
    <p class="small text-muted mb-0 mt-2">Charge = ₹40 + ₹50 per kg, plus 18% GST.</p>
</div>
</c:if>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>ID</th><th>Tracking</th><th>Customer</th><th>Total</th><th>Status</th><th></th></tr></thead>
        <tbody>
        <c:forEach items="${invoices}" var="inv">
            <tr>
                <td>${inv.id}</td>
                <td>${inv.trackingNumber}</td>
                <td>${inv.customerName}</td>
                <td>₹ ${inv.total}</td>
                <td>${inv.status}</td>
                <td><a href="?id=${inv.id}">View</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
