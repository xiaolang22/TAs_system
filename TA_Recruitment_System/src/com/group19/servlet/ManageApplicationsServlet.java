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
        Path appFilePath = resolveDataPath(appRelativePath, "applications.json");
        ApplicationDao applicationDao = new ApplicationDao(appFilePath);

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

        String jobId = trimToNull(req.getParameter("jobId"));
        String applicationId = trimToNull(req.getParameter("applicationId"));
        String status = trimToNull(req.getParameter("status"));
        String decisionNote = req.getParameter("decisionNote");
        String sortMode = normalizeSortMode(req.getParameter("sort"));

        ServiceResult<Application> result = applicationService.updateApplicationStatus(
                applicationId,
                status,
                decisionNote
        );

        if (result.isSuccess()) {
            String encodedJobId = URLEncoder.encode(jobId == null ? "" : jobId, StandardCharsets.UTF_8);
            String encodedSort = URLEncoder.encode(sortMode, StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath()
                    + "/mo/applications?jobId="
                    + encodedJobId
                    + "&sort="
                    + encodedSort
                    + "&updated=true");
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
        req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
    }

    private static ApplicantReviewRow findApplicant(List<ApplicantReviewRow> applicants, String studentId) {
        if (applicants == null || studentId == null || studentId.isBlank()) {
            return null;
        }
        for (ApplicantReviewRow applicant : applicants) {
            if (studentId.equalsIgnoreCase(applicant.getTaStudentId())) {
                return applicant;
            }
        }
        return null;
    }

    private void prepareDetailAttributes(HttpServletRequest req, Job job, ApplicantReviewRow applicant,
                                         String jobId, String sortMode) {
        req.setAttribute("job", job);
        req.setAttribute("applicant", applicant);
        req.setAttribute("sortMode", sortMode);
        req.setAttribute("sortLabel", "status".equals(sortMode) ? "Status" : "Match degree");
        req.setAttribute("backUrl", req.getContextPath()
                + "/mo/applications?jobId="
                + encode(jobId)
                + "&sort="
                + encode(sortMode));
        req.setAttribute("resumeHref", buildResumeHref(req, applicant.getCvFilePath()));
        boolean resumeAvailable = applicant.getCvFilePath() != null && !applicant.getCvFilePath().isBlank();
        req.setAttribute("resumeAvailableClass", resumeAvailable ? "" : "hidden");
        req.setAttribute("resumeMissingClass", resumeAvailable ? "hidden" : "");
    }

    private static String buildApplicantRowsHtml(HttpServletRequest req, List<ApplicantReviewRow> applicants,
                                                 String jobId, String sortMode) {
        if (applicants == null || applicants.isEmpty()) {
            return buildEmptyRowsHtml("No applications found for this job.");
        }

        String contextPath = req.getContextPath();
        StringBuilder html = new StringBuilder();
        for (ApplicantReviewRow applicant : applicants) {
            html.append("<tr>");
            html.append("<td><div class=\"table-main\"><strong>")
                    .append(escapeHtml(applicant.getTaName()))
                    .append("</strong><span class=\"table-subtext\">")
                    .append(escapeHtml(applicant.getTaStudentId()))
                    .append("</span></div></td>");
            html.append("<td><div class=\"skill-cloud\">")
                    .append(applicant.getCoreSkillsHtml())
                    .append("</div></td>");
            html.append("<td><span class=\"status-pill tag-neutral\">")
                    .append(applicant.getMatchScore())
                    .append("%</span></td>");
            html.append("<td>").append(escapeHtml(applicant.getCurrentWorkloadLabel())).append("</td>");
            html.append("<td><span class=\"status-pill ")
                    .append(statusClass(applicant.getStatus()))
                    .append("\">")
                    .append(escapeHtml(applicant.getStatus()))
                    .append("</span></td>");
            html.append("<td>")
                    .append(buildProfileLink(contextPath, jobId, applicant.getTaStudentId(), sortMode))
                    .append("</td>");
            html.append("<td>")
                    .append(buildResumeLink(contextPath, applicant.getCvFilePath()))
                    .append("</td>");
            html.append("<td>");
            html.append("<form method=\"post\" action=\"")
                    .append(escapeHtml(contextPath))
                    .append("/mo/applications\" class=\"inline-form\">");
            html.append("<input type=\"hidden\" name=\"jobId\" value=\"")
                    .append(escapeHtml(jobId))
                    .append("\">");
            html.append("<input type=\"hidden\" name=\"applicationId\" value=\"")
                    .append(escapeHtml(applicant.getApplicationId()))
                    .append("\">");
            html.append("<input type=\"hidden\" name=\"sort\" value=\"")
                    .append(escapeHtml(sortMode))
                    .append("\">");
            html.append("<label class=\"sr-only\" for=\"status-")
                    .append(escapeHtml(applicant.getApplicationId()))
                    .append("\">Status</label>");
            html.append("<select id=\"status-")
                    .append(escapeHtml(applicant.getApplicationId()))
                    .append("\" name=\"status\">");
            html.append(statusOption("SUBMITTED", applicant.getStatus()));
            html.append(statusOption("IN_REVIEW", applicant.getStatus()));
            html.append(statusOption("SHORTLISTED", applicant.getStatus()));
            html.append(statusOption("ACCEPTED", applicant.getStatus()));
            html.append(statusOption("REJECTED", applicant.getStatus()));
            html.append("</select>");
            html.append("<textarea name=\"decisionNote\" rows=\"3\" placeholder=\"Optional note\">")
                    .append(escapeHtml(applicant.getDecisionNote()))
                    .append("</textarea>");
            html.append("<button type=\"submit\">Update</button>");
            html.append("</form>");
            html.append("</td>");
            html.append("</tr>");
        }
        return html.toString();
    }

    private static String buildProfileLink(String contextPath, String jobId, String studentId, String sortMode) {
        String href = contextPath + "/mo/applications?jobId=" + encode(jobId)
                + "&studentId=" + encode(studentId)
                + "&detail=1"
                + "&sort=" + encode(sortMode);
        return "<a class=\"link-btn secondary\" href=\"" + escapeHtml(href) + "\">Open profile</a>";
    }

    private static String buildResumeLink(String contextPath, String cvFilePath) {
        if (cvFilePath == null || cvFilePath.isBlank()) {
            return "<span class=\"muted\">No CV uploaded</span>";
        }
        return "<a class=\"link-btn\" href=\""
                + escapeHtml(contextPath + cvFilePath)
                + "\" target=\"_blank\" rel=\"noopener\">Open resume</a>";
    }

    private static String buildResumeHref(HttpServletRequest req, String cvFilePath) {
        if (cvFilePath == null || cvFilePath.isBlank()) {
            return "";
        }
        return req.getContextPath() + cvFilePath;
    }

    private static String statusClass(String status) {
        String normalized = trimLower(status);
        return switch (normalized) {
            case "shortlisted" -> "tag-warning";
            case "accepted" -> "tag-good";
            case "rejected" -> "tag-alert";
            case "in_review" -> "tag-neutral";
            default -> "tag-neutral";
        };
    }

    private static String statusOption(String optionValue, String currentValue) {
        boolean selected = optionValue != null && optionValue.equalsIgnoreCase(currentValue);
        return "<option value=\"" + escapeHtml(optionValue) + "\"" + (selected ? " selected" : "") + ">"
                + escapeHtml(optionValue)
                + "</option>";
    }

    private static String buildEmptyRowsHtml(String message) {
        return "<tr><td colspan=\"8\"><div class=\"empty-state\">" + escapeHtml(message) + "</div></td></tr>";
    }

    private static String normalizeSortMode(String sortMode) {
        if ("status".equalsIgnoreCase(sortMode)) {
            return "status";
        }
        return "match";
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim();
        return "1".equals(normalized) || "true".equalsIgnoreCase(normalized) || "yes".equalsIgnoreCase(normalized);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        String result = value.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        return result.replace("'", "&#39;");
    }

    private static String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String trimLower(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
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
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }
}
