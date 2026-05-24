package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.NotificationDao;
import com.group19.dao.TADao;
import com.group19.dto.ApplicantReviewPageData;
import com.group19.dto.ApplicantReviewRow;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import com.group19.model.LoginUser;
import com.group19.service.ApplicantReviewService;
import com.group19.service.ApplicationService;
import com.group19.service.MoNewApplicationNotificationService;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Application management servlet providing MOs with applicant review and
 * management functionality.
 *
 * <p>Handles URL: /mo/applications
 *
 * <p>Supported features:
 * <ul>
 *   <li>View all applicants for a given position</li>
 *   <li>Sort applicants by match score or status</li>
 *   <li>Update application status (Submitted → In Review → Shortlisted → Accepted/Rejected)</li>
 *   <li>View applicant details and CV</li>
 * </ul>
 *
 * <p>Access: restricted to the MO role.
 *
 * @author Group 19
 * @see ApplicantReviewService
 * @see ApplicationService
 */
public class ManageApplicationsServlet extends HttpServlet {
    private ApplicationService applicationService;
    private ApplicantReviewService applicantReviewService;
    private MoNewApplicationNotificationService moNewApplicationNotificationService;

    /**
     * Initialises the Servlet, loads application, job, TA, and notification data files,
     * and creates the relevant service instances.
     */
    @Override
    public void init() {
        String appDataPath = getServletContext().getInitParameter("applicationDataFile");
        String appRelativePath = appDataPath == null || appDataPath.isBlank()
                ? "/data/applications.json"
                : appDataPath;
        Path appFilePath = resolveDataPath(appRelativePath, "applications.json");
        ApplicationDao applicationDao = new ApplicationDao(appFilePath);
        this.applicationService = ApplicationServiceFactory.create(getServletContext());

        Path jobFilePath = DataPathResolver.resolve(
                getServletContext(), "jobDataFile", "/data/jobs.json", "jobs.json");
        JobDao jobDao = new JobDao(jobFilePath);

        String taDataPath = getServletContext().getInitParameter("taDataFile");
        String taRelativePath = taDataPath == null || taDataPath.isBlank()
                ? "/data/tas.json"
                : taDataPath;
        Path taFilePath = resolveDataPath(taRelativePath, "tas.json");
        TADao taDao = new TADao(taFilePath);

        this.applicantReviewService = new ApplicantReviewService(applicationDao, taDao, jobDao);

        Path notificationFilePath = DataPathResolver.resolve(
                getServletContext(), "notificationDataFile", "/data/notifications.json", "notifications.json");
        this.moNewApplicationNotificationService = new MoNewApplicationNotificationService(
                new NotificationDao(notificationFilePath),
                jobDao);
    }

