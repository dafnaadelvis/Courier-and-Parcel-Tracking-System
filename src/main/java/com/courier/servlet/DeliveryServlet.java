package com.courier.servlet;

import com.courier.dao.DeliveryAgentDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.DeliveryAgent;
import com.courier.model.Parcel;
import com.courier.model.TrackingEvent;
import com.courier.model.User;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet("/app/deliveries")
public class DeliveryServlet extends HttpServlet {
    private static final Set<String> ALLOWED = Set.of(
            "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED");

    private final ParcelDao parcelDao = new ParcelDao();
    private final DeliveryAgentDao agentDao = new DeliveryAgentDao();
    private final TrackingEventDao eventDao = new TrackingEventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        User user = (User) req.getSession().getAttribute("user");
        try {
            DeliveryAgent agent = agentDao.findByUserId(user.getId());
            if (agent == null && !user.isAdmin()) {
                Flash.error(req, "No agent profile linked to this account.");
                req.setAttribute("parcels", java.util.List.of());
            } else if (user.isAdmin() && agent == null) {
                req.setAttribute("parcels", parcelDao.findAll());
            } else {
                req.setAttribute("parcels", parcelDao.findAssignedToAgent(agent.getId()));
            }
            req.getRequestDispatcher("/WEB-INF/views/deliveries.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            int parcelId = Integer.parseInt(req.getParameter("parcelId"));
            String status = req.getParameter("status");
            if (!ALLOWED.contains(status)) {
                Flash.error(req, "Invalid status.");
                resp.sendRedirect(req.getContextPath() + "/app/deliveries");
                return;
            }
            Parcel parcel = parcelDao.findById(parcelId);
            if (parcel == null) {
                Flash.error(req, "Parcel not found.");
                resp.sendRedirect(req.getContextPath() + "/app/deliveries");
                return;
            }
            if (user.isAgent()) {
                DeliveryAgent agent = agentDao.findByUserId(user.getId());
                boolean assigned = parcelDao.findAssignedToAgent(agent.getId()).stream()
                        .anyMatch(p -> p.getId() == parcelId);
                if (!assigned) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            parcelDao.updateStatus(parcelId, status);
            TrackingEvent ev = new TrackingEvent();
            ev.setParcelId(parcelId);
            ev.setStatus(status);
            ev.setLocation(req.getParameter("location"));
            ev.setRemarks(req.getParameter("remarks"));
            ev.setUpdatedBy(user.getId());
            eventDao.insert(ev);
            Flash.set(req, "Status updated to " + status + ".");
            resp.sendRedirect(req.getContextPath() + "/app/deliveries");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
