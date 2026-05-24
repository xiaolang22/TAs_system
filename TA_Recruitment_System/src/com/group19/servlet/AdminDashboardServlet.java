package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.AdminDashboardData;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.model.UserAccount;
import com.group19.service.AdminDashboardService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Administrator Dashboard Servlet, handling comprehensive system management operations.
 *
 * <p>URL handled: /admin/home
 *
 * <p>Management panels supported:
 * <ul>
 *   <li>TA Management panel -- view TA list, reset passwords, freeze/unfreeze accounts</li>
 *   <li>MO Management panel -- view MO list, reset passwords, freeze/unfreeze accounts</li>
 *   <li>Job Management panel -- view/edit/delete job information</li>
 * </ul>
 *
 * <p>Permissions: accessible only by ADMIN role.
 *
 * @author Group 19
 * @see AdminDashboardService
 */
public class AdminDashboardServlet extends HttpServlet {
    /** Focus section identifier for the TA management panel */
    private static final String TA_PANEL = "admin-ta-panel";
    /** Focus section identifier for the MO management panel */
    private static final String MO_PANEL = "admin-mo-panel";
    /** Focus section identifier for the Job management panel */
    private static final String JOB_PANEL = "admin-job-panel";

    private AdminDashboardService adminDashboardService;

    /**
     * Initialises the Servlet, loads user, application, job, and TA data files,
     * and creates the AdminDashboardService instance.
     */
    @Override
    public void init() {
        Path userPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        Path applicationPath = DataPathResolver.resolve(
                getServletContext(), "applicationDataFile", "/data/applications.json", "applications.json");
        Path jobPath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        Path taPath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        this.adminDashboardService = new AdminDashboardService(
                new UserAccountDao(userPath),
                new ApplicationDao(applicationPath),
                new JobDao(jobPath),
                new TADao(taPath));
    }

