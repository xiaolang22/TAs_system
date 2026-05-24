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

/**
 * Job posting/editing servlet handling MO creation of new positions and
 * modification of existing positions.
 *
 * <p>Handles URL: /mo/post-job
 *
 * <p>Supported operations:
 * <ul>
 *   <li>GET — displays the job posting form (loads existing data for editing if a jobId parameter is present)</li>
 *   <li>POST — creates a new position or updates an existing one</li>
 * </ul>
 *
 * <p>Position fields include: title, description, requirements, hours, schedule, deadline, etc.
 *
 * <p>Access: restricted to the MO role.
 *
 * @author Group 19
 * @see JobService
 */
public class PostJobServlet extends HttpServlet {

    private JobService jobService;

    /**
     * Initialises the Servlet, loads the job data file, and creates the JobService instance.
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
     * Resolves the absolute path of a data file, preferring the web application's real
     * path and falling back to the working directory.
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
     * Handles GET requests: displays the job posting/editing page.
     *
     * <p>If a jobId parameter is provided, loads the existing position data for edit mode.
     *
     * @param req  HTTP request
     * @param resp HTTP response
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
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access post job.");
            return;
        }

        String jobId = trimToNull(req.getParameter("jobId"));
        if (jobId != null) {
            Job existing = jobService.findById(jobId);
            if (existing == null) {
                req.setAttribute("errorMsg", "Job not found.");
            } else if (!jobService.isOwnedBy(existing, loginUser.getUserId())) {
                req.setAttribute("errorMsg", "You can only edit jobs that you posted.");
            } else {
                req.setAttribute("job", existing);
                req.setAttribute("editing", true);
            }
        }

        req.setAttribute("loginUser", loginUser);
        req.getRequestDispatcher("/jsp/post_job.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: creates or updates a job position.
     *
     * <p>If the request includes a non-empty jobId, the existing position is updated;
     * otherwise a new position is created. Redirects on success, returns to the
     * form page with an error message on failure.
     *
     * @param req  HTTP request
     * @param resp HTTP response
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
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can post job.");
            return;
        }

        Job job = new Job();
        job.setJobId(trimToNull(req.getParameter("jobId")));
        job.setTitle(req.getParameter("title"));
        job.setDescription(req.getParameter("description"));
        job.setRequirements(req.getParameter("requirements"));
        job.setHours(req.getParameter("hours"));
        job.setSchedule(req.getParameter("schedule"));
        job.setDeadline(req.getParameter("deadline"));
        job.setOwnerMoUserId(loginUser.getUserId());

        boolean editing = job.getJobId() != null;
        ServiceResult<Job> result = editing
                ? jobService.updateJob(job, loginUser.getUserId())
                : jobService.createJob(job);

        if (result.isSuccess()) {
            String redirectUrl = req.getContextPath() + "/mo/post-job?success=true";
            if (editing) {
                redirectUrl += "&edited=true&jobId="
                        + URLEncoder.encode(job.getJobId(), StandardCharsets.UTF_8);
            }
            resp.sendRedirect(redirectUrl);
            return;
        }

        req.setAttribute("errorMsg", result.getMessage());
        req.setAttribute("job", job);
        req.setAttribute("loginUser", loginUser);
        req.setAttribute("editing", editing);
        req.getRequestDispatcher("/jsp/post_job.jsp").forward(req, resp);
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
