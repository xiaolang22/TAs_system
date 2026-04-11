package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.service.ApplicationService;
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

    @Override
    public void init() {
        String appDataPath = getServletContext().getInitParameter("applicationDataFile");
        String appRelativePath = appDataPath == null || appDataPath.isBlank()
                ? "/data/applications.json"
                : appDataPath;

        Path appFilePath = resolveDataPath(appRelativePath);
        this.applicationService = new ApplicationService(new ApplicationDao(appFilePath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String jobId = req.getParameter("jobId");
        List<Application> applications = new ArrayList<>();

        if (jobId == null || jobId.isBlank()) {
            req.setAttribute("errorMsg", "Job ID is required");
        } else {
            applications = applicationService.getApplicationsByJobId(jobId);
        }

        req.setAttribute("jobId", jobId);
        req.setAttribute("applications", applications);
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
        req.setAttribute("jobId", jobId);
        req.setAttribute("applications", applications);
        req.setAttribute("errorMsg", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/manage_applications.jsp").forward(req, resp);
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "applications.json");
    }
}