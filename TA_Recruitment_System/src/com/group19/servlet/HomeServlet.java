package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.SavedJobDao;
import com.group19.dao.UserAccountDao;
import com.group19.model.LoginUser;
import com.group19.service.DeadlineReminderService;
import com.group19.service.JobService;
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
import java.time.LocalDate;
import java.nio.file.Path;

public class HomeServlet extends HttpServlet {
    private SavedJobService savedJobService;
    private TaStatusNotificationService taStatusNotificationService;
    private MoNewApplicationNotificationService moNewApplicationNotificationService;
    private DeadlineReminderService deadlineReminderService;

    @Override
    public void init() {
        Path savedJobPath = DataPathResolver.resolve(
                getServletContext(), "savedJobDataFile", "/data/saved_jobs.json", "saved_jobs.json");
        Path jobPath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        Path notificationPath = DataPathResolver.resolve(
                getServletContext(), "notificationDataFile", "/data/notifications.json", "notifications.json");

        Path applicationPath = DataPathResolver.resolve(
                getServletContext(), "applicationDataFile", "/data/applications.json", "applications.json");

        JobDao jobDao = new JobDao(jobPath);
        JobService jobService = new JobService(jobDao);
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobPath), jobDao);
        this.deadlineReminderService = new DeadlineReminderService(
                jobService,
                new ApplicationDao(applicationPath),
                savedJobService);
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
        String contextPath = req.getContextPath();
        LocalDate today = LocalDate.now();

        if ("TA".equalsIgnoreCase(loginUser.getRole())) {
            String taStudentId = loginUser.getUserId();
            req.setAttribute("savedJobs", savedJobService.findSavedJobs(taStudentId));
            req.setAttribute("taNotifications", taStatusNotificationService.loadForTaDashboard(taStudentId));
            req.setAttribute("unreadNotificationCount", taStatusNotificationService.countUnread(taStudentId));
            req.setAttribute("deadlineReminders",
                    deadlineReminderService.buildRemindersForTa(taStudentId, today, contextPath));
        } else if ("MO".equalsIgnoreCase(loginUser.getRole())) {
            String moUserId = loginUser.getUserId();
            req.setAttribute("moNotifications",
                    moNewApplicationNotificationService.loadForMoDashboard(moUserId, contextPath));
            req.setAttribute("moUnreadNotificationCount",
                    moNewApplicationNotificationService.countUnread(moUserId));
            req.setAttribute("deadlineReminders",
                    deadlineReminderService.buildRemindersForMo(today, contextPath));
        }
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
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
