package com.group19.servlet;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import com.group19.service.AuthService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Login/Registration servlet, handling user authentication and account
 * registration.
 *
 * <p>URL handled: /login
 *
 * <p>Supported operation modes:
 * <ul>
 *   <li>User login -- authentication for TA/MO roles</li>
 *   <li>Admin login -- ADMIN role login via the separate admin portal</li>
 *   <li>User registration -- account registration for TA/MO roles</li>
 * </ul>
 *
 * <p>Upon successful login, users are redirected to the role-specific home page:
 * <ul>
 *   <li>TA -> /ta/home</li>
 *   <li>MO -> /mo/home</li>
 *   <li>ADMIN -> /admin/home</li>
 * </ul>
 *
 * @author Group 19
 * @see AuthService
 */
public class LoginServlet extends HttpServlet {
    /** Administrator view identifier */
    private static final String VIEW_ADMIN = "ADMIN";
    /** TA role identifier */
    private static final String ROLE_TA = "TA";
    /** ADMIN role identifier */
    private static final String ROLE_ADMIN = "ADMIN";
    /** Login mode identifier */
    private static final String MODE_LOGIN = "LOGIN";
    /** Registration mode identifier */
    private static final String MODE_REGISTER = "REGISTER";

    private AuthService authService;

    /**
     * Initialises the Servlet, loads the user data file, and creates the AuthService
     * instance.
     */
    @Override
    public void init() {
        Path filePath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.authService = new AuthService(new UserAccountDao(filePath));
    }

    /**
     * Handles GET requests: displays the login/registration page.
     *
     * <p>If the user is already logged in, they are redirected directly to their
     * role-specific home page; otherwise the login page is displayed (normal user
     * login page or admin login page).
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");

        if (loginUser != null) {
            redirectByRole(req, resp, loginUser);
            return;
        }

        forwardToLoginPage(req, resp);
    }

    /**
     * Handles POST requests: performs login or registration.
     *
     * <p>The portal parameter distinguishes admin login from normal user
     * authentication. For normal users, the actionType parameter distinguishes
     * login (LOGIN) from registration (REGISTER).
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String portal = normalize(req.getParameter("portal"));
        if (VIEW_ADMIN.equals(portal)) {
            handleAdminLogin(req, resp);
            return;
        }

        handleUserAuth(req, resp);
    }

    /**
     * Handles login or registration requests for normal users (TA/MO).
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void handleUserAuth(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String selectedRole = normalizeUserRole(req.getParameter("role"));
        String actionType = normalizeMode(req.getParameter("actionType"));
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServiceResult<LoginUser> result;
        if (MODE_REGISTER.equals(actionType)) {
            req.setAttribute("displayName", req.getParameter("displayName"));
            req.setAttribute("userId", req.getParameter("userId"));
            result = authService.register(
                    selectedRole,
                    req.getParameter("displayName"),
                    req.getParameter("userId"),
                    username,
                    password,
                    req.getParameter("confirmPassword"));
        } else {
            result = authService.login(selectedRole, username, password);
        }

        if (!result.isSuccess()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("selectedRole", selectedRole == null ? ROLE_TA : selectedRole);
            req.setAttribute("selectedMode", actionType);
            req.setAttribute("adminView", false);
            forwardToLoginPage(req, resp);
            return;
        }

        if (MODE_REGISTER.equals(actionType)) {
            String role = selectedRole == null ? ROLE_TA : selectedRole;
            resp.sendRedirect(req.getContextPath() + "/login?role=" + role + "&registered=true");
            return;
        }

        createSession(req, result.getData());
        redirectByRole(req, resp, result.getData());
    }

    /**
     * Handles administrator login requests.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void handleAdminLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServiceResult<LoginUser> result = authService.login(ROLE_ADMIN, username, password);
        if (!result.isSuccess()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("adminView", true);
            forwardToLoginPage(req, resp);
            return;
        }

        createSession(req, result.getData());
        redirectByRole(req, resp, result.getData());
    }

    /**
     * Creates a user session, sets the login user attribute, with a session timeout
     * of 30 minutes.
     *
     * @param req       the HTTP request
     * @param loginUser the login user information
     */
    private void createSession(HttpServletRequest req, LoginUser loginUser) {
        HttpSession session = req.getSession(true);
        session.setAttribute("loginUser", loginUser);
        session.setMaxInactiveInterval(30 * 60);
    }

