<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card p-4">
            <h3 class="mb-3">Customer registration</h3>
            <form method="post" action="${pageContext.request.contextPath}/register">
                <div class="row g-3">
                    <div class="col-md-6"><label class="form-label">Full name</label><input class="form-control" name="fullName" required></div>
                    <div class="col-md-6"><label class="form-label">Username</label><input class="form-control" name="username" required></div>
                    <div class="col-md-6"><label class="form-label">Password</label><input class="form-control" type="password" name="password" required></div>
                    <div class="col-md-6"><label class="form-label">Email</label><input class="form-control" type="email" name="email"></div>
                    <div class="col-md-6"><label class="form-label">Phone</label><input class="form-control" name="phone"></div>
                    <div class="col-md-6"><label class="form-label">City</label><input class="form-control" name="city"></div>
                    <div class="col-12"><label class="form-label">Address</label><input class="form-control" name="address"></div>
                    <div class="col-md-4"><label class="form-label">Pincode</label><input class="form-control" name="pincode"></div>
                </div>
                <button class="btn btn-primary mt-3" type="submit">Create account</button>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