    /**
     * Handles GET requests: displays the applicant list for a specified job.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Obtain the job ID, sort mode, and applicant detail parameters</li>
     *   <li>If an applicationId is provided, mark the notification as viewed</li>
     *   <li>Load the applicant data for the job</li>
     *   <li>If in detail mode (detail=1 or studentId present), redirect to the
     *       candidate profile page</li>
     *   <li>Otherwise, forward to applicant_review.jsp to display the applicant list</li>
     * </ol>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String jobId = trimToNull(req.getParameter("jobId"));
        String applicationId = trimToNull(req.getParameter("applicationId"));
        String sortMode = normalizeSortMode(req.getParameter("sort"));
        String studentId = trimToNull(req.getParameter("studentId"));
        boolean detailMode = isTruthy(req.getParameter("detail")) || studentId != null;

        markMoNotificationViewedIfNeeded(req, applicationId);

        req.setAttribute("jobId", jobId);
        req.setAttribute("sortMode", sortMode);
        req.setAttribute("showMatchColumn", "match".equals(sortMode));
        req.setAttribute("updated", req.getParameter("updated"));

        if (jobId == null) {
            req.setAttribute("errorMsg", "Job ID is required.");
            req.setAttribute("jobTitle", "");
            req.setAttribute("applicantRowsHtml", buildEmptyRowsHtml("Job ID is required.", "match".equals(sortMode)));
            req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
            return;
        }

        ServiceResult<ApplicantReviewPageData> result = applicantReviewService.loadApplicantsForJob(jobId, sortMode);
        if (!result.isSuccess()) {
            req.setAttribute("errorMsg", result.getMessage());
            req.setAttribute("jobTitle", "");
            req.setAttribute("applicantRowsHtml",
                    buildEmptyRowsHtml(result.getMessage(), "match".equals(sortMode)));
            req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
            return;
        }

        ApplicantReviewPageData pageData = result.getData();
        List<ApplicantReviewRow> applicants = pageData.getApplicants();
        applyReviewPageAttributes(req, pageData, applicants, jobId, sortMode);

        if (detailMode) {
            ApplicantReviewRow applicant = findApplicant(applicants, studentId);
            if (applicant == null) {
                req.setAttribute("errorMsg", "No applicants were found for this job.");
                req.setAttribute("applicantRowsHtml",
                        buildEmptyRowsHtml("No applicants were found for this job.", "match".equals(sortMode)));
                req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
                return;
            }
            resp.sendRedirect(buildCandidateProfileUrl(req.getContextPath(), jobId, applicant.getTaStudentId(), sortMode));
            return;
        }

        req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
    }

    /**
     * Handles POST requests: updates the application status (e.g., accept, reject).
     *
     * <p>Updates the application status based on the status parameter and redirects
     * back to the applicant list page upon success.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
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

        ServiceResult<ApplicantReviewPageData> pageResult = applicantReviewService.loadApplicantsForJob(jobId, sortMode);
        if (pageResult.isSuccess()) {
            ApplicantReviewPageData pageData = pageResult.getData();
            applyReviewPageAttributes(req, pageData, pageData.getApplicants(), jobId, sortMode);
        } else {
            req.setAttribute("jobTitle", "");
            req.setAttribute("showMatchColumn", "match".equals(sortMode));
            req.setAttribute("applicantRowsHtml",
                    buildEmptyRowsHtml(pageResult.getMessage(), "match".equals(sortMode)));
        }

        req.setAttribute("jobId", jobId);
        req.setAttribute("sortMode", sortMode);
        req.setAttribute("errorMsg", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/applicant_review.jsp").forward(req, resp);
    }

    /**
     * Binds applicant review page data to request attributes.
     *
     * @param req        the HTTP request
     * @param pageData   the page data
     * @param applicants the list of applicant review rows
     * @param jobId      the job ID
     * @param sortMode   the sort mode
     */
    private void applyReviewPageAttributes(HttpServletRequest req, ApplicantReviewPageData pageData,
                                           List<ApplicantReviewRow> applicants,
                                           String jobId, String sortMode) {
        req.setAttribute("job", pageData.getJob());
        req.setAttribute("jobTitle", pageData.getJob() == null ? "" : pageData.getJob().getTitle());
        req.setAttribute("sortLabel", pageData.getSortLabel());
        req.setAttribute("applicantCount", applicants == null ? 0 : applicants.size());
        req.setAttribute("showMatchColumn", "match".equals(sortMode));
        req.setAttribute("applicantRowsHtml", buildApplicantRowsHtml(req, applicants, jobId, sortMode));
    }

    /**
     * Finds a specific applicant by student ID within the applicant list.
     *
     * @param applicants the list of applicant review rows
     * @param studentId  the student ID
     * @return the matching applicant review row, or null if not found
     */
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

