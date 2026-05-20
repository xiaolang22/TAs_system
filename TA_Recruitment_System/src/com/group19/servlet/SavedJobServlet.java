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

public class SavedJobServlet extends HttpServlet {
    private SavedJobService savedJobService;

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

    private static String appendMessage(String returnTo, String name, String message) {
        String separator = returnTo.contains("?") ? "&" : "?";
        return returnTo + separator + name + "=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }
}
