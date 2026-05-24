package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.SavedJobDao;
import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.AdminDashboardData;
import com.group19.dto.MoTaCandidateCard;
import com.group19.dto.MoNotificationView;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.AdminDashboardService;
import com.group19.service.DeadlineReminderService;
import com.group19.service.JobService;
import com.group19.service.MoNewApplicationNotificationService;
import com.group19.service.MoTaDirectoryService;
import com.group19.service.SavedJobService;
import com.group19.service.TaStatusNotificationService;
import com.group19.service.WorkloadService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Home page routing Servlet, dispatching requests to the corresponding home page
 * view based on the user's role.
 *
 * <p>URL patterns handled:
 * <ul>
 *   <li>/home -- generic home entry point, redirects based on role</li>
 *   <li>/ta/home -- Teaching Assistant home, displaying available jobs, saved jobs,
 *       notifications, etc.</li>
 *   <li>/mo/home -- Module Organiser home, displaying candidate TA directory,
 *       notifications, job overview, etc.</li>
 *   <li>/admin/home -- Administrator home, displaying system dashboard statistics</li>
 * </ul>
 *
 * <p>Permissions: accessible by all logged-in users; different content is shown
 * based on role.
 *
 * @author Group 19
 * @see JobService
 * @see MoTaDirectoryService
 * @see AdminDashboardService
 */
public class HomeServlet extends HttpServlet {
    private JobDao jobDao;
    private ApplicationDao applicationDao;
    private TADao taDao;
    private JobService jobService;
    private SavedJobService savedJobService;
    private TaStatusNotificationService taStatusNotificationService;
    private MoNewApplicationNotificationService moNewApplicationNotificationService;
    private DeadlineReminderService deadlineReminderService;
    private MoTaDirectoryService moTaDirectoryService;
    private AdminDashboardService adminDashboardService;

    /**
     * Initialises the Servlet, loads all data files, and creates the various service instances.
     */
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
        Path taPath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        Path userPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");