    /**
     * Builds the HTML row content for the applicant list, including applicant
     * information, skill tags, match score, status selector, and action buttons.
     *
     * @param req        the HTTP request
     * @param applicants the list of applicant review rows
     * @param jobId      the job ID
     * @param sortMode   the sort mode
     * @return the HTML string for the applicant rows
     */
    private static String buildApplicantRowsHtml(HttpServletRequest req, List<ApplicantReviewRow> applicants,
                                                 String jobId, String sortMode) {
        boolean showMatchColumn = "match".equals(sortMode);
        if (applicants == null || applicants.isEmpty()) {
            return buildEmptyRowsHtml("There are no applications for this job yet.", showMatchColumn);
        }

        String contextPath = req.getContextPath();
        StringBuilder html = new StringBuilder();
        for (ApplicantReviewRow applicant : applicants) {
            html.append("<tr>");
            html.append("<td><div class=\"table-main\"><strong>")
                    .append(escapeHtml(applicant.getTaName()))
                    .append("</strong></div></td>");
            html.append("<td><div class=\"skill-cloud\">")
                    .append(applicant.getCoreSkillsHtml())
                    .append("</div></td>");
            if (showMatchColumn) {
                html.append("<td><span class=\"status-pill tag-neutral\">")
                        .append(applicant.getMatchScore())
                        .append("%</span></td>");
            }
            html.append("<td>").append(escapeHtml(applicant.getCurrentWorkloadLabel())).append("</td>");
            html.append("<td><span class=\"status-pill ")
                    .append(statusClass(applicant.getStatus()))
                    .append("\">")
                    .append(escapeHtml(statusLabel(applicant.getStatus())))
                    .append("</span></td>");
            html.append("<td class=\"review-profile-cell\">")
                    .append(buildProfileLink(contextPath, jobId, applicant.getTaStudentId(), sortMode))
                    .append("</td>");
            html.append("<td>");
            html.append("<form method=\"post\" action=\"")
                    .append(escapeHtml(contextPath))
                    .append("/mo/applications\" class=\"inline-form review-action-form\">");
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
                    .append("\">Application Status</label>");
            html.append("<select id=\"status-")
                    .append(escapeHtml(applicant.getApplicationId()))
                    .append("\" name=\"status\">");
            for (String allowedStatus : allowedStatuses(applicant.getStatus())) {
                html.append(statusOption(allowedStatus, applicant.getStatus()));
            }
            html.append("</select>");
            html.append("<p class=\"table-subtext review-process-note\">")
                    .append(escapeHtml(buildStatusRuleHint()))
                    .append("</p>");
            html.append("<button type=\"submit\">Update Status</button>");
            html.append("</form>");
            html.append("</td>");
            html.append("</tr>");
        }
        return html.toString();
    }

    /**
     * Builds the HTML link for viewing an applicant's profile.
     *
     * @param contextPath the application context path
     * @param jobId       the job ID
     * @param studentId   the student ID
     * @param sortMode    the sort mode
     * @return the HTML string for the profile link
     */
    private static String buildProfileLink(String contextPath, String jobId, String studentId, String sortMode) {
        String href = buildCandidateProfileUrl(contextPath, jobId, studentId, sortMode);
        return "<a class=\"link-btn secondary\" href=\"" + escapeHtml(href) + "\">View Profile / Resume</a>";
    }

    /**
     * Builds the URL for the candidate profile page, including return parameters.
     *
     * @param contextPath the application context path
     * @param jobId       the job ID
     * @param studentId   the student ID
     * @param sortMode    the sort mode
     * @return the full URL for the candidate profile page
     */
    private static String buildCandidateProfileUrl(String contextPath, String jobId, String studentId, String sortMode) {
        String backUrl = contextPath + "/mo/applications?jobId=" + encode(jobId) + "&sort=" + encode(sortMode);
        return contextPath + "/mo/ta-profile?studentId=" + encode(studentId)
                + "&backUrl=" + encode(backUrl)
                + "&backLabel=" + encode("Back to Applicant List");
    }

    /**
     * Returns the corresponding CSS class name for a given application status.
     *
     * @param status the application status string
     * @return the CSS class name
     */
    private static String statusClass(String status) {
        String normalized = trimLower(status);
        return switch (normalized) {
            case "shortlisted" -> "tag-warning";
            case "accepted" -> "tag-good";
            case "rejected" -> "tag-alert";
            case "in_review" -> "tag-info";
            default -> "tag-neutral";
        };
    }

