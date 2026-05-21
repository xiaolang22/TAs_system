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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

public class ApplyServlet extends HttpServlet {
    private ApplicationService applicationService;
    private TADao taDao;
    private JobService jobService;

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
            req.setAttribute("error", "Job ID is missing");
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
            return;
        }

        Job job = jobService.findById(jobId.trim());
        if (job == null || !jobService.isOpenForApplication(job, LocalDate.now())) {
            req.setAttribute("error", "This job is not open for applications.");
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
            return;
        }

        String taStudentId = loginUser.getUserId();
        TA taProfile = taDao.findByStudentId(taStudentId);
        if (taProfile == null) {
            req.setAttribute("error", "Please complete your profile before applying");
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
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
            req.setAttribute("error", result.getMessage());
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
        }
    }

}
