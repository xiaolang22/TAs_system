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

        populateAccountPage(req, loginUser);
        if ("profile".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "个人信息已更新。");
        } else if ("password".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "密码已更新。");
        } else if ("avatar".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "头像已更新。");
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

        String action = trimToEmpty(req.getParameter("action"));
        ServiceResult<LoginUser> result;

        switch (action) {
            case "profile":
                result = accountCenterService.updateProfile(
                        loginUser.getUserId(),
                        req.getParameter("displayName"),
                        req.getParameter("username"));
                break;
            case "password":
                result = accountCenterService.updatePassword(
                        loginUser.getUserId(),
                        req.getParameter("newPassword"),
                        req.getParameter("confirmPassword"));
                break;
            case "avatar":
                Part avatarPart;
                try {
                    avatarPart = req.getPart("avatarFile");
                } catch (IllegalStateException e) {
                    avatarPart = null;
                }
                result = accountCenterService.updateAvatar(loginUser.getUserId(), avatarPart, resolveAvatarUploadDir());
                break;
            default:
                result = ServiceResult.failure("无法识别当前操作。");
                break;
        }

        if (result.isSuccess()) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.setAttribute("loginUser", result.getData());
            }
            resp.sendRedirect(req.getContextPath() + "/ta/account?updated=" + action);
            return;
        }

        LoginUser latestLoginUser = currentLoginUser(req);
        if ("profile".equals(action)) {
            populateAccountPage(req, latestLoginUser);
            UserAccount account = (UserAccount) req.getAttribute("account");
            if (account != null) {
                account.setDisplayName(trimToEmpty(req.getParameter("displayName")));
                account.setUsername(trimToEmpty(req.getParameter("username")));
            }
            req.setAttribute("avatarInitial", buildAvatarInitial(account == null ? "" : account.getDisplayName()));
        } else {
            populateAccountPage(req, latestLoginUser);
        }
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/account_center.jsp").forward(req, resp);
    }

    private void populateAccountPage(HttpServletRequest req, LoginUser loginUser) {
        req.setAttribute("loginUser", loginUser);
        ServiceResult<UserAccount> result = accountCenterService.loadAccount(loginUser.getUserId());
        if (result.isSuccess()) {
            req.setAttribute("account", result.getData());
            req.setAttribute("avatarPreviewUrl", buildAvatarPreviewUrl(req, result.getData().getAvatarPath()));
            req.setAttribute("avatarInitial", buildAvatarInitial(result.getData().getDisplayName()));
            return;
        }

        UserAccount fallback = new UserAccount();
        fallback.setDisplayName(loginUser.getDisplayName());
        fallback.setUsername(loginUser.getUsername());
        fallback.setUserId(loginUser.getUserId());
        fallback.setRole(loginUser.getRole());
        fallback.setAvatarPath(loginUser.getAvatarPath());
        req.setAttribute("account", fallback);
        req.setAttribute("avatarPreviewUrl", buildAvatarPreviewUrl(req, loginUser.getAvatarPath()));
        req.setAttribute("avatarInitial", buildAvatarInitial(loginUser.getDisplayName()));
        req.setAttribute("error", result.getMessage());
    }

    private LoginUser currentLoginUser(HttpServletRequest req) {
        Object requestUser = req.getAttribute("loginUser");
        if (requestUser instanceof LoginUser) {
            return (LoginUser) requestUser;
        }
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

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
