package com.courier.servlet;

import com.courier.dao.InvoiceDao;
import com.courier.dao.ParcelDao;
import com.courier.dao.PickupDao;
import com.courier.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/app/reports")
public class ReportServlet extends HttpServlet {
    private final ParcelDao parcelDao = new ParcelDao();
    private final PickupDao pickupDao = new PickupDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        try {
            req.setAttribute("statusCounts", parcelDao.countByStatus());
            req.setAttribute("parcels", parcelDao.findAll());
            req.setAttribute("todayPickups", pickupDao.countByDate(LocalDate.now()));
            req.setAttribute("pickups", pickupDao.findAll());
            req.setAttribute("invoiceTotal", invoiceDao.sumTotals());
            req.setAttribute("invoices", invoiceDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
