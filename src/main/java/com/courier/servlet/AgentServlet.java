package com.courier.servlet;

import com.courier.dao.DeliveryAgentDao;
import com.courier.dao.UserDao;
import com.courier.model.DeliveryAgent;
import com.courier.model.User;
import com.courier.util.Flash;
import com.courier.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/app/agents")
public class AgentServlet extends HttpServlet {
    private final DeliveryAgentDao agentDao = new DeliveryAgentDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.consume(req);
        try {
            String edit = req.getParameter("edit");
            if (edit != null) {
                req.setAttribute("editAgent", agentDao.findById(Integer.parseInt(edit)));
            }
            req.setAttribute("agents", agentDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/agents.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("update".equals(action)) {
                DeliveryAgent a = agentDao.findById(Integer.parseInt(req.getParameter("id")));
                a.setVehicleNo(req.getParameter("vehicleNo"));
                a.setStatus(req.getParameter("status"));
                agentDao.update(a);
                User u = userDao.findById(a.getUserId());
                u.setFullName(req.getParameter("fullName"));
                u.setEmail(req.getParameter("email"));
                u.setPhone(req.getParameter("phone"));
                userDao.update(u);
                Flash.set(req, "Agent updated.");
            } else {
                if (userDao.findByUsername(req.getParameter("username")) != null) {
                    Flash.error(req, "Username already exists.");
                    resp.sendRedirect(req.getContextPath() + "/app/agents");
                    return;
                }
                User u = new User();
                u.setUsername(req.getParameter("username"));
                u.setPasswordHash(PasswordUtil.hash(req.getParameter("password")));
                u.setRole("AGENT");
                u.setFullName(req.getParameter("fullName"));
                u.setEmail(req.getParameter("email"));
                u.setPhone(req.getParameter("phone"));
                userDao.insert(u);
                DeliveryAgent a = new DeliveryAgent();
                a.setUserId(u.getId());
                a.setVehicleNo(req.getParameter("vehicleNo"));
                a.setStatus(req.getParameter("status"));
                agentDao.insert(a);
                Flash.set(req, "Delivery agent created.");
            }
            resp.sendRedirect(req.getContextPath() + "/app/agents");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
