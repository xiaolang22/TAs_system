package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.TARecommendation;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.service.ApplicationService;
import com.group19.service.RecommendationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ManageApplicationsServlet extends HttpServlet {
    private ApplicationService applicationService;
    private RecommendationService recommendationService;
    private JobDao jobDao;

    @Override
    public void init() {
        String appDataPath = getServletContext().getInitParameter("applicationDataFile");
        String appRelativePath = appDataPath == null || appDataPath.isBlank()
                ? "/data/applications.json"
                : appDataPath;

        Path appFilePath = resolveDataPath(appRelativePath);
        ApplicationDao applicationDao = new ApplicationDao(appFilePath);
        this.applicationService = new ApplicationService(applicationDao);

        String jobDataPath = getServletContext().getInitParameter("jobDataFile");
        String jobRelativePath = jobDataPath == null || jobDataPath.isBlank()
                ? "/data/jobs.json"
                : jobDataPath;
        Path jobFilePath = resolveDataPath(jobRelativePath);
        this.jobDao = new JobDao(jobFilePath);

        String taDataPath = getServletContext().getInitParameter("taDataFile");
        String taRelativePath = taDataPath == null || taDataPath.isBlank()
                ? "/data/tas.json"
                : taDataPath;
        Path taFilePath = resolveDataPath(taRelativePath);
        this.recommendationService = new RecommendationService(new TADao(taFilePath), applicationDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String jobId = req.getParameter("jobId");
        List<Application> applications = new ArrayList<>();
        List<TARecommendation> recommendations = new ArrayList<>();
        Job job = null;

        if (jobId == null || jobId.isBlank()) {
            req.setAttribute("errorMsg", "Job ID is required");
        } else {
            applications = applicationService.getApplicationsByJobId(jobId);
            job = findJobById(jobId);
            if (job == null) {
                req.setAttribute("errorMsg", "Job not found");
            } else {
                recommendations = recommendationService.recommendApplicants(job, applications);
            }
        }

        req.setAttribute("jobId", jobId);
        req.setAttribute("job", job);
        req.setAttribute("applications", applications);
        req.setAttribute("recommendations", recommendations);
        req.getRequestDispatcher("/WEB-INF/jsp/manage_applications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String jobId = req.getParameter("jobId");
        String applicationId = req.getParameter("applicationId");
        String status = req.getParameter("status");
        String decisionNote = req.getParameter("decisionNote");

        ServiceResult<Application> result = applicationService.updateApplicationStatus(
                applicationId,
                status,
                decisionNote
        );

        if (result.isSuccess()) {
            String encodedJobId = URLEncoder.encode(jobId == null ? "" : jobId, StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/mo/applications?jobId=" + encodedJobId + "&updated=true");
            return;
        }

        List<Application> applications = applicationService.getApplicationsByJobId(jobId);
        Job job = findJobById(jobId);
        List<TARecommendation> recommendations = job == null
                ? new ArrayList<>()
                : recommendationService.recommendApplicants(job, applications);
        req.setAttribute("jobId", jobId);
        req.setAttribute("job", job);
        req.setAttribute("applications", applications);
        req.setAttribute("recommendations", recommendations);
        req.setAttribute("errorMsg", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/manage_applications.jsp").forward(req, resp);
    }

    private Job findJobById(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return null;
        }

        for (Job job : jobDao.findAll()) {
            if (jobId.equalsIgnoreCase(job.getJobId())) {
                return job;
            }
        }
        return null;
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "applications.json");
    }
}
