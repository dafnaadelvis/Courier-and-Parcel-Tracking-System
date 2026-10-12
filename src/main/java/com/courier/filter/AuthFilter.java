package com.courier.filter;

import com.courier.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter("/app/*")
public class AuthFilter extends HttpFilter {

    private static final Set<String> ADMIN_PREFIXES = Set.of(
            "/app/customers",
            "/app/agents",
            "/app/users",
            "/app/assign",
            "/app/reports"
    );

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        if (startsWithAny(path, ADMIN_PREFIXES) && !user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (path.startsWith("/app/deliveries") && !user.isAgent() && !user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        req.setAttribute("currentUser", user);
        chain.doFilter(req, res);
    }

    private boolean startsWithAny(String path, Set<String> prefixes) {
        for (String p : prefixes) {
            if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
