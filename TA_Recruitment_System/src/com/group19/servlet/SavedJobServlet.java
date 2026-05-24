package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dao.SavedJobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.SavedJobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Saved job servlet, handling TA users' bookmarking and unbookmarking of job
 * positions.
 *
 * <p>URL handled: /saved-job
 *
 * <p>Supported operations (POST method):
 * <ul>
 *   <li>Save job (default action) -- adds a position to the TA's saved list</li>
 *   <li>Remove saved job (action=remove) -- removes a position from the saved list</li>
 * </ul>
 *
 * <p>After the operation completes, the user is redirected back to the source page
 * (returnTo parameter) with an operation result message.
 *
 * <p>Permissions: accessible only by TA role.
 *
 * @author Group 19
 * @see SavedJobService
 */
public class SavedJobServlet extends HttpServlet {
    private SavedJobService savedJobService;

    /**
     * Initialises the Servlet, loads saved-job data and job data files, and creates
     * the SavedJobService instance.
     */
    @Override
    public void init() {
        Path savedJobPath = resolveDataPath(
                firstNonBlank(getServletContext().getInitParameter("savedJobDataFile"), "/data/saved_jobs.json"),
                "saved_jobs.json");
        Path jobPath = resolveDataPath(
                firstNonBlank(getServletContext().getInitParameter("jobDataFile"), "/data/jobs.json"),
                "jobs.json");

        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobPath), new JobDao(jobPath));
    }

    /**
     * Handles POST requests: performs the save or remove-saved-job operation.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify TA login state</li>
     *   <li>Decide whether to save or remove based on the action parameter</li>
     *   <li>After the operation completes, redirect to the page specified by returnTo
     *       with an operation result message</li>
     * </ol>
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

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String jobId = req.getParameter("jobId");
        String action = req.getParameter("action");
        ServiceResult<Void> result;
        if ("remove".equalsIgnoreCase(action)) {
            result = savedJobService.removeSavedJob(loginUser.getUserId(), jobId);
        } else {
            result = savedJobService.saveJob(loginUser.getUserId(), jobId);
        }

        String returnTo = safeReturnTo(req);
        String flag = result.isSuccess() ? "savedJobMessage" : "savedJobError";
        resp.sendRedirect(appendMessage(returnTo, flag, result.getMessage()));
    }

    /**
     * Safely parses the returnTo parameter, allowing only paths within the same
     * application to prevent open redirect attacks.
     *
     * @param req the HTTP request
     * @return a safe return path
     */
    private String safeReturnTo(HttpServletRequest req) {
        String returnTo = req.getParameter("returnTo");
        String contextPath = req.getContextPath();
        if (returnTo == null || returnTo.isBlank()) {
            return contextPath + "/jobs";
        }
        String trimmed = returnTo.trim();
        if (trimmed.startsWith(contextPath + "/")) {
            return trimmed;
        }
        if (trimmed.startsWith("/") && (contextPath == null || contextPath.isBlank())) {
            return trimmed;
        }
        return contextPath + "/jobs";
    }

    /**
     * Appends a query parameter message to a URL.
     *
     * @param returnTo the original URL
     * @param name     the parameter name
     * @param message  the message content
     * @return the URL with the appended parameter
     */
    private static String appendMessage(String returnTo, String name, String message) {
        String separator = returnTo.contains("?") ? "&" : "?";
        return returnTo + separator + name + "=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's
     * real path and falling back to the data folder under the working directory.
     *
     * @param webRelativePath  the web-relative path
     * @param fallbackFileName the fallback file name
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }

    /**
     * Returns the first non-blank value from two strings.
     *
     * @param preferred the preferred value
     * @param fallback  the fallback value
     * @return the first non-blank string
     */
    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }
}
