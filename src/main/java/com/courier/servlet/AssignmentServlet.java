package com.courier.servlet;

import com.courier.dao.AssignmentDao;
import com.courier.dao.DeliveryAgentDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.Assignment;
import com.courier.model.TrackingEvent;
import com.courier.model.User;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/app/assign")
public class AssignmentServlet extends HttpServlet {
    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final ParcelDao parcelDao = new ParcelDao();
    private final DeliveryAgentDao agentDao = new DeliveryAgentDao();
    private final TrackingEventDao eventDao = new TrackingEventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        try {
            req.setAttribute("unassigned", parcelDao.findUnassigned());
            req.setAttribute("agents", agentDao.findAll());
            req.setAttribute("assignments", assignmentDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/assign.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            int parcelId = Integer.parseInt(req.getParameter("parcelId"));
            int agentId = Integer.parseInt(req.getParameter("agentId"));
            if (assignmentDao.existsForParcel(parcelId)) {
                Flash.error(req, "This parcel already has an agent.");
            } else {
                Assignment a = new Assignment();
                a.setParcelId(parcelId);
                a.setAgentId(agentId);
                assignmentDao.insert(a);
                parcelDao.updateStatus(parcelId, "IN_TRANSIT");
                TrackingEvent ev = new TrackingEvent();
                ev.setParcelId(parcelId);
                ev.setStatus("IN_TRANSIT");
                ev.setLocation("Hub");
                ev.setRemarks("Delivery agent assigned");
                ev.setUpdatedBy(user.getId());
                eventDao.insert(ev);
                Flash.set(req, "Agent assigned.");
            }
            resp.sendRedirect(req.getContextPath() + "/app/assign");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
