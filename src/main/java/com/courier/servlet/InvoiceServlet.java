package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.InvoiceDao;
import com.courier.dao.ParcelDao;
import com.courier.model.Customer;
import com.courier.model.Invoice;
import com.courier.model.Parcel;
import com.courier.model.User;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/app/invoices")
public class InvoiceServlet extends HttpServlet {
    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final ParcelDao parcelDao = new ParcelDao();
    private final CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        User user = (User) req.getSession().getAttribute("user");
        try {
            String view = req.getParameter("id");
            if (view != null) {
                Invoice inv = invoiceDao.findById(Integer.parseInt(view));
                if (inv == null) {
                    Flash.error(req, "Invoice not found.");
                    resp.sendRedirect(req.getContextPath() + "/app/invoices");
                    return;
                }
                if (user.isCustomer()) {
                    Customer c = customerDao.findByUserId(user.getId());
                    Parcel p = parcelDao.findById(inv.getParcelId());
                    if (c == null || p == null || p.getCustomerId() != c.getId()) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }
                req.setAttribute("invoice", inv);
                req.setAttribute("parcel", parcelDao.findById(inv.getParcelId()));
                req.getRequestDispatcher("/WEB-INF/views/invoice-view.jsp").forward(req, resp);
                return;
            }
            if (user.isCustomer()) {
                Customer c = customerDao.findByUserId(user.getId());
                req.setAttribute("invoices", c == null ? java.util.List.of() : invoiceDao.findByCustomerId(c.getId()));
            } else if (user.isAdmin()) {
                req.setAttribute("invoices", invoiceDao.findAll());
                req.setAttribute("parcels", parcelDao.findAll());
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/invoices.jsp").forward(req, resp);
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
            int parcelId = Integer.parseInt(req.getParameter("parcelId"));
            if (invoiceDao.findByParcelId(parcelId) != null) {
                Flash.error(req, "Invoice already exists for this parcel.");
                resp.sendRedirect(req.getContextPath() + "/app/invoices");
                return;
            }
            Parcel parcel = parcelDao.findById(parcelId);
            BigDecimal weight = parcel.getWeightKg() == null ? BigDecimal.ONE : parcel.getWeightKg();
            BigDecimal amount = new BigDecimal("40").add(weight.multiply(new BigDecimal("50"))).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tax = amount.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            Invoice inv = new Invoice();
            inv.setParcelId(parcelId);
            inv.setAmount(amount);
            inv.setTax(tax);
            inv.setTotal(amount.add(tax));
            inv.setStatus("UNPAID");
            invoiceDao.insert(inv);
            Flash.set(req, "Invoice generated for " + parcel.getTrackingNumber());
            resp.sendRedirect(req.getContextPath() + "/app/invoices");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
