<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Customers" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Customers</h2>
<div class="card p-3 mb-4">
    <h5>${empty editCustomer ? 'Add customer' : 'Edit customer'}</h5>
    <form method="post" class="row g-3">
        <c:if test="${not empty editCustomer}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${editCustomer.id}">
        </c:if>
        <c:if test="${empty editCustomer}">
            <div class="col-md-4"><label class="form-label">Username</label><input class="form-control" name="username" required></div>
            <div class="col-md-4"><label class="form-label">Password</label><input class="form-control" type="password" name="password" required></div>
        </c:if>
        <div class="col-md-4"><label class="form-label">Full name</label><input class="form-control" name="fullName" value="${editCustomer.user.fullName}" required></div>
        <div class="col-md-4"><label class="form-label">Email</label><input class="form-control" name="email" value="${editCustomer.user.email}"></div>
        <div class="col-md-4"><label class="form-label">Phone</label><input class="form-control" name="phone" value="${editCustomer.user.phone}"></div>
        <div class="col-md-4"><label class="form-label">City</label><input class="form-control" name="city" value="${editCustomer.city}"></div>
        <div class="col-md-8"><label class="form-label">Address</label><input class="form-control" name="address" value="${editCustomer.address}"></div>
        <div class="col-md-4"><label class="form-label">Pincode</label><input class="form-control" name="pincode" value="${editCustomer.pincode}"></div>
        <div class="col-12"><button class="btn btn-primary" type="submit">Save</button></div>
    </form>
</div>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>Name</th><th>Username</th><th>City</th><th>Phone</th><th></th></tr></thead>
        <tbody>
        <c:forEach items="${customers}" var="c">
            <tr>
                <td>${c.user.fullName}</td>
                <td>${c.user.username}</td>
                <td>${c.city}</td>
                <td>${c.user.phone}</td>
                <td><a href="?edit=${c.id}">Edit</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
