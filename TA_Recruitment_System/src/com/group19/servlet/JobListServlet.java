package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.dao.SavedJobDao;
import com.group19.model.Job;
import com.group19.model.LoginUser;
import com.group19.service.JobService;
import com.group19.service.SavedJobService;
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
import java.util.Set;

public class JobListServlet extends HttpServlet {
    private JobDao jobDao;
    private JobService jobService;
    private SavedJobService savedJobService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath, "jobs.json");
        this.jobDao = new JobDao(jobFilePath);
        this.jobService = new JobService(jobDao);

        String savedJobDataPath = getServletContext().getInitParameter("savedJobDataFile");
        String savedJobRelativePath = savedJobDataPath == null || savedJobDataPath.isBlank()
                ? "/data/saved_jobs.json"
                : savedJobDataPath;
        Path savedJobFilePath = resolveDataPath(savedJobRelativePath, "saved_jobs.json");
        this.savedJobService = new SavedJobService(new SavedJobDao(savedJobFilePath), jobDao);
    }

    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");
        preventCaching(resp);

        LocalDate today = LocalDate.now();
        LoginUser loginUser = currentLoginUser(req);
        if (loginUser != null) {
            req.setAttribute("loginUser", loginUser);
        }
        req.setAttribute("currentRequestPath", buildCurrentRequestPath(req));
        setSavedJobFeedback(req);

        String jobId = req.getParameter("jobId");
        if (jobId != null && !jobId.isBlank()) {
            Job job = jobDao.findById(jobId.trim());
            req.setAttribute("job", job);
            if (job != null && !jobService.isOpenForApplication(job, today)) {
                req.setAttribute("applyBlockedReason",
                        "This job is closed or its application deadline has passed.");
            }
            applySavedState(req, loginUser, job);
            req.getRequestDispatcher("/WEB-INF/jsp/job_detail.jsp").forward(req, resp);
            return;
        }

        renderJobList(req, today, loginUser);
        req.getRequestDispatcher("/WEB-INF/jsp/job_list.jsp").forward(req, resp);
    }

    private void renderJobList(HttpServletRequest req, LocalDate today, LoginUser loginUser) {
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = isTruthy(req.getParameter("showHidden"));

        List<Job> openJobs = jobService.findOpenActiveJobs(today);
        List<Job> hiddenPool = jobService.findHiddenFromOpenJobs(today);
        int hiddenFromOpenCount = hiddenPool.size();

        List<Job> jobs;
        if (showingHidden) {
            jobs = jobService.filterJobs(hiddenPool, keyword, schedule, skills);
        } else {
            jobs = jobService.filterJobs(openJobs, keyword, schedule, skills);
        }

        req.setAttribute("jobs", jobs);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", jobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenFromOpenCount);
        req.setAttribute("hiddenPoolCount", hiddenPool.size());
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("viewHiddenJobsUrl", buildViewHiddenJobsUrl(req, keyword, schedule, skills));

        if (isTa(loginUser)) {
            Set<String> savedJobIds = savedJobService.findSavedJobIds(loginUser.getUserId());
            req.setAttribute("savedJobIds", savedJobIds);
        }
    }

    private void applySavedState(HttpServletRequest req, LoginUser loginUser, Job job) {
        if (job != null && isTa(loginUser)) {
            req.setAttribute("jobSaved", savedJobService.isSaved(loginUser.getUserId(), job.getJobId()));
        }
    }

    private static LoginUser currentLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    private static boolean isTa(LoginUser loginUser) {
        return loginUser != null && "TA".equalsIgnoreCase(loginUser.getRole());
    }

    private static void setSavedJobFeedback(HttpServletRequest req) {
        String message = trimToNull(req.getParameter("savedJobMessage"));
        String error = trimToNull(req.getParameter("savedJobError"));
        if (message != null) {
            req.setAttribute("savedJobMessage", message);
        }
        if (error != null) {
            req.setAttribute("savedJobError", error);
        }
    }

    private static String buildCurrentRequestPath(HttpServletRequest req) {
        String query = req.getQueryString();
        String path = req.getRequestURI();
        return query == null || query.isBlank() ? path : path + "?" + query;
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String t = value.trim();
        return "1".equals(t) || "true".equalsIgnoreCase(t) || "yes".equalsIgnoreCase(t);
    }

    private static String buildViewHiddenJobsUrl(HttpServletRequest req, String keyword,
                                                 String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        parts.add("showHidden=1");
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "schedule", schedule);
        appendQuery(parts, "skills", skills);
        return req.getContextPath() + "/jobs?" + String.join("&", parts);
    }

    private static void appendQuery(List<String> parts, String name, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        parts.add(name + "=" + URLEncoder.encode(raw, StandardCharsets.UTF_8));
    }

    private static void preventCaching(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
