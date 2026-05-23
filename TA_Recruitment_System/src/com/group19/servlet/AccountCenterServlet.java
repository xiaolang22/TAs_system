package com.group19.servlet;

import com.group19.dao.UserAccountDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import com.group19.service.AccountCenterService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@MultipartConfig
public class AccountCenterServlet extends HttpServlet {
    private AccountCenterService accountCenterService;

    @Override
    public void init() {
        Path userDataPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.accountCenterService = new AccountCenterService(new UserAccountDao(userDataPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ServiceResult<UserAccount> result = accountCenterService.loadAccount(loginUser.getUserId());
        if (result.isSuccess()) {
            req.setAttribute("account", result.getData());
            req.setAttribute("avatarPreviewUrl", buildAvatarPreviewUrl(req, result.getData().getAvatarPath()));
            req.setAttribute("avatarInitial", buildAvatarInitial(result.getData().getDisplayName()));
        } else {
            req.setAttribute("error", result.getMessage());
        }
        req.setAttribute("loginUser", loginUser);
        if ("true".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "个人中心信息已更新。");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/account_center.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Part avatarPart;
        try {
            avatarPart = req.getPart("avatarFile");
        } catch (IllegalStateException e) {
            avatarPart = null;
        }

        ServiceResult<LoginUser> result = accountCenterService.updateAccount(
                loginUser.getUserId(),
                req.getParameter("displayName"),
                req.getParameter("username"),
                req.getParameter("newPassword"),
                req.getParameter("confirmPassword"),
                avatarPart,
                resolveAvatarUploadDir());

        if (result.isSuccess()) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.setAttribute("loginUser", result.getData());
            }
            resp.sendRedirect(req.getContextPath() + "/ta/account?updated=true");
            return;
        }

        ServiceResult<UserAccount> accountResult = accountCenterService.loadAccount(loginUser.getUserId());
        UserAccount account = accountResult.isSuccess() ? accountResult.getData() : new UserAccount();
        account.setDisplayName(req.getParameter("displayName"));
        account.setUsername(req.getParameter("username"));
        account.setUserId(loginUser.getUserId());
        account.setRole(loginUser.getRole());
        account.setAvatarPath(loginUser.getAvatarPath());

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("account", account);
        req.setAttribute("avatarPreviewUrl", buildAvatarPreviewUrl(req, account.getAvatarPath()));
        req.setAttribute("avatarInitial", buildAvatarInitial(account.getDisplayName()));
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/account_center.jsp").forward(req, resp);
    }

    private LoginUser currentLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    private Path resolveAvatarUploadDir() {
        String realPath = getServletContext().getRealPath("/uploads/avatars");
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "uploads", "avatars");
    }

    private String buildAvatarPreviewUrl(HttpServletRequest req, String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            return "";
        }
        return req.getContextPath() + avatarPath.trim();
    }

    private String buildAvatarInitial(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return "TA";
        }
        return displayName.trim().substring(0, 1);
    }
}
