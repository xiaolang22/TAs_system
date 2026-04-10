package com.group19.servlet;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("userDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/users.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
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

        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServiceResult<LoginUser> result = authService.login(username, password);
        if (!result.isSuccess()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        LoginUser loginUser = result.getData();
        HttpSession session = req.getSession(true);
        session.setAttribute("loginUser", loginUser);
        session.setMaxInactiveInterval(30 * 60);

        redirectByRole(req, resp, loginUser);
    }

    private void redirectByRole(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser) throws IOException {
        String role = loginUser.getRole();

        if ("MO".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/mo/post-job");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "users.json");
    }
}