package com.courier.servlet;

import com.courier.dao.ParcelDao;
import com.courier.dao.TrackingEventDao;
import com.courier.model.Parcel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/track")
public class PublicTrackServlet extends HttpServlet {
    private final ParcelDao parcelDao = new ParcelDao();
    private final TrackingEventDao eventDao = new TrackingEventDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("number");
        try {
            if (number != null && !number.isBlank()) {
                Parcel parcel = parcelDao.findByTrackingNumber(number.trim());
                if (parcel == null) {
                    req.setAttribute("flashError", "No parcel found for that tracking number.");
                } else {
                    req.setAttribute("parcel", parcel);
                    req.setAttribute("events", eventDao.findByParcelId(parcel.getId()));
                }
            }
            req.getRequestDispatcher("/WEB-INF/views/public-track.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
