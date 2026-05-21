package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.SavedJobDao;
import com.group19.model.LoginUser;
import com.group19.dao.UserAccountDao;
import com.group19.service.MoNewApplicationNotificationService;
import com.group19.service.SavedJobService;
import com.group19.service.TaStatusNotificationService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HomeServlet extends HttpServlet {
    private SavedJobService savedJobService;
    private TaStatusNotificationService taStatusNotificationService;
    private MoNewApplicationNotificationService moNewApplicationNotificationService;

    @Override
    public void init() {
        Path savedJobPath = resolveDataPath(
                firstNonBlank(getServletContext().getInitParameter("savedJobDataFile"), "/data/saved_jobs.json"),
                "saved_jobs.json");
        Path jobPath = resolveDataPath(
                firstNonBlank(getServletContext().getInitParameter("jobDataFile"), "/data/jobs.json"),
                "jobs.json");
        Path notificationPath = DataPathResolver.resolve(
                getServletContext(), "notificationDataFile", "/data/notifications.json", "notifications.json");

        JobDao jobDao = new JobDao(jobPath);
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobPath), jobDao);
        NotificationDao notificationDao = new NotificationDao(notificationPath);
        Path userPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.taStatusNotificationService = new TaStatusNotificationService(notificationDao, jobDao);
        this.moNewApplicationNotificationService =
                new MoNewApplicationNotificationService(notificationDao, jobDao, new UserAccountDao(userPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("currentRequestPath", buildCurrentRequestPath(req));
        setSavedJobFeedback(req);
        if ("TA".equalsIgnoreCase(loginUser.getRole())) {
            String taStudentId = loginUser.getUserId();
            req.setAttribute("savedJobs", savedJobService.findSavedJobs(taStudentId));
            req.setAttribute("taNotifications", taStatusNotificationService.loadForTaDashboard(taStudentId));
            req.setAttribute("unreadNotificationCount", taStatusNotificationService.countUnread(taStudentId));
        } else if ("MO".equalsIgnoreCase(loginUser.getRole())) {
            String moUserId = loginUser.getUserId();
            req.setAttribute("moNotifications",
                    moNewApplicationNotificationService.loadForMoDashboard(moUserId, req.getContextPath()));
            req.setAttribute("moUnreadNotificationCount",
                    moNewApplicationNotificationService.countUnread(moUserId));
        }
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
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

    private static void setSavedJobFeedback(HttpServletRequest req) {
        String message = trimToNull(req.getParameter("savedJobMessage"));
        String error = trimToNull(req.getParameter("savedJobError"));
        if (message != null) {
            req.setAttribute("savedJobMessage", message);
        }
        if (error != null) {
            req.setAttribute("savedJobError", error);
        }
    }

    private static String buildCurrentRequestPath(HttpServletRequest req) {
        String query = req.getQueryString();
        String path = req.getRequestURI();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
