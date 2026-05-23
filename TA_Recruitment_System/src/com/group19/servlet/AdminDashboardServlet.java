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

public class AdminDashboardServlet extends HttpServlet {
    private static final String TA_PANEL = "admin-ta-panel";
    private static final String MO_PANEL = "admin-mo-panel";
    private static final String JOB_PANEL = "admin-job-panel";

    private AdminDashboardService adminDashboardService;

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

    private void handleAccountResult(HttpServletRequest req, HttpServletResponse resp, ServiceResult<UserAccount> result,
                                     String selectedTaId, String selectedMoId, String selectedJobId,
                                     String focusSection) throws IOException {
        redirectWithResult(req, resp, result.getMessage(), result.isSuccess(),
                selectedTaId, selectedMoId, selectedJobId, focusSection);
    }

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

    private static String selectedOr(String preferred, String fallback) {
        String value = trimToNull(preferred);
        return value != null ? value : fallback;
    }

    private static String normalizeFocusSection(String value) {
        String trimmed = trimToNull(value);
        if (TA_PANEL.equals(trimmed) || MO_PANEL.equals(trimmed) || JOB_PANEL.equals(trimmed)) {
            return trimmed;
        }
        return null;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
