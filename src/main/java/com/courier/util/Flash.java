package com.courier.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class Flash {
    private Flash() {
    }

    public static void set(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }

    public static void error(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    public static void consume(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object ok = session.getAttribute("flash");
        Object err = session.getAttribute("flashError");
        if (ok != null) {
            req.setAttribute("flash", ok);
            session.removeAttribute("flash");
        }
        if (err != null) {
            req.setAttribute("flashError", err);
            session.removeAttribute("flashError");
        }
    }
}
