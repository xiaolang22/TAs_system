package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.ApplicationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplyServlet extends HttpServlet {
    private ApplicationService applicationService;
    private TADao taDao;

    @Override
    public void init() {
        String taDataPath = getServletContext().getInitParameter("taDataFile");
        String taRelativePath = taDataPath == null || taDataPath.isBlank()
                ? "/data/tas.json"
                : taDataPath;
        Path taFilePath = resolveDataPath(taRelativePath);
        this.taDao = new TADao(taFilePath);

        String appDataPath = getServletContext().getInitParameter("applicationDataFile");
        String appRelativePath = appDataPath == null || appDataPath.isBlank()
                ? "/data/applications.json"
                : appDataPath;
        Path appFilePath = resolveDataPath(appRelativePath);
        this.applicationService = new ApplicationService(new ApplicationDao(appFilePath));
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
            req.getRequestDispatcher("/home").forward(req, resp);
            return;
        }

        String taStudentId = loginUser.getUserId();
        TA taProfile = taDao.findByStudentId(taStudentId);
        if (taProfile == null) {
            req.setAttribute("error", "Please complete your profile before applying");
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
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
        }
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "tas.json");
    }
}