        this.jobDao = new JobDao(jobPath);
        this.applicationDao = new ApplicationDao(applicationPath);
        this.jobService = new JobService(jobDao);
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobPath), jobDao);
        this.deadlineReminderService = new DeadlineReminderService(
                this.jobService,
                applicationDao,
                savedJobService);
        NotificationDao notificationDao = new NotificationDao(notificationPath);
        this.taStatusNotificationService = new TaStatusNotificationService(notificationDao, jobDao);
        this.moNewApplicationNotificationService =
                new MoNewApplicationNotificationService(notificationDao, jobDao);
        this.moTaDirectoryService = new MoTaDirectoryService(new TADao(taPath), new UserAccountDao(userPath));
        this.adminDashboardService = new AdminDashboardService(
                new UserAccountDao(userPath),
                new ApplicationDao(applicationPath),
                new JobDao(jobPath),
                new TADao(taPath));
    }

    /**
     * Handles GET requests: routes to the corresponding home page view based on the
     * user's role and servletPath.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify the user's login state</li>
     *   <li>For the /home path, redirect to the role-specific home page</li>
     *   <li>For /ta/home, load TA home page data (jobs, notifications, saved jobs, etc.)</li>
     *   <li>For /mo/home, load MO home page data (notifications, job statistics,
     *       candidate TA directory, etc.)</li>
     *   <li>For /admin/home, load administrator dashboard statistics</li>
     * </ol>
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
                    deadlineReminderService.buildRemindersForMo(moUserId, today, contextPath));
            req.setAttribute("moNotificationPreviewCount", moNotifications.size());
            req.setAttribute("moOwnedJobCount", jobService.findJobsOwnedBy(moUserId).size());
            req.setAttribute("moAllJobCount", jobService.findAllJobs().size());
            prepareMoCandidates(req);
            req.getRequestDispatcher("/WEB-INF/jsp/mo_home.jsp").forward(req, resp);
            return;
        }

        if ("/admin/home".equals(servletPath)) {
            if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
                return;
            }
            prepareAdminDashboardStats(req);
            req.getRequestDispatcher("/WEB-INF/jsp/admin_home.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + roleHomePath(loginUser));
    }

    /**
     * Prepares job data for the TA home page, supporting keyword, schedule, and skill
     * filtering as well as viewing hidden jobs.
     *
     * @param req         the HTTP request
     * @param today       the current date
     * @param taStudentId the TA's student ID
     */
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

    /**
     * Prepares candidate TA data for the MO home page, supporting keyword/programme/
     * availability filtering and skill-match mode.
     *
     * @param req the HTTP request
     */
    private void prepareMoCandidates(HttpServletRequest req) {
        String filterMode = normalizeMoFilterMode(req.getParameter("mode"));
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String programme = trimToNull(req.getParameter("programme"));
        String availability = trimToNull(req.getParameter("availability"));
        String requiredSkills = trimToNull(req.getParameter("requiredSkills"));

        ServiceResult<List<MoTaCandidateCard>> allResult = moTaDirectoryService.loadAllCandidates();
        List<MoTaCandidateCard> allCandidates = allResult.isSuccess() ? allResult.getData() : new ArrayList<>();
        attachMoCandidateUrls(req, allCandidates);

        List<MoTaCandidateCard> visibleCandidates = allCandidates;
        boolean showMatchDetails = false;

        if (!allResult.isSuccess()) {
            req.setAttribute("moCandidateError", allResult.getMessage());
        } else if ("match".equals(filterMode)) {
            if (requiredSkills == null) {
                req.setAttribute("moCandidateInfo", "Enter required job skills to rank TAs by match score.");
            } else {
                ServiceResult<List<MoTaCandidateCard>> matchResult =
                        moTaDirectoryService.matchCandidates(allCandidates, requiredSkills);
                if (matchResult.isSuccess()) {
                    visibleCandidates = matchResult.getData();
                    attachMoCandidateUrls(req, visibleCandidates);
                    showMatchDetails = true;
                } else {
                    visibleCandidates = new ArrayList<>();
                    req.setAttribute("moCandidateError", matchResult.getMessage());
                }
            }
        } else {
            visibleCandidates = moTaDirectoryService.filterCandidates(allCandidates, keyword, programme, availability);
        }

        req.setAttribute("moCandidates", visibleCandidates);
        req.setAttribute("moFilterMode", filterMode);
        req.setAttribute("moFilterKeyword", nullToEmpty(keyword));
        req.setAttribute("moFilterProgramme", nullToEmpty(programme));
        req.setAttribute("moFilterAvailability", nullToEmpty(availability));
        req.setAttribute("moRequiredSkills", nullToEmpty(requiredSkills));
        req.setAttribute("moCandidateTotalCount", allCandidates.size());
        req.setAttribute("moCandidateFilteredCount", visibleCandidates.size());
        req.setAttribute("moShowMatchDetails", showMatchDetails);
    }

    /**
     * Prepares administrator dashboard statistics and binds them to request attributes.
     *
     * @param req the HTTP request
     */
    private void prepareAdminDashboardStats(HttpServletRequest req) {
        ServiceResult<AdminDashboardData> result = adminDashboardService.loadDashboard(
                trimToNull(req.getParameter("selectedTaId")),
                trimToNull(req.getParameter("selectedMoId")),
                trimToNull(req.getParameter("selectedJobId")));
        if (!result.isSuccess()) {
            req.setAttribute("errorMsg", result.getMessage());
            return;
        }

        AdminDashboardData data = result.getData();
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
     * Attaches context-relative URLs (avatar, CV preview, etc.) to candidate TA cards.
     *
     * @param req        the HTTP request
     * @param candidates the list of candidate TA cards
     */
    private static void attachMoCandidateUrls(HttpServletRequest req, List<MoTaCandidateCard> candidates) {
        if (candidates == null) {
            return;
        }
        String contextPath = req.getContextPath();
        for (MoTaCandidateCard candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String avatarPath = candidate.getAvatarPath();
            candidate.setAvatarUrl(avatarPath == null || avatarPath.isBlank() ? "" : contextPath + avatarPath);
            String cvFilePath = candidate.getCvFilePath();
            boolean resumeAvailable = hasUploadedResume(req, cvFilePath);
            candidate.setResumeAvailable(resumeAvailable);
            candidate.setCvUrl(resumeAvailable ? contextPath + cvFilePath : "");
        }
    }

    /**
     * Checks whether the specified CV file exists on the server.
     *
     * @param req        the HTTP request
     * @param cvFilePath the CV file path
     * @return true if the file exists
     */
    private static boolean hasUploadedResume(HttpServletRequest req, String cvFilePath) {
        if (cvFilePath == null || cvFilePath.isBlank()) {
            return false;
        }
        String normalized = cvFilePath.trim().replace("/", java.io.File.separator);
        if (normalized.startsWith(java.io.File.separator)) {
            normalized = normalized.substring(1);
        }
        String webRelative = "/" + normalized.replace(java.io.File.separatorChar, '/');
        String realPath = req.getServletContext().getRealPath(webRelative);
        if (realPath != null && !realPath.isBlank()) {
            return Files.exists(Paths.get(realPath));
        }
        return Files.exists(Paths.get(System.getProperty("user.dir"), "web", normalized));
    }

    /**
     * Sets saved-job operation feedback messages into request attributes.
     *
     * @param req the HTTP request
     */
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

    /**
     * Sets page-level error messages into request attributes.
     *
     * @param req the HTTP request
     */
    private static void setPageError(HttpServletRequest req) {
        String error = trimToNull(req.getParameter("error"));
        if (error != null) {
            req.setAttribute("error", error);
        }
    }

    /**
     * Builds the full path of the current request (including query parameters).
     *
     * @param req the HTTP request
     * @return the request path string
     */
    private static String buildCurrentRequestPath(HttpServletRequest req) {
        String query = req.getQueryString();
        String path = req.getRequestURI();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }

    /**
     * Determines whether a string value is truthy (1, true, yes).
     *
     * @param value the input string
     * @return true if the value is truthy
     */
    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String t = value.trim();
        return "1".equals(t) || "true".equalsIgnoreCase(t) || "yes".equalsIgnoreCase(t);
    }

    /**
     * Builds the URL for viewing hidden jobs.
     *
     * @param req      the HTTP request
     * @param keyword  the filter keyword
     * @param schedule the schedule filter
     * @param skills   the skills filter
     * @return the URL for the hidden job list
     */
    private static String buildViewHiddenJobsUrl(HttpServletRequest req, String keyword,
                                                 String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        parts.add("showHidden=1");
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "schedule", schedule);
        appendQuery(parts, "skills", skills);
        return req.getContextPath() + "/ta/home?" + String.join("&", parts);
    }

    /**
     * Redirects the generic /home path to the home page corresponding to the user's role.
     *
     * @param req       the HTTP request
     * @param resp      the HTTP response
     * @param loginUser the currently logged-in user
     * @throws IOException if an I/O error occurs
     */
    private static void redirectToRoleHome(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser)
            throws IOException {
        String query = req.getQueryString();
        String target = roleHomePath(loginUser);
        if (query != null && !query.isBlank()) {
            target = target + "?" + query;
        }
        resp.sendRedirect(req.getContextPath() + target);
    }

    /**
     * Returns the home page path corresponding to the user's role.
     *
     * @param loginUser the logged-in user
     * @return the role-specific home page path
     */
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

    /**
     * Appends a non-blank parameter to the query parameter list.
     *
     * @param parts the query parameter list
     * @param name  the parameter name
     * @param raw   the parameter value
     */
    private static void appendQuery(List<String> parts, String name, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        parts.add(name + "=" + java.net.URLEncoder.encode(raw, StandardCharsets.UTF_8));
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

    /**
     * Normalises the MO candidate filter mode, accepting only "match"; otherwise
     * defaults to "keyword".
     *
     * @param mode the mode parameter
     * @return the normalised filter mode
     */
    private static String normalizeMoFilterMode(String mode) {
        return "match".equalsIgnoreCase(mode) ? "match" : "keyword";
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
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Converts a null string to an empty string.
     *
     * @param value the input string
     * @return a non-null string
     */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
