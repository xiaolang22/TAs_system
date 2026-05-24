package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.TARecommendation;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.RecommendationService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AdminRecommendationServlet extends HttpServlet {
    private JobDao jobDao;
    private TADao taDao;
    private RecommendationService recommendationService;

    @Override
    public void init() {
        Path jobPath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        Path taPath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        Path applicationPath = DataPathResolver.resolve(
                getServletContext(), "applicationDataFile", "/data/applications.json", "applications.json");
        this.jobDao = new JobDao(jobPath);
        this.taDao = new TADao(taPath);
        this.recommendationService = new RecommendationService(new ApplicationDao(applicationPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentAdmin(req, resp);
        if (loginUser == null) {
            return;
        }

        List<Job> jobs = jobDao.findAll();
        Job selectedJob = resolveSelectedJob(jobs, req.getParameter("jobId"));
        List<TARecommendation> recommendations = new ArrayList<>();
        if (selectedJob == null) {
            req.setAttribute("errorMsg", "No jobs are available for recommendation.");
        } else {
            recommendations = recommendationService.recommend(selectedJob, taDao.findAll());
        }

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("jobs", jobs);
        req.setAttribute("selectedJob", selectedJob);
        req.setAttribute("selectedJobId", selectedJob == null ? "" : selectedJob.getJobId());
        req.setAttribute("recommendations", recommendations);
        req.getRequestDispatcher("/WEB-INF/jsp/admin_recommendations.jsp").forward(req, resp);
    }

    private static Job resolveSelectedJob(List<Job> jobs, String requestedJobId) {
        if (jobs == null || jobs.isEmpty()) {
            return null;
        }
        if (requestedJobId != null && !requestedJobId.isBlank()) {
            for (Job job : jobs) {
                if (job != null && requestedJobId.trim().equalsIgnoreCase(job.getJobId())) {
                    return job;
                }
            }
        }
        return jobs.get(0);
    }

    private LoginUser currentAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can access recommendations.");
            return null;
        }
        return loginUser;
    }
}
