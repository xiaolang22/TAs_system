package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.MoTaCandidateCard;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.MoTaDirectoryService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * MO views TA profile servlet, handling Module Organiser viewing of Teaching
 * Assistant personal profiles and CVs.
 *
 * <p>URL handled: /mo/ta-profile
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Verify MO login identity</li>
 *   <li>Load detailed candidate TA information based on studentId</li>
 *   <li>Attach avatar URL, CV availability, and other information</li>
 *   <li>Support returning to the source page via backUrl/backLabel parameters</li>
 *   <li>Forward to mo_ta_profile.jsp for display</li>
 * </ol>
 *
 * <p>Permissions: accessible only by MO role.
 *
 * @author Group 19
 * @see MoTaDirectoryService
 */
public class MoTaProfileServlet extends HttpServlet {
    private MoTaDirectoryService moTaDirectoryService;

    /**
     * Initialises the Servlet, loads TA data and user data files, and creates the
     * MoTaDirectoryService instance.
     */
    @Override
    public void init() {
        Path taPath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        Path userPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.moTaDirectoryService = new MoTaDirectoryService(new TADao(taPath), new UserAccountDao(userPath));
    }

    /**
     * Handles GET requests: displays the detailed profile page for a specified TA.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify MO login state</li>
     *   <li>Obtain the studentId parameter and load candidate TA data</li>
     *   <li>If loading fails, display an error message</li>
     *   <li>Attach avatar URL and CV file availability information</li>
     *   <li>Forward to mo_ta_profile.jsp</li>
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

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = trimToNull(req.getParameter("studentId"));
        req.setAttribute("backUrl", resolveBackUrl(req));
        req.setAttribute("backLabel", resolveBackLabel(req));
        ServiceResult<MoTaCandidateCard> result = moTaDirectoryService.loadCandidateByStudentId(studentId);
        if (!result.isSuccess()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/mo_ta_profile.jsp").forward(req, resp);
            return;
        }

        MoTaCandidateCard candidate = result.getData();
        attachCandidateAssets(req, candidate);
        req.setAttribute("loginUser", loginUser);
        req.setAttribute("candidate", candidate);
        req.getRequestDispatcher("/WEB-INF/jsp/mo_ta_profile.jsp").forward(req, resp);
    }

    /**
     * Resolves the return URL, allowing only whitelisted paths; falls back to the
     * MO home page otherwise.
     *
     * @param req the HTTP request
     * @return a safe return URL
     */
    private String resolveBackUrl(HttpServletRequest req) {
        String fallback = req.getContextPath() + "/mo/home";
        String raw = trimToNull(req.getParameter("backUrl"));
        if (raw == null) {
            return fallback;
        }
        String contextPath = req.getContextPath();
        if (raw.startsWith(contextPath + "/mo/applications")
                || raw.startsWith(contextPath + "/mo/home")
                || raw.startsWith(contextPath + "/mo/jobs")) {
            return raw;
        }
        return fallback;
    }

    /**
     * Resolves the label text for the back button, preferring the backLabel
     * parameter and otherwise inferring from the return URL.
     *
     * @param req the HTTP request
     * @return the label text for the back button
     */
    private String resolveBackLabel(HttpServletRequest req) {
        String backUrl = resolveBackUrl(req);
        String rawLabel = trimToNull(req.getParameter("backLabel"));
        if (rawLabel != null) {
            return rawLabel;
        }
        if (backUrl.contains("/mo/applications")) {
            return "Back to Applicant List";
        }
        if (backUrl.contains("/mo/jobs")) {
            return "Back to Job List";
        }
        return "Back to Home";
    }

    /**
     * Attaches the avatar URL and CV file availability information to a candidate
     * TA card.
     *
     * @param req       the HTTP request
     * @param candidate the candidate TA card object
     */
    private void attachCandidateAssets(HttpServletRequest req, MoTaCandidateCard candidate) {
        if (candidate == null) {
            return;
        }
        String contextPath = req.getContextPath();
        String avatarPath = candidate.getAvatarPath();
        candidate.setAvatarUrl(avatarPath == null || avatarPath.isBlank() ? "" : contextPath + avatarPath);

        String cvFilePath = candidate.getCvFilePath();
        boolean resumeAvailable = hasUploadedResume(cvFilePath);
        candidate.setResumeAvailable(resumeAvailable);
        candidate.setCvUrl(resumeAvailable ? contextPath + cvFilePath : "");
    }

    /**
     * Checks whether the specified CV file exists on the server.
     *
     * @param cvFilePath the CV file path
     * @return true if the file exists
     */
    private boolean hasUploadedResume(String cvFilePath) {
        if (cvFilePath == null || cvFilePath.isBlank()) {
            return false;
        }
        String normalized = cvFilePath.trim().replace("/", java.io.File.separator);
        if (normalized.startsWith(java.io.File.separator)) {
            normalized = normalized.substring(1);
        }
        String realPath = getServletContext().getRealPath("/" + normalized.replace(java.io.File.separatorChar, '/'));
        if (realPath != null && !realPath.isBlank()) {
            return Files.exists(Paths.get(realPath));
        }
        return Files.exists(Paths.get(System.getProperty("user.dir"), "web", normalized));
    }

    /**
     * Trims the string and returns null if the result is empty.
     *
     * @param value the input string
     * @return the trimmed string, or null if empty
     */
    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
