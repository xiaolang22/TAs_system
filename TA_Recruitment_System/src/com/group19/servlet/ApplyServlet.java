package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.ApplicationService;
import com.group19.service.JobService;
import com.group19.util.ApplicationServiceFactory;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Job Application Servlet, handling TA users' job application operations.
 *
 * <p>URL handled: /apply
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Verify the TA's login state</li>
 *   <li>Check that the job exists and is open for applications</li>
 *   <li>Check that the TA has completed their application materials</li>
 *   <li>Submit the application and redirect to the result page</li>
 * </ol>
 *
 * <p>Permissions: accessible only by TA role (POST method).
 *
 * @author Group 19
 * @see ApplicationService
 * @see JobService
 */
public class ApplyServlet extends HttpServlet {
    private ApplicationService applicationService;
    private TADao taDao;
    private JobService jobService;

    /**
     * Initialises the Servlet, loads TA data, application data, and job data files,
     * and creates the relevant service instances.
     */
    @Override
    public void init() {
        Path taFilePath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        this.taDao = new TADao(taFilePath);

        this.applicationService = ApplicationServiceFactory.create(getServletContext());

        Path jobFilePath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        this.jobService = new JobService(new JobDao(jobFilePath));
    }

    /**
     * Handles POST requests: executes the TA's job application operation.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify the user's login state and TA role</li>
     *   <li>Validate the job ID parameter</li>
     *   <li>Check that the job exists and is open for applications</li>
     *   <li>Check that the TA has completed their profile</li>
     *   <li>Call the service layer to submit the application</li>
     *   <li>Redirect based on the result (success or failure)</li>
     * </ol>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.isBlank()) {
            resp.sendRedirect(buildHomeErrorUrl(req, "Job ID is required."));
            return;
        }

        Job job = jobService.findById(jobId.trim());
        if (job == null || !jobService.isOpenForApplication(job, LocalDate.now())) {
            resp.sendRedirect(buildHomeErrorUrl(req, "This job is not currently available for application."));
            return;
        }

        String taStudentId = loginUser.getUserId();
        TA taProfile = taDao.findByStudentId(taStudentId);
        if (taProfile == null) {
            resp.sendRedirect(buildHomeErrorUrl(req, "Please complete your application materials before applying."));
            return;
        }

        String cvFilePath = taProfile.getCvFilePath();
        ServiceResult<Application> result = applicationService.applyForJob(
                jobId,
                taStudentId,
                taProfile.getName(),
                cvFilePath
        );

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/home?applySuccess=true");
        } else {
            resp.sendRedirect(buildHomeErrorUrl(req, result.getMessage()));
        }
    }

    /**
     * Builds a home page redirect URL with an error message.
     *
     * @param req     the HTTP request, used to obtain the context path
     * @param message the error message
     * @return the full redirect URL
     */
    private String buildHomeErrorUrl(HttpServletRequest req, String message) {
        return req.getContextPath() + "/home?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}
