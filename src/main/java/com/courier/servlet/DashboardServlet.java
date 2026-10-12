package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.DeliveryAgentDao;
import com.courier.dao.InvoiceDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.PickupDao;
import com.courier.model.Customer;
import com.courier.model.DeliveryAgent;
import com.courier.model.User;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ParcelDao parcelDao = new ParcelDao();
    private final PickupDao pickupDao = new PickupDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final DeliveryAgentDao agentDao = new DeliveryAgentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        User user = (User) req.getSession().getAttribute("user");
        try {
            if (user.isAdmin()) {
                req.setAttribute("statusCounts", parcelDao.countByStatus());
                req.setAttribute("todayPickups", pickupDao.countByDate(LocalDate.now()));
                req.setAttribute("invoiceTotal", invoiceDao.sumTotals());
                req.setAttribute("parcelCount", parcelDao.findAll().size());
            } else if (user.isCustomer()) {
                Customer c = customerDao.findByUserId(user.getId());
                if (c != null) {
                    req.setAttribute("parcels", parcelDao.findByCustomerId(c.getId()));
                }
            } else if (user.isAgent()) {
                DeliveryAgent a = agentDao.findByUserId(user.getId());
                if (a != null) {
                    req.setAttribute("parcels", parcelDao.findAssignedToAgent(a.getId()));
                }
            }
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