    /**
     * Handles GET requests: loads and displays the administrator dashboard.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify administrator identity</li>
     *   <li>Load details of the specified TA/MO/job based on optional parameters</li>
     *   <li>Bind dashboard data to request attributes</li>
     *   <li>Forward to admin_home.jsp</li>
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

        LoginUser loginUser = currentAdmin(req, resp);
        if (loginUser == null) {
            return;
        }

        String selectedTaId = trimToNull(req.getParameter("selectedTaId"));
        String selectedMoId = trimToNull(req.getParameter("selectedMoId"));
        String selectedJobId = trimToNull(req.getParameter("selectedJobId"));
        String focusSection = normalizeFocusSection(req.getParameter("focusSection"));
        ServiceResult<AdminDashboardData> result =
                adminDashboardService.loadDashboard(selectedTaId, selectedMoId, selectedJobId);

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("successMsg", req.getParameter("success"));
        req.setAttribute("errorMsg", req.getParameter("error"));
        req.setAttribute("selectedTaId", selectedTaId);
        req.setAttribute("selectedMoId", selectedMoId);
        req.setAttribute("selectedJobId", selectedJobId);
        req.setAttribute("focusSection", focusSection);
        if (result.isSuccess()) {
            bindDashboard(req, result.getData());
        } else {
            req.setAttribute("errorMsg", result.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/jsp/admin_home.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: executes administrator management operations.
     *
     * <p>Different operations are performed based on the action parameter:
     * <ul>
     *   <li>resetTaPassword / resetMoPassword -- reset account password</li>
     *   <li>toggleTaFreeze / toggleMoFreeze -- freeze/unfreeze account</li>
     *   <li>updateJob -- update job information</li>
     *   <li>deleteJob -- delete a job</li>
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

        LoginUser loginUser = currentAdmin(req, resp);
        if (loginUser == null) {
            return;
        }

        String action = trimToNull(req.getParameter("action"));
        String selectedTaId = trimToNull(req.getParameter("selectedTaId"));
        String selectedMoId = trimToNull(req.getParameter("selectedMoId"));
        String selectedJobId = trimToNull(req.getParameter("selectedJobId"));
        String focusSection = normalizeFocusSection(req.getParameter("focusSection"));

        if ("resetTaPassword".equals(action)) {
            handleAccountResult(req, resp, adminDashboardService.resetAccountPassword(req.getParameter("userId"), "TA"),
                    selectedOr(req.getParameter("userId"), selectedTaId), selectedMoId, selectedJobId, focusSection);
            return;
        }
        if ("toggleTaFreeze".equals(action)) {
            handleAccountResult(req, resp, adminDashboardService.toggleAccountFreeze(req.getParameter("userId"), "TA"),
                    selectedOr(req.getParameter("userId"), selectedTaId), selectedMoId, selectedJobId, focusSection);
            return;
        }
        if ("resetMoPassword".equals(action)) {
            handleAccountResult(req, resp, adminDashboardService.resetAccountPassword(req.getParameter("userId"), "MO"),
                    selectedTaId, selectedOr(req.getParameter("userId"), selectedMoId), selectedJobId, focusSection);
            return;
        }
        if ("toggleMoFreeze".equals(action)) {
            handleAccountResult(req, resp, adminDashboardService.toggleAccountFreeze(req.getParameter("userId"), "MO"),
                    selectedTaId, selectedOr(req.getParameter("userId"), selectedMoId), selectedJobId, focusSection);
            return;
        }
        if ("updateJob".equals(action)) {
            ServiceResult<Job> result = adminDashboardService.updateJob(
                    req.getParameter("jobId"),
                    req.getParameter("title"),
                    req.getParameter("description"),
                    req.getParameter("requirements"),
                    req.getParameter("hours"),
                    req.getParameter("schedule"),
                    req.getParameter("deadline"),
                    req.getParameter("status"));
            redirectWithResult(req, resp, result.getMessage(), result.isSuccess(), selectedTaId, selectedMoId,
                    selectedOr(req.getParameter("jobId"), selectedJobId), focusSection);
            return;
        }
        if ("deleteJob".equals(action)) {
            ServiceResult<Void> result = adminDashboardService.deleteJob(req.getParameter("jobId"));
            redirectWithResult(req, resp, result.getMessage(), result.isSuccess(), selectedTaId, selectedMoId, null, focusSection);
            return;
        }

        redirectWithResult(req, resp, "Unable to recognize the current action.", false,
                selectedTaId, selectedMoId, selectedJobId, focusSection);
    }

    /**
     * Handles the result of an account operation by redirecting with a result message.
     *
     * @param req           the HTTP request
     * @param resp          the HTTP response
     * @param result        the service-layer result of the account operation
     * @param selectedTaId  the currently selected TA ID
     * @param selectedMoId  the currently selected MO ID
     * @param selectedJobId the currently selected job ID
     * @param focusSection  the focus panel section
     * @throws IOException if an I/O error occurs
     */
    private void handleAccountResult(HttpServletRequest req, HttpServletResponse resp, ServiceResult<UserAccount> result,
                                     String selectedTaId, String selectedMoId, String selectedJobId,
                                     String focusSection) throws IOException {
        redirectWithResult(req, resp, result.getMessage(), result.isSuccess(),
                selectedTaId, selectedMoId, selectedJobId, focusSection);
    }

