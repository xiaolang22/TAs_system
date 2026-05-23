package com.group19.servlet;

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

public class MoJobListServlet extends HttpServlet {
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

        boolean showAll = isTruthy(req.getParameter("showAll"));
        List<Job> allJobs = jobService.findAllJobs();
        List<Job> ownedJobs = jobService.findJobsOwnedBy(loginUser.getUserId());
        List<Job> jobs = showAll ? allJobs : ownedJobs;

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("jobs", jobs);
        req.setAttribute("showAll", showAll);
        req.setAttribute("ownedJobCount", ownedJobs.size());
        req.setAttribute("allJobCount", allJobs.size());
        req.getRequestDispatcher("/WEB-INF/jsp/mo_job_list.jsp").forward(req, resp);
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim();
        return "1".equals(normalized) || "true".equalsIgnoreCase(normalized) || "yes".equalsIgnoreCase(normalized);
    }
}
