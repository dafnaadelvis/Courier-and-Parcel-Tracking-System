<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delivery agents" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Delivery agents</h2>
<div class="card p-3 mb-4">
    <h5>${empty editAgent ? 'Add agent' : 'Edit agent'}</h5>
    <form method="post" class="row g-3">
        <c:if test="${not empty editAgent}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${editAgent.id}">
        </c:if>
        <c:if test="${empty editAgent}">
            <div class="col-md-4"><label class="form-label">Username</label><input class="form-control" name="username" required></div>
            <div class="col-md-4"><label class="form-label">Password</label><input class="form-control" type="password" name="password" required></div>
        </c:if>
        <div class="col-md-4"><label class="form-label">Full name</label><input class="form-control" name="fullName" value="${editAgent.user.fullName}" required></div>
        <div class="col-md-4"><label class="form-label">Email</label><input class="form-control" name="email" value="${editAgent.user.email}"></div>
        <div class="col-md-4"><label class="form-label">Phone</label><input class="form-control" name="phone" value="${editAgent.user.phone}"></div>
        <div class="col-md-4"><label class="form-label">Vehicle no</label><input class="form-control" name="vehicleNo" value="${editAgent.vehicleNo}"></div>
        <div class="col-md-4">
            <label class="form-label">Status</label>
            <select class="form-select" name="status">
                <option value="AVAILABLE" ${editAgent.status == 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                <option value="BUSY" ${editAgent.status == 'BUSY' ? 'selected' : ''}>BUSY</option>
                <option value="INACTIVE" ${editAgent.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
            </select>
        </div>
        <div class="col-12"><button class="btn btn-primary" type="submit">Save</button></div>
    </form>
</div>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>Name</th><th>Username</th><th>Vehicle</th><th>Status</th><th></th></tr></thead>
        <tbody>
        <c:forEach items="${agents}" var="a">
            <tr>
                <td>${a.user.fullName}</td>
                <td>${a.user.username}</td>
                <td>${a.vehicleNo}</td>
                <td>${a.status}</td>
                <td><a href="?edit=${a.id}">Edit</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