    /**
     * Builds a redirect URL (with query parameters) and performs the redirect.
     *
     * @param req           the HTTP request
     * @param resp          the HTTP response
     * @param message       the result message
     * @param success       whether the operation was successful
     * @param selectedTaId  the currently selected TA ID
     * @param selectedMoId  the currently selected MO ID
     * @param selectedJobId the currently selected job ID
     * @param focusSection  the focus panel section
     * @throws IOException if an I/O error occurs
     */
    private void redirectWithResult(HttpServletRequest req, HttpServletResponse resp, String message, boolean success,
                                    String selectedTaId, String selectedMoId, String selectedJobId,
                                    String focusSection) throws IOException {
        StringBuilder target = new StringBuilder(req.getContextPath()).append("/admin/home");
        boolean hasQuery = false;
        hasQuery = appendQuery(target, hasQuery, success ? "success" : "error", message);
        hasQuery = appendQuery(target, hasQuery, "selectedTaId", selectedTaId);
        hasQuery = appendQuery(target, hasQuery, "selectedMoId", selectedMoId);
        hasQuery = appendQuery(target, hasQuery, "selectedJobId", selectedJobId);
        if (focusSection != null) {
            hasQuery = appendQuery(target, hasQuery, "focusSection", focusSection);
            target.append('#').append(focusSection);
        }
        resp.sendRedirect(target.toString());
    }

    /**
     * Appends a query parameter to the URL builder.
     *
     * @param target   the URL builder
     * @param hasQuery whether a query parameter already exists
     * @param name     the parameter name
     * @param value    the parameter value
     * @return whether a query parameter is now present after appending
     */
    private static boolean appendQuery(StringBuilder target, boolean hasQuery, String name, String value) {
        if (value == null || value.isBlank()) {
            return hasQuery;
        }
        target.append(hasQuery ? '&' : '?')
                .append(name)
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        return true;
    }

    /**
     * Binds dashboard data to request attributes for JSP page rendering.
     *
     * @param req  the HTTP request
     * @param data the dashboard data
     */
    private void bindDashboard(HttpServletRequest req, AdminDashboardData data) {
        req.setAttribute("taAccounts", data.getTaAccounts());
        req.setAttribute("moAccounts", data.getMoAccounts());
        req.setAttribute("jobs", data.getJobs());
        req.setAttribute("selectedTa", data.getSelectedTa());
        req.setAttribute("selectedMo", data.getSelectedMo());
        req.setAttribute("selectedJob", data.getSelectedJob());
        req.setAttribute("openJobCount", data.getOpenJobCount());
        req.setAttribute("closedJobCount", data.getClosedJobCount());
        req.setAttribute("taCount", data.getTaCount());
        req.setAttribute("moCount", data.getMoCount());
        req.setAttribute("pendingApplicationCount", data.getPendingApplicationCount());
        req.setAttribute("acceptedApplicationCount", data.getAcceptedApplicationCount());
        req.setAttribute("rejectedApplicationCount", data.getRejectedApplicationCount());
        req.setAttribute("workloadWarningCount", data.getWorkloadWarningCount());
        req.setAttribute("timeConflictCount", data.getTimeConflictCount());
        req.setAttribute("recentJobs", data.getRecentJobs());
        req.setAttribute("recentApplications", data.getRecentApplications());
        req.setAttribute("recentAlerts", data.getRecentAlerts());
    }

    /**
     * Verifies that the current user is an administrator; redirects or returns an
     * error if not.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @return the current admin user, or null if verification fails
     * @throws IOException if an I/O error occurs (may be thrown during redirect)
     */
    private LoginUser currentAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can access this page.");
            return null;
        }
        return loginUser;
    }

    /**
     * Selects a non-null value, preferring the preferred value and falling back
     * to the fallback when it is null.
     *
     * @param preferred the preferred value
     * @param fallback  the fallback value
     * @return a non-null value
     */
    private static String selectedOr(String preferred, String fallback) {
        String value = trimToNull(preferred);
        return value != null ? value : fallback;
    }

    /**
     * Normalises the focus section parameter, accepting only known panel identifiers.
     *
     * @param value the raw focus section parameter
     * @return a valid focus section identifier, or null if invalid
     */
    private static String normalizeFocusSection(String value) {
        String trimmed = trimToNull(value);
        if (TA_PANEL.equals(trimmed) || MO_PANEL.equals(trimmed) || JOB_PANEL.equals(trimmed)) {
            return trimmed;
        }
        return null;
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
