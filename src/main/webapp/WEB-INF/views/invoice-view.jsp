<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Invoice ${invoice.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<div class="card p-4">
    <h3>Invoice #${invoice.id}</h3>
    <p class="text-muted">Generated ${invoice.generatedAt}</p>
    <table class="table">
        <tr><th>Tracking number</th><td>${invoice.trackingNumber}</td></tr>
        <tr><th>Customer</th><td>${invoice.customerName}</td></tr>
        <tr><th>Receiver</th><td>${parcel.receiverName}</td></tr>
        <tr><th>Weight</th><td>${parcel.weightKg} kg</td></tr>
        <tr><th>Amount</th><td>₹ ${invoice.amount}</td></tr>
        <tr><th>GST (18%)</th><td>₹ ${invoice.tax}</td></tr>
        <tr><th>Total</th><td><strong>₹ ${invoice.total}</strong></td></tr>
        <tr><th>Status</th><td>${invoice.status}</td></tr>
    </table>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/app/invoices">Back</a>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
