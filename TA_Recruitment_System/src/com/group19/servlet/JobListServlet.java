package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.JobService;
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

public class JobListServlet extends HttpServlet {
    private JobService jobService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath);

        String appConfiguredPath = getServletContext().getInitParameter("applicationDataFile");
        String appRelativePath = appConfiguredPath == null || appConfiguredPath.isBlank()
                ? "/data/applications.json"
                : appConfiguredPath;
        Path appFilePath = resolveDataPath(appRelativePath);

        this.jobService = new JobService(new JobDao(jobFilePath), new ApplicationDao(appFilePath));
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String jobId = req.getParameter("jobId");
        if (jobId != null && !jobId.isBlank()) {
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }
            req.setAttribute("job", job);
            req.getRequestDispatcher("/WEB-INF/jsp/job_detail.jsp").forward(req, resp);
            return;
        }

        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        String status = req.getParameter("status");
        String deadlineFrom = req.getParameter("deadlineFrom");
        String deadlineTo = req.getParameter("deadlineTo");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        String excludeAppliedForTaStudentId = null;
        if (loginUser != null && "TA".equalsIgnoreCase(loginUser.getRole())) {
            excludeAppliedForTaStudentId = loginUser.getUserId();
        }

        List<Job> jobs = jobService.searchJobs(
                keyword,
                category,
                status,
                deadlineFrom,
                deadlineTo,
                excludeAppliedForTaStudentId
        );
        req.setAttribute("jobs", jobs);
        req.getRequestDispatcher("/WEB-INF/jsp/job_list.jsp").forward(req, resp);
    }
}
