package com.group19.servlet;

import com.group19.dao.JobDao;
import com.group19.model.Job;
import com.group19.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobListServlet extends HttpServlet {
    private JobDao jobDao;
    private JobService jobService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("jobDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/jobs.json"
                : configuredPath;
        Path jobFilePath = resolveDataPath(relativePath);
        this.jobDao = new JobDao(jobFilePath);
        this.jobService = new JobService(jobDao);
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "jobs.json");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");
        preventCaching(resp);

        LocalDate today = LocalDate.now();

        String jobId = req.getParameter("jobId");
        if (jobId != null && !jobId.isBlank()) {
            Job job = jobDao.findById(jobId.trim());
            req.setAttribute("job", job);
            if (job != null && !jobService.isOpenForApplication(job, today)) {
                req.setAttribute("applyBlockedReason",
                        "This job is closed or the application deadline has passed.");
            }
            req.getRequestDispatcher("/WEB-INF/jsp/job_detail.jsp").forward(req, resp);
            return;
        }

        renderJobList(req, today);
        req.getRequestDispatcher("/WEB-INF/jsp/job_list.jsp").forward(req, resp);
    }

    private void renderJobList(HttpServletRequest req, LocalDate today) {
        String keyword = trimToNull(firstNonBlank(req.getParameter("keyword"), req.getParameter("q")));
        String category = trimToNull(req.getParameter("category"));
        String schedule = trimToNull(req.getParameter("schedule"));
        String skills = trimToNull(req.getParameter("skills"));
        boolean showingHidden = isTruthy(req.getParameter("showHidden"));

        List<Job> openJobs = jobService.findOpenActiveJobs(today);
        List<Job> hiddenPool = jobService.findHiddenFromOpenJobs(today);
        int hiddenFromOpenCount = hiddenPool.size();

        List<Job> jobs;
        if (showingHidden) {
            jobs = jobService.filterJobs(hiddenPool, keyword, category, schedule, skills);
        } else {
            jobs = jobService.filterJobs(openJobs, keyword, category, schedule, skills);
        }

        req.setAttribute("jobs", jobs);
        req.setAttribute("filterKeyword", nullToEmpty(keyword));
        req.setAttribute("filterCategory", nullToEmpty(category));
        req.setAttribute("filterSchedule", nullToEmpty(schedule));
        req.setAttribute("filterSkills", nullToEmpty(skills));
        req.setAttribute("openJobCount", openJobs.size());
        req.setAttribute("filteredCount", jobs.size());
        req.setAttribute("hiddenFromOpenCount", hiddenFromOpenCount);
        req.setAttribute("hiddenPoolCount", hiddenPool.size());
        req.setAttribute("showingHidden", showingHidden);
        req.setAttribute("viewHiddenJobsUrl", buildViewHiddenJobsUrl(req, keyword, category, schedule, skills));
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String t = value.trim();
        return "1".equals(t) || "true".equalsIgnoreCase(t) || "yes".equalsIgnoreCase(t);
    }

    private static String buildViewHiddenJobsUrl(HttpServletRequest req, String keyword, String category,
                                                 String schedule, String skills) {
        List<String> parts = new ArrayList<>();
        parts.add("showHidden=1");
        appendQuery(parts, "keyword", keyword);
        appendQuery(parts, "category", category);
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
