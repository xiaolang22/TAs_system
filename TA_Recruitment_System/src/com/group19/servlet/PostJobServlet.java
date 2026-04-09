package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
        req.getRequestDispatcher("/jsp/post_job.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Job job = new Job();
        job.setTitle(req.getParameter("title"));
        job.setCategory(req.getParameter("category"));
        job.setDescription(req.getParameter("description"));
        job.setRequirements(req.getParameter("requirements"));
        job.setHours(req.getParameter("hours"));
        job.setSchedule(req.getParameter("schedule"));
        job.setDeadline(req.getParameter("deadline"));

        ServiceResult<Job> result = jobService.createJob(job);

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/mo/post-job?success=true");
        } else {
            req.setAttribute("errorMsg", result.getMessage());
            req.setAttribute("job", job);
            req.getRequestDispatcher("/jsp/post_job.jsp").forward(req, resp);
        }
    }
}