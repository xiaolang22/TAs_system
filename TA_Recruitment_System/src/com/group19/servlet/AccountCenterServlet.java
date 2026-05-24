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

/**
 * Account Centre Servlet, handling viewing and editing of user account information.
 *
 * <p>URL patterns handled:
 * <ul>
 *   <li>/ta/account -- Teaching Assistant Account Centre</li>
 *   <li>/mo/account -- Module Organiser Account Centre</li>
 *   <li>/admin/account -- Administrator Account Centre</li>
 * </ul>
 *
 * <p>Supported operations:
 * <ul>
 *   <li>View / edit profile (displayName, username)</li>
 *   <li>Change password</li>
 *   <li>Upload / replace avatar</li>
 * </ul>
 *
 * <p>Permissions: accessible by TA, MO, and ADMIN roles.
 *
 * @author Group 19
 * @see AccountCenterService
 */
@MultipartConfig
public class AccountCenterServlet extends HttpServlet {
    private AccountCenterService accountCenterService;

    /**
     * Initialises the Servlet, loads the user data file, and creates the AccountCenterService instance.
     */
    @Override
    public void init() {
        Path userDataPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.accountCenterService = new AccountCenterService(new UserAccountDao(userDataPath));
    }

    /**
     * Handles GET requests: loads and displays the current user's Account Centre page.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify the user's login state and role permissions</li>
     *   <li>Load account information and populate request attributes</li>
     *   <li>Check for update-success feedback messages (profile/password/avatar)</li>
     *   <li>Forward to account_center.jsp</li>
     * </ol>
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

        LoginUser loginUser = currentLoginUser(req);
        if (!isAllowedRole(loginUser)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        populateAccountPage(req, loginUser);
        if ("profile".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "Profile information updated successfully.");
        } else if ("password".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "Password updated successfully.");
        } else if ("avatar".equalsIgnoreCase(req.getParameter("updated"))) {
            req.setAttribute("success", "Avatar updated successfully.");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/account_center.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: performs account update operations (profile, password, avatar).
     *
     * <p>The operation is determined by the action parameter:
     * <ul>
     *   <li>profile -- update display name and username</li>
     *   <li>password -- change password</li>
     *   <li>avatar -- upload avatar file</li>
     * </ul>
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

        LoginUser loginUser = currentLoginUser(req);
        if (!isAllowedRole(loginUser)) {
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
                result = ServiceResult.failure("Unable to recognize the current action.");
                break;
        }

        if (result.isSuccess()) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.setAttribute("loginUser", result.getData());
            }
            resp.sendRedirect(req.getContextPath() + accountPath(result.getData()) + "?updated=" + action);
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

    /**
     * Populates request attributes for the account page, including login user details,
     * navigation paths, avatar preview, etc.
     *
     * @param req       the HTTP request
     * @param loginUser the currently logged-in user
     */
    private void populateAccountPage(HttpServletRequest req, LoginUser loginUser) {
        req.setAttribute("loginUser", loginUser);
        req.setAttribute("accountPath", accountPath(loginUser));
        req.setAttribute("homePath", homePath(loginUser));
        req.setAttribute("roleLabel", roleLabel(loginUser));
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

    /**
     * Retrieves the current login user, preferring the request attribute over the session.
     *
     * @param req the HTTP request
     * @return the currently logged-in user, or null if not logged in
     */
    private LoginUser currentLoginUser(HttpServletRequest req) {
        Object requestUser = req.getAttribute("loginUser");
        if (requestUser instanceof LoginUser) {
            return (LoginUser) requestUser;
        }
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    /**
     * Resolves the avatar upload directory path, preferring the web application's real path.
     *
     * @return the path to the avatar upload directory
     */
    private Path resolveAvatarUploadDir() {
        String realPath = getServletContext().getRealPath("/uploads/avatars");
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "uploads", "avatars");
    }

    /**
     * Builds the full URL for avatar preview.
     *
     * @param req        the HTTP request, used to obtain the context path
     * @param avatarPath the relative path of the avatar file
     * @return the full avatar preview URL, or an empty string if the path is blank
     */
    private String buildAvatarPreviewUrl(HttpServletRequest req, String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            return "";
        }
        return req.getContextPath() + avatarPath.trim();
    }

    /**
     * Generates an initial-letter placeholder for the avatar based on the display name.
     *
     * @param displayName the user's display name
     * @return the upper-case initial character, or "U" if the name is blank
     */
    private String buildAvatarInitial(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return "U";
        }
        return displayName.trim().substring(0, 1);
    }

    /**
     * Checks whether the user has permission to access the Account Centre
     * (TA, MO, or ADMIN role).
     *
     * @param loginUser the login user object
     * @return true if the user has an allowed role, false otherwise
     */
    private static boolean isAllowedRole(LoginUser loginUser) {
        if (loginUser == null || loginUser.getRole() == null) {
            return false;
        }
        String role = loginUser.getRole().trim();
        return "TA".equalsIgnoreCase(role) || "MO".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Returns the Account Centre URL path corresponding to the user's role.
     *
     * @param loginUser the login user object
     * @return the Account Centre path for the corresponding role
     */
    private static String accountPath(LoginUser loginUser) {
        if (loginUser != null && "ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return "/admin/account";
        }
        if (loginUser != null && "MO".equalsIgnoreCase(loginUser.getRole())) {
            return "/mo/account";
        }
        return "/ta/account";
    }

    /**
     * Returns the home page URL path corresponding to the user's role.
     *
     * @param loginUser the login user object
     * @return the home page path for the corresponding role
     */
    private static String homePath(LoginUser loginUser) {
        if (loginUser != null && "ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return "/admin/home";
        }
        if (loginUser != null && "MO".equalsIgnoreCase(loginUser.getRole())) {
            return "/mo/home";
        }
        return "/ta/home";
    }

    /**
     * Returns the role label text corresponding to the user's role.
     *
     * @param loginUser the login user object
     * @return the role label (ADMIN, MO, or TA)
     */
    private static String roleLabel(LoginUser loginUser) {
        if (loginUser != null && "ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            return "ADMIN";
        }
        if (loginUser != null && "MO".equalsIgnoreCase(loginUser.getRole())) {
            return "MO";
        }
        return "TA";
    }

    /**
     * Trims the string, returning an empty string for null values.
     *
     * @param value the input string
     * @return the trimmed string, or an empty string if null
     */
    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
