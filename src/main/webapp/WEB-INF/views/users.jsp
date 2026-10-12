<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Users" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Users</h2>
<div class="table-responsive card">
    <table class="table mb-0">
        <thead><tr><th>ID</th><th>Username</th><th>Name</th><th>Role</th><th>Email</th><th>Phone</th></tr></thead>
        <tbody>
        <c:forEach items="${users}" var="u">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.fullName}</td>
                <td>${u.role}</td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
