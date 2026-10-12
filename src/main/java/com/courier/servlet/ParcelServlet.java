package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.Customer;
import com.courier.model.Parcel;
import com.courier.model.TrackingEvent;
import com.courier.model.User;
import com.courier.util.Flash;
import com.courier.util.TrackingNumber;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/app/parcels")
public class ParcelServlet extends HttpServlet {
    private final ParcelDao parcelDao = new ParcelDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final TrackingEventDao eventDao = new TrackingEventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        User user = (User) req.getSession().getAttribute("user");
        try {
            if (user.isCustomer()) {
                Customer c = customerDao.findByUserId(user.getId());
                req.setAttribute("parcels", c == null ? java.util.List.of() : parcelDao.findByCustomerId(c.getId()));
            } else if (user.isAdmin()) {
                req.setAttribute("parcels", parcelDao.findAll());
                req.setAttribute("customers", customerDao.findAll());
                String edit = req.getParameter("edit");
                if (edit != null) {
                    req.setAttribute("editParcel", parcelDao.findById(Integer.parseInt(edit)));
                }
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/parcels.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (!user.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            String action = req.getParameter("action");
            if ("update".equals(action)) {
                Parcel p = parcelDao.findById(Integer.parseInt(req.getParameter("id")));
                fill(p, req);
                parcelDao.update(p);
                Flash.set(req, "Parcel updated.");
            } else {
                Parcel p = new Parcel();
                p.setTrackingNumber(TrackingNumber.generate());
                p.setCustomerId(Integer.parseInt(req.getParameter("customerId")));
                fill(p, req);
                p.setStatus("CREATED");
                parcelDao.insert(p);
                TrackingEvent ev = new TrackingEvent();
                ev.setParcelId(p.getId());
                ev.setStatus("CREATED");
                ev.setLocation("Origin");
                ev.setRemarks("Parcel registered");
                ev.setUpdatedBy(user.getId());
                eventDao.insert(ev);
                Flash.set(req, "Parcel created. Tracking number: " + p.getTrackingNumber());
            }
            resp.sendRedirect(req.getContextPath() + "/app/parcels");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void fill(Parcel p, HttpServletRequest req) {
        p.setSenderName(req.getParameter("senderName"));
        p.setSenderAddress(req.getParameter("senderAddress"));
        p.setReceiverName(req.getParameter("receiverName"));
        p.setReceiverAddress(req.getParameter("receiverAddress"));
        p.setReceiverPhone(req.getParameter("receiverPhone"));
        String w = req.getParameter("weightKg");
        p.setWeightKg(w == null || w.isBlank() ? BigDecimal.ZERO : new BigDecimal(w));
        p.setParcelType(req.getParameter("parcelType"));
        if (req.getParameter("status") != null) {
            p.setStatus(req.getParameter("status"));
        }
    }
}
