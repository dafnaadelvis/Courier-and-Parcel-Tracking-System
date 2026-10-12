package com.courier.servlet;

import com.courier.dao.CustomerDao;
import com.courier.dao.UserDao;
import com.courier.model.Customer;
import com.courier.model.User;
import com.courier.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final CustomerDao customerDao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        try {
            if (username.isEmpty() || password == null || password.length() < 4) {
                req.setAttribute("flashError", "Username and a password of at least 4 characters are required.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }
            if (userDao.findByUsername(username) != null) {
                req.setAttribute("flashError", "Username already exists.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setRole("CUSTOMER");
            user.setFullName(trim(req.getParameter("fullName")));
            user.setEmail(trim(req.getParameter("email")));
            user.setPhone(trim(req.getParameter("phone")));
            userDao.insert(user);

            Customer customer = new Customer();
            customer.setUserId(user.getId());
            customer.setAddress(trim(req.getParameter("address")));
            customer.setCity(trim(req.getParameter("city")));
            customer.setPincode(trim(req.getParameter("pincode")));
            customerDao.insert(customer);

            req.getSession(true).setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/app/dashboard");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String trim(String v) {
        return v == null ? "" : v.trim();
    }
}
