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

public class LoginServlet extends HttpServlet {
    private static final String VIEW_ADMIN = "ADMIN";
    private static final String ROLE_TA = "TA";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String MODE_LOGIN = "LOGIN";
    private static final String MODE_REGISTER = "REGISTER";

    private AuthService authService;

    @Override
    public void init() {
        Path filePath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.authService = new AuthService(new UserAccountDao(filePath));
    }

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

    private void createSession(HttpServletRequest req, LoginUser loginUser) {
        HttpSession session = req.getSession(true);
        session.setAttribute("loginUser", loginUser);
        session.setMaxInactiveInterval(30 * 60);
    }

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

    private void forwardToLoginPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        preparePageAttributes(req);
        String viewPath = isAdminView(req)
                ? "/WEB-INF/jsp/admin_login.jsp"
                : "/WEB-INF/jsp/login.jsp";
        req.getRequestDispatcher(viewPath).forward(req, resp);
    }

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

    private boolean isAdminView(HttpServletRequest req) {
        Object attribute = req.getAttribute("adminView");
        if (attribute instanceof Boolean) {
            return (Boolean) attribute;
        }
        String view = normalize(req.getParameter("view"));
        String portal = normalize(req.getParameter("portal"));
        return VIEW_ADMIN.equals(view) || VIEW_ADMIN.equals(portal);
    }

    private void setPresetAttributes(HttpServletRequest req, String prefix, UserAccount preset) {
        req.setAttribute(prefix + "PresetUsername", preset == null ? "" : preset.getUsername());
        req.setAttribute(prefix + "PresetPassword", preset == null ? "" : preset.getPassword());
    }

    private String defaultUserRole(String role) {
        String normalizedRole = normalizeUserRole(role);
        return normalizedRole == null ? ROLE_TA : normalizedRole;
    }

    private static String normalizeMode(String mode) {
        String normalized = normalize(mode);
        if (MODE_REGISTER.equals(normalized)) {
            return MODE_REGISTER;
        }
        return MODE_LOGIN;
    }

    private static String normalizeUserRole(String role) {
        String normalized = normalize(role);
        if ("TA".equals(normalized) || "MO".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
