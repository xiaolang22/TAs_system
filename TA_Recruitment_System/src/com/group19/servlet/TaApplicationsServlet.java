package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.TimelineDao;
import com.group19.dto.TaApplicationOverview;
import com.group19.model.LoginUser;
import com.group19.service.TaApplicationStatusService;
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
import java.util.List;

public class TaApplicationsServlet extends HttpServlet {
    private TaApplicationStatusService taApplicationStatusService;
    private TaStatusNotificationService taStatusNotificationService;

    @Override
    public void init() {
        Path applicationPath = DataPathResolver.resolve(
                getServletContext(), "applicationDataFile", "/data/applications.json", "applications.json");
        Path jobPath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        Path timelinePath = DataPathResolver.resolve(
                getServletContext(), "timelineDataFile", "/data/timelines.json", "timelines.json");
        Path notificationPath = DataPathResolver.resolve(
                getServletContext(), "notificationDataFile", "/data/notifications.json", "notifications.json");

        ApplicationDao applicationDao = new ApplicationDao(applicationPath);
        JobDao jobDao = new JobDao(jobPath);
        TimelineDao timelineDao = new TimelineDao(timelinePath);
        this.taApplicationStatusService = new TaApplicationStatusService(applicationDao, jobDao, timelineDao);
        this.taStatusNotificationService = new TaStatusNotificationService(new NotificationDao(notificationPath), jobDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = loginUser.getUserId();
        List<TaApplicationOverview> overviews = taApplicationStatusService.loadOverviewsForTa(studentId);
        req.setAttribute("taNotifications", taStatusNotificationService.loadAllForTa(studentId));
        taStatusNotificationService.markAllAsRead(studentId);
        req.setAttribute("applications", overviews);
        req.setAttribute("loginUser", loginUser);
        req.getRequestDispatcher("/WEB-INF/jsp/ta_applications.jsp").forward(req, resp);
    }
}
