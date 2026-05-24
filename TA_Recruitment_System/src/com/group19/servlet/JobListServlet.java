package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dao.SavedJobDao;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.JobService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Job List Servlet, handling job browsing and detail viewing.
 *
 * <p>URL patterns handled:
 * <ul>
 *   <li>/jobs -- job list page, with filtering and search support</li>
 *   <li>/jobs?jobId=xxx -- job detail page</li>
 * </ul>
 *
 * <p>Supported features:
 * <ul>
 *   <li>Filter jobs by keyword, schedule, and skills</li>
 *   <li>View the hidden jobs pool</li>
 *   <li>View job details</li>
 *   <li>TA users can view saved status</li>
 * </ul>
 *
 * @author Group 19
 * @see JobService
 * @see SavedJobService
 */
public class JobListServlet extends HttpServlet {
    private JobDao jobDao;
    private JobService jobService;
    private SavedJobService savedJobService;

    /**
     * Initialises the Servlet, loads job data and saved-job data files,
     * and creates the JobService and SavedJobService instances.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath, "jobs.json");
        this.jobDao = new JobDao(jobFilePath);
        this.jobService = new JobService(jobDao);

        String savedJobDataPath = getServletContext().getInitParameter("savedJobDataFile");
        String savedJobRelativePath = savedJobDataPath == null || savedJobDataPath.isBlank()
                ? "/data/saved_jobs.json"
                : savedJobDataPath;
        Path savedJobFilePath = resolveDataPath(savedJobRelativePath, "saved_jobs.json");
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobFilePath), jobDao);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's real
     * path and falling back to the data directory under the working directory.
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
     * Handles GET requests: displays the job list or job details.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Set request/response encoding and cache control</li>
     *   <li>If a jobId is provided, query and display the job details (job_detail.jsp)</li>
     *   <li>Otherwise, display the job list based on filter criteria (job_list.jsp)</li>
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
        preventCaching(resp);

        LocalDate today = LocalDate.now();
        LoginUser loginUser = currentLoginUser(req);
        if (loginUser != null) {
            req.setAttribute("loginUser", loginUser);
        }
        req.setAttribute("currentRequestPath", buildCurrentRequestPath(req));
        setSavedJobFeedback(req);

        String jobId = req.getParameter("jobId");
        if (jobId != null && !jobId.isBlank()) {
            Job job = jobDao.findById(jobId.trim());
            req.setAttribute("job", job);
            if (job != null && !jobService.isOpenForApplication(job, today)) {
                req.setAttribute("applyBlockedReason",
                        "This job is closed or its application deadline has passed.");
            }
            applySavedState(req, loginUser, job);
            req.getRequestDispatcher("/WEB-INF/jsp/job_detail.jsp").forward(req, resp);
            return;
        }

        renderJobList(req, today, loginUser);
        req.getRequestDispatcher("/WEB-INF/jsp/job_list.jsp").forward(req, resp);
    }

    /**
     * Renders the job list, supporting keyword, schedule, and skill filtering,
     * as well as switching between open and hidden job views.
     *
     * @param req       the HTTP request
     * @param today     the current date
     * @param loginUser the currently logged-in user
     */
    private void renderJobList(HttpServletRequest req, LocalDate today, LoginUser loginUser) {
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = isTruthy(req.getParameter("showHidden"));

        List<Job> openJobs = jobService.findOpenActiveJobs(today);
        List<Job> hiddenPool = jobService.findHiddenFromOpenJobs(today);
        int hiddenFromOpenCount = hiddenPool.size();

        List<Job> jobs;
        if (showingHidden) {
            jobs = jobService.filterJobs(hiddenPool, keyword, schedule, skills);
        } else {
            jobs = jobService.filterJobs(openJobs, keyword, schedule, skills);
        }

        req.setAttribute("jobs", jobs);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", jobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenFromOpenCount);
        req.setAttribute("hiddenPoolCount", hiddenPool.size());
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("viewHiddenJobsUrl", buildViewHiddenJobsUrl(req, keyword, schedule, skills));

        if (isTa(loginUser)) {
            Set<String> savedJobIds = savedJobService.findSavedJobIds(loginUser.getUserId());
            req.setAttribute("savedJobIds", savedJobIds);
        }
    }

    /**
     * Sets the saved state of a job into request attributes (TA users only).
     *
     * @param req       the HTTP request
     * @param loginUser the currently logged-in user
     * @param job       the target job
     */
    private void applySavedState(HttpServletRequest req, LoginUser loginUser, Job job) {
        if (job != null && isTa(loginUser)) {
            req.setAttribute("jobSaved", savedJobService.isSaved(loginUser.getUserId(), job.getJobId()));
        }
    }

    /**
     * Retrieves the logged-in user from the current session.
     *
     * @param req the HTTP request
     * @return the logged-in user, or null if not logged in
     */
    private static LoginUser currentLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    /**
     * Determines whether the current user has a TA role.
     *
     * @param loginUser the logged-in user
     * @return true if the user has the TA role
     */
    private static boolean isTa(LoginUser loginUser) {
        return loginUser != null && "TA".equalsIgnoreCase(loginUser.getRole());
    }

    /**
     * Sets saved-job feedback messages into request attributes.
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
        return req.getContextPath() + "/jobs?" + String.join("&", parts);
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
        parts.add(name + "=" + URLEncoder.encode(raw, StandardCharsets.UTF_8));
    }

    /**
     * Sets response headers to prevent the browser from caching the page.
     *
     * @param resp the HTTP response
     */
    private static void preventCaching(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
    }

    /**
     * Returns the first non-blank value from two strings.
     *
     * @param a the preferred value
     * @param b the fallback value
     * @return the first non-blank string
     */
    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b;
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
