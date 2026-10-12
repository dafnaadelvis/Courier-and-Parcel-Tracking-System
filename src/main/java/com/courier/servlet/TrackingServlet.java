package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.Customer;
import com.courier.model.Parcel;
import com.courier.model.User;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/app/tracking")
public class TrackingServlet extends HttpServlet {
    private final ParcelDao parcelDao = new ParcelDao();
    private final TrackingEventDao eventDao = new TrackingEventDao();
    private final CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        String number = req.getParameter("number");
        User user = (User) req.getSession().getAttribute("user");
        try {
            if (number != null && !number.isBlank()) {
                Parcel parcel = parcelDao.findByTrackingNumber(number.trim());
                if (parcel == null) {
                    req.setAttribute("flashError", "No parcel found for that tracking number.");
                } else if (user.isCustomer()) {
                    Customer c = customerDao.findByUserId(user.getId());
                    if (c == null || c.getId() != parcel.getCustomerId()) {
                        req.setAttribute("flashError", "That shipment does not belong to your account.");
                    } else {
                        req.setAttribute("parcel", parcel);
                        req.setAttribute("events", eventDao.findByParcelId(parcel.getId()));
                    }
                } else {
                    req.setAttribute("parcel", parcel);
                    req.setAttribute("events", eventDao.findByParcelId(parcel.getId()));
                }
            }
            req.getRequestDispatcher("/WEB-INF/views/tracking.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
