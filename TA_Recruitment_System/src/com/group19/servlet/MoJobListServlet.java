package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
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

        String jobId = trimToNull(req.getParameter("jobId"));
        if (jobId != null) {
            renderDetail(req, resp, loginUser, jobId);
            return;
        }

        boolean showAll = isTruthy(req.getParameter("showAll"));
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = showAll && isTruthy(req.getParameter("showHidden"));
        LocalDate today = LocalDate.now();

        List<Job> allJobs = jobService.findAllJobs();
        List<Job> ownedJobs = jobService.findJobsOwnedBy(loginUser.getUserId());
        List<Job> jobs = showAll
                ? buildAllJobsView(today, keyword, schedule, skills, showingHidden, req, allJobs)
                : buildOwnedJobsView(keyword, schedule, skills, req, ownedJobs);

        req.setAttribute("loginUser", loginUser);
        req.setAttribute("jobs", jobs);
        req.setAttribute("showAll", showAll);
        req.setAttribute("ownedJobCount", ownedJobs.size());
        req.setAttribute("allJobCount", allJobs.size());
        String currentListUrl = buildListUrl(req, showAll, showingHidden, keyword, schedule, skills);
        req.setAttribute("detailLinkPrefix",
                currentListUrl + (currentListUrl.contains("?") ? "&" : "?") + "jobId=");
        req.getRequestDispatcher("/WEB-INF/jsp/mo_job_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String action = trimToNull(req.getParameter("action"));
        if (!"delete".equalsIgnoreCase(action)) {
            resp.sendRedirect(req.getContextPath() + "/mo/jobs");
            return;
        }

        ServiceResult<Void> result = jobService.deleteJob(req.getParameter("jobId"), loginUser.getUserId());
        String target = buildListUrl(req,
                isTruthy(req.getParameter("showAll")),
                isTruthy(req.getParameter("showHidden")),
                trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q"))),
                trimToNull(req.getParameter("schedule")),
                trimToNull(req.getParameter("skills")));
        String separator = target.contains("?") ? "&" : "?";
        if (result.isSuccess()) {
            resp.sendRedirect(target + separator + "success=" + encode(result.getMessage()));
            return;
        }
        resp.sendRedirect(target + separator + "error=" + encode(result.getMessage()));
    }

    private List<Job> buildAllJobsView(LocalDate today, String keyword, String schedule, String skills,
                                       boolean showingHidden, HttpServletRequest req, List<Job> allJobs) {
        List<Job> openJobs = new ArrayList<>();
        List<Job> hiddenJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (jobService.isOpenForListing(job, today)) {
                openJobs.add(job);
            } else {
                hiddenJobs.add(job);
            }
        }
        List<Job> source = showingHidden ? hiddenJobs : openJobs;
        List<Job> filteredJobs = jobService.filterJobs(source, keyword, schedule, skills);
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", filteredJobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenJobs.size());
        req.setAttribute("hiddenPoolCount", hiddenJobs.size());
        req.setAttribute("viewHiddenJobsUrl", buildListUrl(req, true, true, keyword, schedule, skills));
        req.setAttribute("viewOpenJobsUrl", buildListUrl(req, true, false, keyword, schedule, skills));
        return filteredJobs;
    }

    private List<Job> buildOwnedJobsView(String keyword, String schedule, String skills,
                                         HttpServletRequest req, List<Job> ownedJobs) {
        List<Job> filteredJobs = jobService.filterJobs(ownedJobs, keyword, schedule, skills);
        req.setAttribute("showingHidden", false);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("filteredCount", filteredJobs.size());
        return filteredJobs;
    }

    private void renderDetail(HttpServletRequest req, HttpServletResponse resp, LoginUser loginUser, String jobId)
            throws ServletException, IOException {
        boolean showAll = isTruthy(req.getParameter("showAll"));
        boolean showHidden = showAll && isTruthy(req.getParameter("showHidden"));
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));

        Job job = jobService.findById(jobId);
        if (job == null) {
            req.setAttribute("errorMsg", "未找到对应岗位。");
        } else {
            req.setAttribute("job", job);
            req.setAttribute("isOwnedJob", jobService.isOwnedBy(job, loginUser.getUserId()));
        }
        req.setAttribute("backUrl", buildListUrl(req, showAll, showHidden, keyword, schedule, skills));
        req.getRequestDispatcher("/WEB-INF/jsp/mo_job_detail.jsp").forward(req, resp);
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

    private static String buildListUrl(HttpServletRequest req, boolean showAll, boolean showHidden,
                                       String keyword, String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        if (showAll) {
            parts.add("showAll=1");
        }
        if (showHidden) {
            parts.add("showHidden=1");
        }
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "schedule", schedule);
        appendQuery(parts, "skills", skills);
        if (parts.isEmpty()) {
            return req.getContextPath() + "/mo/jobs";
        }
        return req.getContextPath() + "/mo/jobs?" + String.join("&", parts);
    }

    private static void appendQuery(List<String> parts, String name, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        parts.add(name + "=" + encode(raw));
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim();
        return "1".equals(normalized) || "true".equalsIgnoreCase(normalized) || "yes".equalsIgnoreCase(normalized);
    }
}
