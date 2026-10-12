package com.courier.servlet;

import com.courier.dao.UserDao;
import com.courier.model.User;
import com.courier.util.Flash;
import com.courier.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            User user = userDao.findByUsername(username == null ? "" : username.trim());
            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                req.setAttribute("flashError", "Invalid username or password.");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            req.getSession(true).setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/app/dashboard");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
