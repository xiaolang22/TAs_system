package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.SavedJobDao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.MoNotificationView;
import com.group19.model.Job;
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
import java.util.ArrayList;
import java.util.List;

public class HomeServlet extends HttpServlet {
    private JobService jobService;
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
        this.jobService = new JobService(jobDao);
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobPath), jobDao);
        this.deadlineReminderService = new DeadlineReminderService(
                this.jobService,
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
        String servletPath = req.getServletPath();
        if ("/home".equals(servletPath)) {
            redirectToRoleHome(req, resp, loginUser);
            return;
        }

        req.setAttribute("currentRequestPath", buildCurrentRequestPath(req));
        setSavedJobFeedback(req);
        setPageError(req);
        String contextPath = req.getContextPath();
        LocalDate today = LocalDate.now();

        if ("/ta/home".equals(servletPath)) {
            if (!"TA".equalsIgnoreCase(loginUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
                return;
            }
            String taStudentId = loginUser.getUserId();
            req.setAttribute("savedJobs", savedJobService.findSavedJobs(taStudentId));
            req.setAttribute("savedJobIds", savedJobService.findSavedJobIds(taStudentId));
            req.setAttribute("taNotifications", taStatusNotificationService.loadForTaDashboard(taStudentId));
            req.setAttribute("unreadNotificationCount", taStatusNotificationService.countUnread(taStudentId));
            req.setAttribute("deadlineReminders",
                    deadlineReminderService.buildRemindersForTa(taStudentId, today, contextPath));
            prepareTaJobs(req, today, taStudentId);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
            return;
        }

        if ("/mo/home".equals(servletPath)) {
            if (!"MO".equalsIgnoreCase(loginUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
                return;
            }
            String moUserId = loginUser.getUserId();
            List<MoNotificationView> moNotifications =
                    moNewApplicationNotificationService.loadForMoDashboard(moUserId, contextPath);
            req.setAttribute("moNotifications", moNotifications);
            req.setAttribute("moUnreadNotificationCount",
                    moNewApplicationNotificationService.countUnread(moUserId));
            req.setAttribute("deadlineReminders",
                    deadlineReminderService.buildRemindersForMo(today, contextPath));
            req.setAttribute("moNotificationPreviewCount", moNotifications.size());
            req.getRequestDispatcher("/WEB-INF/jsp/mo_home.jsp").forward(req, resp);
            return;
        }

        if ("/admin/home".equals(servletPath)) {
            if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
                return;
            }
            req.getRequestDispatcher("/WEB-INF/jsp/admin_home.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
    }

    private void prepareTaJobs(HttpServletRequest req, LocalDate today, String taStudentId) {
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = isTruthy(req.getParameter("showHidden"));

        List<Job> openJobs = jobService.findOpenActiveJobs(today);
        List<Job> hiddenJobs = jobService.findHiddenFromOpenJobs(today);
        List<Job> visibleJobs = showingHidden
                ? jobService.filterJobs(hiddenJobs, keyword, schedule, skills)
                : jobService.filterJobs(openJobs, keyword, schedule, skills);

        req.setAttribute("jobs", visibleJobs);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", visibleJobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenJobs.size());
        req.setAttribute("hiddenPoolCount", hiddenJobs.size());
        req.setAttribute("viewHiddenJobsUrl", buildViewHiddenJobsUrl(req, keyword, schedule, skills));
        req.setAttribute("taUserId", taStudentId);
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

    private static void setPageError(HttpServletRequest req) {
        String error = trimToNull(req.getParameter("error"));
        if (error != null) {
            req.setAttribute("error", error);
        }
    }

    private static String buildCurrentRequestPath(HttpServletRequest req) {
        String query = req.getQueryString();
        String path = req.getRequestURI();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String t = value.trim();
        return "1".equals(t) || "true".equalsIgnoreCase(t) || "yes".equalsIgnoreCase(t);
    }

    private static String buildViewHiddenJobsUrl(HttpServletRequest req, String keyword,
                                                 String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        parts.add("showHidden=1");
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "schedule", schedule);
        appendQuery(parts, "skills", skills);
        return req.getContextPath() + "/ta/home?" + String.join("&", parts);
    }

    private static void redirectToRoleHome(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser)
            throws IOException {
        String query = req.getQueryString();
        String target = roleHomePath(loginUser);
        if (query != null && !query.isBlank()) {
            target = target + "?" + query;
        }
        resp.sendRedirect(req.getContextPath() + target);
    }

    private static String roleHomePath(LoginUser loginUser) {
        if (loginUser == null || loginUser.getRole() == null) {
            return "/login";
        }
        String role = loginUser.getRole().trim();
        if ("TA".equalsIgnoreCase(role)) {
            return "/ta/home";
        }
        if ("MO".equalsIgnoreCase(role)) {
            return "/mo/home";
        }
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "/admin/home";
        }
        return "/login";
    }

    private static void appendQuery(List<String> parts, String name, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        parts.add(name + "=" + java.net.URLEncoder.encode(raw, StandardCharsets.UTF_8));
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
