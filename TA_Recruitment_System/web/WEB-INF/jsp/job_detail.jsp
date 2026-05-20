<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%!
    private String attr(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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
<body>
<main class="container">
    <header class="page-header">
        <div>
            <h1>Job details</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/jobs">Back to job list</a>
        </div>
    </header>

    <%
        Job job = (Job) request.getAttribute("job");
        String applyBlockedReason = (String) request.getAttribute("applyBlockedReason");
        Object savedObj = request.getAttribute("jobSaved");
        boolean canSaveJob = savedObj instanceof Boolean;
        boolean jobSaved = Boolean.TRUE.equals(savedObj);
        String currentRequestPath = (String) request.getAttribute("currentRequestPath");
        if (currentRequestPath == null || currentRequestPath.isBlank()) {
            currentRequestPath = request.getContextPath() + "/jobs";
        }
        if (job != null) {
    %>
    <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">
        ${savedJobMessage}
    </p>
    <p class="alert error ${empty savedJobError ? 'hidden' : ''}">
        ${savedJobError}
    </p>

    <section class="card">
        <h2><%= job.getTitle() %></h2>
        <p><strong>Module / activity:</strong> <%= job.getCategory() == null ? "" : job.getCategory() %></p>
        <p><strong>Status:</strong> <%= job.getStatus() == null ? "" : job.getStatus() %></p>
        <p><strong>Hours:</strong> <%= job.getHours() == null ? "" : job.getHours() %></p>
        <p><strong>Schedule:</strong> <%= job.getSchedule() == null ? "" : job.getSchedule() %></p>
        <p><strong>Deadline:</strong> <%= job.getDeadline() == null ? "" : job.getDeadline() %></p>

        <h3>Description</h3>
        <p><%= job.getDescription() == null ? "" : job.getDescription() %></p>

        <h3>Required skills</h3>
        <p><%= job.getRequirements() == null ? "" : job.getRequirements() %></p>

        <% if (canSaveJob) { %>
        <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form detail-save-form">
            <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
            <input type="hidden" name="action" value="<%= jobSaved ? "remove" : "save" %>">
            <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
            <button type="submit" class="<%= jobSaved ? "secondary-btn save-toggle saved" : "save-toggle" %>">
                <%= jobSaved ? "Remove saved" : "Save for later" %>
            </button>
        </form>
        <% } %>

        <% if (applyBlockedReason != null && !applyBlockedReason.isBlank()) { %>
        <p class="alert error"><%= applyBlockedReason %></p>
        <% } else { %>
        <div class="apply-form">
            <form method="post" action="${pageContext.request.contextPath}/apply">
                <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
                <button type="submit">Apply for this job</button>
            </form>
        </div>
        <% } %>
    </section>
    <%
        } else {
    %>
    <p class="alert error">Job not found.</p>
    <%
        }
    %>
</main>
</body>
</html>
