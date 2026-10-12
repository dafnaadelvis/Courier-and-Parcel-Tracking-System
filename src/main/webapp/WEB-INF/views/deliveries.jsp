<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Deliveries" scope="request"/>
<jsp:include page="/WEB-INF/views/layout-start.jsp"/>
<h2>Assigned deliveries</h2>
<div class="table-responsive card">
    <table class="table align-middle mb-0">
        <thead><tr><th>Tracking</th><th>Receiver</th><th>Status</th><th>Update</th></tr></thead>
        <tbody>
        <c:forEach items="${parcels}" var="p">
            <tr>
                <td>${p.trackingNumber}</td>
                <td>${p.receiverName}<br><small>${p.receiverAddress}</small></td>
                <td><span class="badge bg-secondary status-badge">${p.status}</span></td>
                <td>
                    <form method="post" class="row g-1">
                        <input type="hidden" name="parcelId" value="${p.id}">
                        <div class="col-md-4">
                            <select class="form-select form-select-sm" name="status">
                                <option>PICKED_UP</option>
                                <option>IN_TRANSIT</option>
                                <option>OUT_FOR_DELIVERY</option>
                                <option>DELIVERED</option>
                            </select>
                        </div>
                        <div class="col-md-3"><input class="form-control form-control-sm" name="location" placeholder="Location"></div>
                        <div class="col-md-3"><input class="form-control form-control-sm" name="remarks" placeholder="Remarks"></div>
                        <div class="col-md-2"><button class="btn btn-sm btn-primary w-100" type="submit">Save</button></div>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/layout-end.jsp"/>
