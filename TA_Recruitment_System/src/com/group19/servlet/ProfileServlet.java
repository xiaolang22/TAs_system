package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.ProfileService;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TA profile servlet, handling viewing and editing of Teaching Assistant
 * personal profiles.
 *
 * <p>URL handled: /profile
 *
 * <p>Supported operations:
 * <ul>
 *   <li>GET -- displays the current TA's profile (loads saved data or shows an
 *       empty form)</li>
 *   <li>POST -- saves/updates the profile (name, email, programme, skills,
 *       experience, availability, etc.)</li>
 * </ul>
 *
 * <p>Permissions: accessible only by TA role.
 *
 * @author Group 19
 * @see ProfileService
 */
public class ProfileServlet extends HttpServlet {
    private ProfileService profileService;

    /**
     * Initialises the Servlet, loads the TA data file, and creates the ProfileService
     * instance.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.profileService = new ProfileService(new TADao(filePath));
    }

    /**
     * Handles GET requests: displays the TA profile editing page.
     *
     * <p>If the profile has already been saved or a CV has been uploaded, a
     * corresponding success message is shown.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        loadProfileForCurrentUser(req, loginUser);

        if ("true".equalsIgnoreCase(req.getParameter("saved"))) {
            req.setAttribute("success", "Profile saved successfully.");
        } else if ("true".equalsIgnoreCase(req.getParameter("cvSaved"))) {
            req.setAttribute("success", "Resume uploaded successfully.");
        }

        TA profile = (TA) req.getAttribute("profile");
        if (profile != null) {
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(profile.getCvFilePath()));
        }

        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: saves the TA personal profile.
     *
     * <p>On success, redirects to the profile page with a success message; on failure,
     * redisplays the form data with an error message.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = loginUser.getUserId();
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String programme = req.getParameter("programme");
        String skills = req.getParameter("skills");
        String experience = req.getParameter("experience");
        String availability = req.getParameter("availability");

        ServiceResult<TA> result = profileService.saveProfile(
                name, studentId, email, programme, skills, experience, availability);

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/profile?saved=true");
            return;
        }

        TA draft = new TA(name, studentId, email, programme, skills, availability);
        draft.setExperience(experience);

        ServiceResult<TA> existingResult = profileService.getProfileByStudentId(studentId);
        if (existingResult.isSuccess() && existingResult.getData() != null) {
            draft.setCvFilePath(existingResult.getData().getCvFilePath());
            draft.setUpdatedAt(existingResult.getData().getUpdatedAt());
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(existingResult.getData().getCvFilePath()));
        }

        req.setAttribute("profile", draft);
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    /**
     * Loads the profile data for the current TA user; if no profile exists, a draft
     * with default values is created.
     *
     * @param req       the HTTP request
     * @param loginUser the currently logged-in TA user
     */
    private void loadProfileForCurrentUser(HttpServletRequest req, LoginUser loginUser) {
        ServiceResult<TA> result = profileService.getProfileByStudentId(loginUser.getUserId());
        if (result.isSuccess() && result.getData() != null) {
            req.setAttribute("profile", result.getData());
            return;
        }

        TA draft = new TA();
        draft.setName(loginUser.getDisplayName());
        draft.setStudentId(loginUser.getUserId());
        req.setAttribute("profile", draft);

        if (!"Profile not found.".equals(result.getMessage())) {
            req.setAttribute("error", result.getMessage());
        }
    }

    /**
     * Retrieves the currently logged-in user, preferring the request attribute
     * first, then falling back to the session.
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
     * Resolves the absolute path of a data file, preferring the web application's
     * real path and falling back to the working directory.
     *
     * @param webRelativePath the web-relative path
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }

        return Paths.get(System.getProperty("user.dir"), "data", "tas.json");
    }
}
