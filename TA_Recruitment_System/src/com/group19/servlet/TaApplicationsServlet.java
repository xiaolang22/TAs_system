package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TimelineDao;
import com.group19.dto.TaApplicationOverview;
import com.group19.model.LoginUser;
import com.group19.service.TaApplicationStatusService;
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

public class TaApplicationsServlet extends HttpServlet {
    private TaApplicationStatusService taApplicationStatusService;

    @Override
    public void init() {
        Path applicationPath = resolveDataPath(
                firstNonBlank(
                        getServletContext().getInitParameter("applicationDataFile"),
                        "/data/applications.json"
                ),
                "applications.json"
        );
        Path jobPath = resolveDataPath(
                firstNonBlank(
                        getServletContext().getInitParameter("jobDataFile"),
                        "/data/jobs.json"
                ),
                "jobs.json"
        );
        Path timelinePath = resolveDataPath(
                firstNonBlank(
                        getServletContext().getInitParameter("timelineDataFile"),
                        "/data/timelines.json"
                ),
                "timelines.json"
        );

        ApplicationDao applicationDao = new ApplicationDao(applicationPath);
        JobDao jobDao = new JobDao(jobPath);
        TimelineDao timelineDao = new TimelineDao(timelinePath);
        this.taApplicationStatusService = new TaApplicationStatusService(applicationDao, jobDao, timelineDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = loginUser.getUserId();
        List<TaApplicationOverview> overviews = taApplicationStatusService.loadOverviewsForTa(studentId);
        req.setAttribute("applications", overviews);
        req.setAttribute("loginUser", loginUser);
        req.getRequestDispatcher("/WEB-INF/jsp/ta_applications.jsp").forward(req, resp);
    }

    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }
}