    /**
     * Generates the HTML for a status dropdown option.
     *
     * @param optionValue  the option value
     * @param currentValue the currently selected status
     * @return the HTML string for the option element
     */
    private static String statusOption(String optionValue, String currentValue) {
        boolean selected = optionValue != null && optionValue.equalsIgnoreCase(currentValue);
        return "<option value=\"" + escapeHtml(optionValue) + "\"" + (selected ? " selected" : "") + ">"
                + escapeHtml(statusLabel(optionValue))
                + "</option>";
    }

    /**
     * Returns the list of statuses allowed to be transitioned to from the current status.
     *
     * @param currentStatus the current application status
     * @return the list of allowed statuses
     */
    private static List<String> allowedStatuses(String currentStatus) {
        List<String> allowed = ApplicationService.getAllowedStatuses(currentStatus);
        if (allowed.isEmpty()) {
            allowed = new ArrayList<>();
            allowed.add("SUBMITTED");
        }
        return allowed;
    }

    /**
     * Builds a hint text describing the status transition rules.
     *
     * @return the status transition rule description
     */
    private static String buildStatusRuleHint() {
        return "Flow: Submitted -> In Review -> Shortlisted -> Accepted / Rejected.";
    }

    /**
     * Builds the HTML row for an empty state (displayed when there are no applicants
     * or an error occurs).
     *
     * @param message         the prompt message
     * @param showMatchColumn whether to display the match score column
     * @return the HTML string for the empty state row
     */
    private static String buildEmptyRowsHtml(String message, boolean showMatchColumn) {
        int columnCount = showMatchColumn ? 7 : 6;
        return "<tr><td colspan=\"" + columnCount + "\"><div class=\"empty-state\">"
                + escapeHtml(message) + "</div></td></tr>";
    }

    /**
     * Converts an internal status code to display text.
     *
     * @param status the internal status code
     * @return the display status text
     */
    private static String statusLabel(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "SUBMITTED" -> "Submitted";
            case "IN_REVIEW" -> "In Review";
            case "SHORTLISTED" -> "Shortlisted";
            case "ACCEPTED" -> "Accepted";
            case "REJECTED" -> "Rejected";
            default -> normalized.isEmpty() ? "-" : normalized;
        };
    }

    /**
     * Normalises the sort mode, accepting only "status"; all others default to "match".
     *
     * @param sortMode the raw sort mode parameter
     * @return the normalised sort mode
     */
    private static String normalizeSortMode(String sortMode) {
        if ("status".equalsIgnoreCase(sortMode)) {
            return "status";
        }
        return "match";
    }

    /**
     * Determines whether a string value is truthy (1, true, yes).
     *
     * @param value the input string
     * @return true if the value is truthy
     */
    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim();
        return "1".equals(normalized) || "true".equalsIgnoreCase(normalized) || "yes".equalsIgnoreCase(normalized);
    }

    /**
     * If the request contains an applicationId, marks the MO's corresponding
     * notification as viewed.
     *
     * @param req           the HTTP request
     * @param applicationId the application ID
     */
    private void markMoNotificationViewedIfNeeded(HttpServletRequest req, String applicationId) {
        if (applicationId == null || applicationId.isBlank()) {
            return;
        }
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"MO".equalsIgnoreCase(loginUser.getRole())) {
            return;
        }
        moNewApplicationNotificationService.markApplicationAsViewed(loginUser.getUserId(), applicationId);
    }

    /**
     * Trims the string and returns null if the result is empty.
     *
     * @param value the input string
     * @return the trimmed string, or null if empty
     */
    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * HTML escaping to prevent XSS attacks.
     *
     * @param value the raw string
     * @return the escaped safe string
     */
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

    /**
     * URL-encodes a string.
     *
     * @param value the raw string
     * @return the URL-encoded string
     */
    private static String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Trims the string and converts it to lower case.
     *
     * @param value the input string
     * @return the lower-cased and trimmed string
     */
    private static String trimLower(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's real
     * path and falling back to the data directory under the working directory.
     *
     * @param webRelativePath  the web-relative path
     * @param fallbackFileName the fallback file name
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }
}
