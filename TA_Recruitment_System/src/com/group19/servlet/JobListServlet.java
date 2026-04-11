package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.model.Job;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JobListServlet extends HttpServlet {
    private JobDao jobDao;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath);
        this.jobDao = new JobDao(jobFilePath);
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
            List<Job> jobs = jobDao.findAll();
            for (Job job : jobs) {
                if (jobId.equals(job.getJobId())) {
                    req.setAttribute("job", job);
                    break;
                }
            }
            req.getRequestDispatcher("/WEB-INF/jsp/job_detail.jsp").forward(req, resp);
            return;
        }

        List<Job> jobs = jobDao.findAll();
        req.setAttribute("jobs", jobs);
        req.getRequestDispatcher("/WEB-INF/jsp/job_list.jsp").forward(req, resp);
    }
}