    /**
     * Redirects to the role-specific home page based on the user's role.
     *
     * @param req       the HTTP request
     * @param resp      the HTTP response
     * @param loginUser the login user
     * @throws IOException if an I/O error occurs
     */
    private void redirectByRole(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser) throws IOException {
        String role = loginUser == null ? null : loginUser.getRole();
        String target;
        if ("TA".equalsIgnoreCase(role)) {
            target = "/ta/home";
        } else if ("MO".equalsIgnoreCase(role)) {
            target = "/mo/home";
        } else if (ROLE_ADMIN.equalsIgnoreCase(role)) {
            target = "/admin/home";
        } else {
            target = "/home";
        }
        resp.sendRedirect(req.getContextPath() + target);
    }

    /**
     * Forwards to the login page, selecting a different JSP page depending on
     * whether it is the admin view.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void forwardToLoginPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        preparePageAttributes(req);
        String viewPath = isAdminView(req)
                ? "/WEB-INF/jsp/admin_login.jsp"
                : "/WEB-INF/jsp/login.jsp";
        req.getRequestDispatcher(viewPath).forward(req, resp);
    }

    /**
     * Prepares preset account information and default selection parameters for the
     * login page.
     *
     * @param req the HTTP request
     */
    private void preparePageAttributes(HttpServletRequest req) {
        boolean adminView = isAdminView(req);
        UserAccount taPreset = authService.getPresetAccount("TA");
        UserAccount moPreset = authService.getPresetAccount("MO");
        UserAccount adminPreset = authService.getPresetAccount(ROLE_ADMIN);

        req.setAttribute("adminView", adminView);
        setPresetAttributes(req, "ta", taPreset);
        setPresetAttributes(req, "mo", moPreset);
        setPresetAttributes(req, "admin", adminPreset);
        if (!adminView && "true".equalsIgnoreCase(req.getParameter("registered"))) {
            req.setAttribute("success", "Registration successful. Please sign in.");
        }

        if (!adminView) {
            if (req.getAttribute("selectedRole") == null) {
                req.setAttribute("selectedRole", defaultUserRole(req.getParameter("role")));
            }
            if (req.getAttribute("selectedMode") == null) {
                req.setAttribute("selectedMode", normalizeMode(req.getParameter("mode")));
            }
        }
    }

    /**
     * Determines whether the current request is for the admin view (judged by the
     * view/portal parameter or request attribute).
     *
     * @param req the HTTP request
     * @return true if it is the admin view
     */
    private boolean isAdminView(HttpServletRequest req) {
        Object attribute = req.getAttribute("adminView");
        if (attribute instanceof Boolean) {
            return (Boolean) attribute;
        }
        String view = normalize(req.getParameter("view"));
        String portal = normalize(req.getParameter("portal"));
        return VIEW_ADMIN.equals(view) || VIEW_ADMIN.equals(portal);
    }

    /**
     * Sets preset account information into request attributes (used for
     * demonstration/testing).
     *
     * @param req    the HTTP request
     * @param prefix the attribute name prefix
     * @param preset the preset account object
     */
    private void setPresetAttributes(HttpServletRequest req, String prefix, UserAccount preset) {
        req.setAttribute(prefix + "PresetUsername", preset == null ? "" : preset.getUsername());
        req.setAttribute(prefix + "PresetPassword", preset == null ? "" : preset.getPassword());
    }

    /**
     * Returns the default user role.
     *
     * @param role the role parameter
     * @return the normalised role string; defaults to TA
     */
    private String defaultUserRole(String role) {
        String normalizedRole = normalizeUserRole(role);
        return normalizedRole == null ? ROLE_TA : normalizedRole;
    }

    /**
     * Normalises the operation mode, accepting only REGISTER; others default to
     * LOGIN.
     *
     * @param mode the mode parameter
     * @return the normalised mode string
     */
    private static String normalizeMode(String mode) {
        String normalized = normalize(mode);
        if (MODE_REGISTER.equals(normalized)) {
            return MODE_REGISTER;
        }
        return MODE_LOGIN;
    }

    /**
     * Normalises the user role, accepting only TA or MO.
     *
     * @param role the role parameter
     * @return a valid role string (TA or MO), or null if invalid
     */
    private static String normalizeUserRole(String role) {
        String normalized = normalize(role);
        if ("TA".equals(normalized) || "MO".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    /**
     * Normalises the string: trims and converts to upper case; returns null if blank
     * or null.
     *
     * @param value the input string
     * @return the normalised string
     */
    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
