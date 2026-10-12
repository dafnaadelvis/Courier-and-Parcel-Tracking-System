package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.PickupDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.Customer;
import com.courier.model.Parcel;
import com.courier.model.Pickup;
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
import java.time.LocalDate;
import java.time.LocalTime;

@WebServlet("/app/pickups")
public class PickupServlet extends HttpServlet {
    private final PickupDao pickupDao = new PickupDao();
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
                req.setAttribute("pickups", c == null ? java.util.List.of() : pickupDao.findByCustomerId(c.getId()));
            } else if (user.isAdmin()) {
                req.setAttribute("pickups", pickupDao.findAll());
                req.setAttribute("customers", customerDao.findAll());
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/pickups.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (!user.isCustomer() && !user.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            Customer customer;
            if (user.isCustomer()) {
                customer = customerDao.findByUserId(user.getId());
            } else {
                customer = customerDao.findById(Integer.parseInt(req.getParameter("customerId")));
            }
            if (customer == null) {
                Flash.error(req, "Customer profile not found.");
                resp.sendRedirect(req.getContextPath() + "/app/pickups");
                return;
            }

            Parcel p = new Parcel();
            p.setTrackingNumber(TrackingNumber.generate());
            p.setCustomerId(customer.getId());
            p.setSenderName(user.isCustomer() ? user.getFullName() : req.getParameter("senderName"));
            p.setSenderAddress(req.getParameter("pickupAddress"));
            p.setReceiverName(req.getParameter("receiverName"));
            p.setReceiverAddress(req.getParameter("receiverAddress"));
            p.setReceiverPhone(req.getParameter("receiverPhone"));
            String w = req.getParameter("weightKg");
            p.setWeightKg(w == null || w.isBlank() ? BigDecimal.ONE : new BigDecimal(w));
            p.setParcelType(req.getParameter("parcelType"));
            p.setStatus("PICKUP_SCHEDULED");
            parcelDao.insert(p);

            Pickup pickup = new Pickup();
            pickup.setParcelId(p.getId());
            pickup.setScheduledDate(LocalDate.parse(req.getParameter("scheduledDate")));
            pickup.setScheduledTime(LocalTime.parse(req.getParameter("scheduledTime")));
            pickup.setPickupAddress(req.getParameter("pickupAddress"));
            pickup.setStatus("SCHEDULED");
            pickupDao.insert(pickup);

            TrackingEvent ev = new TrackingEvent();
            ev.setParcelId(p.getId());
            ev.setStatus("PICKUP_SCHEDULED");
            ev.setLocation(req.getParameter("pickupAddress"));
            ev.setRemarks("Pickup scheduled");
            ev.setUpdatedBy(user.getId());
            eventDao.insert(ev);

            Flash.set(req, "Pickup scheduled. Tracking number: " + p.getTrackingNumber());
            resp.sendRedirect(req.getContextPath() + "/app/pickups");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
