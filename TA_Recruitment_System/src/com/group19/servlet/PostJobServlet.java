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

public class PostJobServlet extends HttpServlet {

    private JobService jobService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath);
        this.jobService = new JobService(new JobDao(jobFilePath));
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

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
                req.setAttribute("errorMsg", "未找到对应岗位。");
            } else if (!jobService.isOwnedBy(existing, loginUser.getUserId())) {
                req.setAttribute("errorMsg", "你只能修改自己发布的岗位。");
            } else {
                req.setAttribute("job", existing);
                req.setAttribute("editing", true);
            }
        }

        req.setAttribute("loginUser", loginUser);
        req.getRequestDispatcher("/jsp/post_job.jsp").forward(req, resp);
    }

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

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
