package com.group19.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Logout servlet, handling user logout operations.
 *
 * <p>URL handled: /logout
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Obtain the current session</li>
 *   <li>Invalidate the session</li>
 *   <li>Redirect to the login page</li>
 * </ol>
 *
 * <p>Supports both GET and POST requests; GET delegates internally to POST
 * handling.
 *
 * @author Group 19
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Handles GET requests, delegating to {@link #doPost} for logout execution.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * Handles POST requests: performs the logout operation by invalidating the
     * current session and redirecting to the login page.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
