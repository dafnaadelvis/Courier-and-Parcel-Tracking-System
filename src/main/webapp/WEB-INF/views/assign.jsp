<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Assign agents" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Assign delivery agents</h2>
<div class="card p-3 mb-4">
    <form method="post" class="row g-3 align-items-end">
        <div class="col-md-5">
            <label class="form-label">Unassigned parcel</label>
            <select class="form-select" name="parcelId" required>
                <c:forEach items="${unassigned}" var="p">
                    <option value="${p.id}">${p.trackingNumber} — ${p.receiverName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-5">
            <label class="form-label">Agent</label>
            <select class="form-select" name="agentId" required>
                <c:forEach items="${agents}" var="a">
                    <option value="${a.id}">${a.user.fullName} (${a.status})</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2"><button class="btn btn-primary w-100" type="submit">Assign</button></div>
    </form>
</div>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>Tracking</th><th>Agent</th><th>Assigned at</th></tr></thead>
        <tbody>
        <c:forEach items="${assignments}" var="asg">
            <tr>
                <td>${asg.trackingNumber}</td>
                <td>${asg.agentName}</td>
                <td>${asg.assignedAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
