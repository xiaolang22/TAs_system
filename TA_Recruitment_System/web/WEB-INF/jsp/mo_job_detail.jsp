<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%!
    private String displayStatus(String value) {
        if (value == null || value.isBlank()) {
            return "Open";
        }
        if ("OPEN".equalsIgnoreCase(value.trim())) {
            return "Open";
        }
        if ("CLOSED".equalsIgnoreCase(value.trim())) {
            return "Closed";
        }
        return value;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Details - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>Job Details</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${backUrl}">Back to Job List</a>
        </div>
    </header>

    <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>

    <%
        Job job = (Job) request.getAttribute("job");
        Boolean isOwnedJobAttr = (Boolean) request.getAttribute("isOwnedJob");
        boolean isOwnedJob = Boolean.TRUE.equals(isOwnedJobAttr);
        if (job != null) {
    %>
    <section class="card detail-card">
        <h2 class="section-title"><%= job.getTitle() == null ? "" : job.getTitle() %></h2>
        <div class="review-badges">
            <span class="status-pill <%= "Open".equals(displayStatus(job.getStatus())) ? "tag-good" : "tag-neutral" %>">Status: <%= displayStatus(job.getStatus()) %></span>
            <span class="status-pill tag-warning">Deadline: <%= job.getDeadline() == null ? "" : job.getDeadline() %></span>
            <span class="status-pill tag-info">Job ID: <%= job.getJobId() == null ? "" : job.getJobId() %></span>
        </div>
    </section>

    <section class="card">
        <div class="detail-grid">
            <div class="detail-item">
                <span class="label">Workload</span>
                <div class="value"><%= job.getHours() == null ? "" : job.getHours() %></div>
            </div>
            <div class="detail-item">
                <span class="label">Schedule</span>
                <div class="value"><%= job.getSchedule() == null ? "" : job.getSchedule() %></div>
            </div>
            <div class="detail-item">
                <span class="label">Job Description</span>
                <div class="value"><%= job.getDescription() == null ? "" : job.getDescription() %></div>
            </div>
            <div class="detail-item">
                <span class="label">Requirements</span>
                <div class="value"><%= job.getRequirements() == null ? "" : job.getRequirements() %></div>
            </div>
        </div>
    </section>

    <% if (isOwnedJob) { %>
    <section class="card">
        <div class="ta-job-actions">
            <a class="link-btn" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= job.getJobId() %>">View Applications</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/post-job?jobId=<%= job.getJobId() %>">Edit Job</a>
        </div>
    </section>
    <% } %>
    <% } %>
</main>
</body>
</html>
