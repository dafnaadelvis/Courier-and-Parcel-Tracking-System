package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.UserDao;
import com.courier.model.Customer;
import com.courier.model.User;
import com.courier.util.Flash;
import com.courier.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/app/customers")
public class CustomerServlet extends HttpServlet {
    private final CustomerDao customerDao = new CustomerDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        try {
            String edit = req.getParameter("edit");
            if (edit != null) {
                req.setAttribute("editCustomer", customerDao.findById(Integer.parseInt(edit)));
            }
            req.setAttribute("customers", customerDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/customers.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("update".equals(action)) {
                Customer c = customerDao.findById(Integer.parseInt(req.getParameter("id")));
                c.setAddress(req.getParameter("address"));
                c.setCity(req.getParameter("city"));
                c.setPincode(req.getParameter("pincode"));
                customerDao.update(c);
                User u = userDao.findById(c.getUserId());
                u.setFullName(req.getParameter("fullName"));
                u.setEmail(req.getParameter("email"));
                u.setPhone(req.getParameter("phone"));
                userDao.update(u);
                Flash.set(req, "Customer updated.");
            } else {
                if (userDao.findByUsername(req.getParameter("username")) != null) {
                    Flash.error(req, "Username already exists.");
                    resp.sendRedirect(req.getContextPath() + "/app/customers");
                    return;
                }
                User u = new User();
                u.setUsername(req.getParameter("username"));
                u.setPasswordHash(PasswordUtil.hash(req.getParameter("password")));
                u.setRole("CUSTOMER");
                u.setFullName(req.getParameter("fullName"));
                u.setEmail(req.getParameter("email"));
                u.setPhone(req.getParameter("phone"));
                userDao.insert(u);
                Customer c = new Customer();
                c.setUserId(u.getId());
                c.setAddress(req.getParameter("address"));
                c.setCity(req.getParameter("city"));
                c.setPincode(req.getParameter("pincode"));
                customerDao.insert(c);
                Flash.set(req, "Customer created.");
            }
            resp.sendRedirect(req.getContextPath() + "/app/customers");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
