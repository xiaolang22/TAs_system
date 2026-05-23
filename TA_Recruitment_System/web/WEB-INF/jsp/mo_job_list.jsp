<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.model.Job" %>
<%!
    private String statusPillClass(String status) {
        if (status == null || status.isBlank()) {
            return "status-pill tag-good";
        }
        String normalized = status.trim().toLowerCase();
        if ("open".equals(normalized)) {
            return "status-pill tag-good";
        }
        if ("closed".equals(normalized)) {
            return "status-pill tag-neutral";
        }
        return "status-pill tag-info";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
%>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }

    int openCount = 0;
    int closedCount = 0;
    for (Job job : jobs) {
        String status = job.getStatus();
        if (status == null || status.isBlank() || "OPEN".equalsIgnoreCase(status.trim())) {
            openCount++;
        } else if ("CLOSED".equalsIgnoreCase(status.trim())) {
            closedCount++;
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO Job List - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide ta-subpage-shell role-management-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">Job Management</span>
            <h1>MO Job List</h1>
            <p class="hint">View posted jobs and open each applicant review workflow.</p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn" href="<%= request.getContextPath() %>/mo/post-job">Post New Job</a>
            <a class="link-btn secondary" href="<%= request.getContextPath() %>/home">Back to Home</a>
        </div>
    </header>

    <section class="review-summary-grid role-stat-grid role-home-summary">
        <div class="summary-card">
            <span class="label">Total Jobs</span>
            <span class="value"><%= jobs.size() %></span>
        </div>
        <div class="summary-card">
            <span class="label">Open Jobs</span>
            <span class="value"><%= openCount %></span>
        </div>
        <div class="summary-card">
            <span class="label">Closed Jobs</span>
            <span class="value"><%= closedCount %></span>
        </div>
        <div class="summary-card">
            <span class="label">Review Entry</span>
            <span class="value">Applicants</span>
        </div>
    </section>

    <% if (jobs.isEmpty()) { %>
    <section class="card">
        <div class="empty-state">No jobs found.</div>
    </section>
    <% } else { %>
    <section class="mo-job-list">
        <% for (Job job : jobs) { %>
        <article class="mo-job-card">
            <div class="mo-job-card-head">
                <div>
                    <h2><%= safe(job.getTitle()) %></h2>
                    <span class="table-subtext">Job ID: <%= safe(job.getJobId()) %></span>
                </div>
                <span class="<%= statusPillClass(job.getStatus()) %>"><%= safe(job.getStatus() == null || job.getStatus().isBlank() ? "OPEN" : job.getStatus()) %></span>
            </div>

            <p class="mo-job-desc"><%= safe(job.getDescription()) %></p>

            <div class="mo-job-meta">
                <div>
                    <span>Deadline</span>
                    <strong><%= safe(job.getDeadline()) %></strong>
                </div>
                <div>
                    <span>Hours</span>
                    <strong><%= safe(job.getHours()) %></strong>
                </div>
                <div>
                    <span>Schedule</span>
                    <strong><%= safe(job.getSchedule()) %></strong>
                </div>
            </div>

            <div class="role-page-actions">
                <a class="link-btn" href="<%= request.getContextPath() %>/mo/applications?jobId=<%= safe(job.getJobId()) %>">
                    View Applicants
                </a>
            </div>
        </article>
        <% } %>
    </section>
    <% } %>
</main>
</body>
</html>
