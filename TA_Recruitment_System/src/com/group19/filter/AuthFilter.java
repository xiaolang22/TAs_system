package com.group19.filter;

import com.group19.model.LoginUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication filter that intercepts protected paths, verifying that the user is
 * logged in and has the appropriate role.
 *
 * <p>Filter logic:
 * <ol>
 *   <li>Check whether a loginUser exists in the session; if not, redirect to the login page.</li>
 *   <li>Verify the user's role against the current request path; return 403 if the role does not match.</li>
 *   <li>Allow the request through once verification passes.</li>
 * </ol>
 * </p>
 *
 * @author Group19
 * @since 1.0
 */
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        request.setAttribute("loginUser", loginUser);

        String servletPath = req.getServletPath();

        if ("/profile".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can access profile.");
            return;
        }

        if ("/ta/home".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can access TA home.");
            return;
        }

        if ("/mo/home".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access MO home.");
            return;
        }

        if ("/admin/home".equals(servletPath) && !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admin can access admin home.");
            return;
        }

        if ("/mo/review".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access candidate review.");
            return;
        }

        if ("/ta/upload-cv".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can upload CV.");
            return;
        }

        if ("/ta/account".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can access TA account center.");
            return;
        }

        if ("/mo/account".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access MO account center.");
            return;
        }

        if ("/admin/account".equals(servletPath) && !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admin can access admin account center.");
            return;
        }

        if ("/ta/applications".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can view application status.");
            return;
        }

        if ("/ta/saved-jobs".equals(servletPath) && !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only TA can save jobs.");
            return;
        }

        if ("/mo/post-job".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access post job.");
            return;
        }

        if ("/mo/jobs".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access job list.");
            return;
        }

        if ("/mo/applications".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can manage applications.");
            return;
        }

        if ("/mo/ta-profile".equals(servletPath) && !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can view TA profiles.");
            return;
        }

        if ("/admin/workload".equals(servletPath) && !"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admin can access workload dashboard.");
            return;
        }

        if ("/mo/workload".equals(servletPath)) {
            if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admin can access workload dashboard.");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/admin/workload");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
