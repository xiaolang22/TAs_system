package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.JobService;
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

/**
 * MO job management servlet, handling Module Organiser management and viewing
 * of their own posted jobs.
 *
 * <p>URL handled: /mo/jobs
 *
 * <p>Supported features:
 * <ul>
 *   <li>View the list of own posted jobs (default view)</li>
 *   <li>View all jobs (showAll=1)</li>
 *   <li>Filter jobs by keyword, schedule, and skills</li>
 *   <li>View hidden/expired jobs</li>
 *   <li>View job details (jobId parameter)</li>
 *   <li>Delete own posted jobs</li>
 * </ul>
 *
 * <p>Permissions: accessible only by MO role.
 *
 * @author Group 19
 * @see JobService
 */
public class MoJobListServlet extends HttpServlet {
    private JobService jobService;

    /**
     * Initialises the Servlet, loads the job data file, and creates the JobService
     * instance.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;

        Path jobFilePath = resolveDataPath(relativePath);
        this.jobService = new JobService(new JobDao(jobFilePath));
    }

    /**
     * Handles GET requests: displays the MO's job management list or job details.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify MO login identity</li>
     *   <li>If jobId is provided, render the job detail page (mo_job_detail.jsp)</li>
     *   <li>Otherwise, display own or all jobs based on the showAll parameter
     *       (mo_job_list.jsp)</li>
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

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access job management.");
            return;
        }

        String jobId = trimToNull(req.getParameter("jobId"));
        if (jobId != null) {
            renderDetail(req, resp, loginUser, jobId);
            return;
        }

        boolean showAll = isTruthy(req.getParameter("showAll"));
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = showAll && isTruthy(req.getParameter("showHidden"));
        LocalDate today = LocalDate.now();

        List<Job> allJobs = jobService.findAllJobs();
        List<Job> ownedJobs = jobService.findJobsOwnedBy(loginUser.getUserId());
        List<Job> jobs = showAll
                ? buildAllJobsView(today, keyword, schedule, skills, showingHidden, req, allJobs)
                : buildOwnedJobsView(keyword, schedule, skills, req, ownedJobs);

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("jobs", jobs);
        req.setAttribute("showAll", showAll);
        req.setAttribute("ownedJobCount", ownedJobs.size());
        req.setAttribute("allJobCount", allJobs.size());
        String currentListUrl = buildListUrl(req, showAll, showingHidden, keyword, schedule, skills);
        req.setAttribute("detailLinkPrefix",
                currentListUrl + (currentListUrl.contains("?") ? "&" : "?") + "jobId=");
        req.getRequestDispatcher("/WEB-INF/jsp/mo_job_list.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: performs the delete job operation.
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

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access job management.");
            return;
        }

        String action = trimToNull(req.getParameter("action"));
        if (!"delete".equalsIgnoreCase(action)) {
            resp.sendRedirect(req.getContextPath() + "/mo/jobs");
            return;
        }

        ServiceResult<Void> result = jobService.deleteJob(req.getParameter("jobId"), loginUser.getUserId());
        String target = buildListUrl(req,
                isTruthy(req.getParameter("showAll")),
                isTruthy(req.getParameter("showHidden")),
                trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q"))),
                trimToNull(req.getParameter("schedule")),
                trimToNull(req.getParameter("skills")));
        String separator = target.contains("?") ? "&" : "?";
        if (result.isSuccess()) {
            resp.sendRedirect(target + separator + "success=" + encode(result.getMessage()));
            return;
        }
        resp.sendRedirect(target + separator + "error=" + encode(result.getMessage()));
    }

    /**
     * Builds the list view for all jobs (distinguishing open and hidden jobs).
     *
     * @param today         the current date
     * @param keyword       the filter keyword
     * @param schedule      the schedule filter
     * @param skills        the skills filter
     * @param showingHidden whether to show hidden jobs
     * @param req           the HTTP request
     * @param allJobs       the list of all jobs
     * @return the filtered list of jobs
     */
    private List<Job> buildAllJobsView(LocalDate today, String keyword, String schedule, String skills,
                                       boolean showingHidden, HttpServletRequest req, List<Job> allJobs) {
        List<Job> openJobs = new ArrayList<>();
        List<Job> hiddenJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (jobService.isOpenForListing(job, today)) {
                openJobs.add(job);
            } else {
                hiddenJobs.add(job);
            }
        }
        List<Job> source = showingHidden ? hiddenJobs : openJobs;
        List<Job> filteredJobs = jobService.filterJobs(source, keyword, schedule, skills);
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", filteredJobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenJobs.size());
        req.setAttribute("hiddenPoolCount", hiddenJobs.size());
        req.setAttribute("viewHiddenJobsUrl", buildListUrl(req, true, true, keyword, schedule, skills));
        req.setAttribute("viewOpenJobsUrl", buildListUrl(req, true, false, keyword, schedule, skills));
        return filteredJobs;
    }

    /**
     * Builds the list view containing only jobs posted by the current MO.
     *
     * @param keyword   the filter keyword
     * @param schedule  the schedule filter
     * @param skills    the skills filter
     * @param req       the HTTP request
     * @param ownedJobs the jobs posted by the MO
     * @return the filtered list of jobs
     */
    private List<Job> buildOwnedJobsView(String keyword, String schedule, String skills,
                                         HttpServletRequest req, List<Job> ownedJobs) {
        List<Job> filteredJobs = jobService.filterJobs(ownedJobs, keyword, schedule, skills);
        req.setAttribute("showingHidden", false);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("filteredCount", filteredJobs.size());
        return filteredJobs;
    }

    /**
     * Renders the job detail page.
     *
     * @param req       the HTTP request
     * @param resp      the HTTP response
     * @param loginUser the current MO user
     * @param jobId     the job ID
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void renderDetail(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser, String jobId)
            throws ServletException, IOException {
        boolean showAll = isTruthy(req.getParameter("showAll"));
        boolean showHidden = showAll && isTruthy(req.getParameter("showHidden"));
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));

        Job job = jobService.findById(jobId);
        if (job == null) {
            req.setAttribute("errorMsg", "Job not found.");
        } else {
            req.setAttribute("job", job);
            req.setAttribute("isOwnedJob", jobService.isOwnedBy(job, loginUser.getUserId()));
        }
        req.setAttribute("backUrl", buildListUrl(req, showAll, showHidden, keyword, schedule, skills));
        req.getRequestDispatcher("/WEB-INF/jsp/mo_job_detail.jsp").forward(req, resp);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's
     * real path and falling back to the working directory.
     *
     * @param webRelativePath the web-relative path
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

    /**
     * Builds the job list URL with query parameters.
     *
     * @param req        the HTTP request
     * @param showAll    whether to show all jobs
     * @param showHidden whether to show hidden jobs
     * @param keyword    the filter keyword
     * @param schedule   the schedule filter
     * @param skills     the skills filter
     * @return the full URL for the job list page
     */
    private static String buildListUrl(HttpServletRequest req, boolean showAll, boolean showHidden,
                                       String keyword, String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        if (showAll) {
            parts.add("showAll=1");
        }
        if (showHidden) {
            parts.add("showHidden=1");
        }
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "schedule", schedule);
        appendQuery(parts, "skills", skills);
        if (parts.isEmpty()) {
            return req.getContextPath() + "/mo/jobs";
        }
        return req.getContextPath() + "/mo/jobs?" + String.join("&", parts);
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
        parts.add(name + "=" + encode(raw));
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

    /**
     * Converts a null string to an empty string.
     *
     * @param value the input string
     * @return a non-null string
     */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * URL-encodes a string.
     *
     * @param value the raw string
     * @return the URL-encoded string
     */
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
        String normalized = value.trim();
        return "1".equals(normalized) || "true".equalsIgnoreCase(normalized) || "yes".equalsIgnoreCase(normalized);
    }
}
