<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%
    Job job = (Job) request.getAttribute("job");
    if (job == null) {
        job = new Job();
    }

    String errorMsg = (String) request.getAttribute("errorMsg");
    String success = request.getParameter("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Post Job - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide ta-subpage-shell role-form-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">MO Workspace</span>
            <h1>Post a Job</h1>
            <p class="hint">Create a TA recruitment post with clear responsibilities, workload, schedule and deadline.</p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn secondary" href="<%= request.getContextPath() %>/mo/jobs">Job List</a>
            <a class="link-btn secondary" href="<%= request.getContextPath() %>/home">Back to Home</a>
        </div>
    </header>

    <% if ("true".equals(success)) { %>
    <p class="alert success">Job posted successfully.</p>
    <% } %>

    <% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <p class="alert error"><%= errorMsg %></p>
    <% } %>

    <section class="card role-form-card">
        <div>
            <h2 class="section-title">Position Details</h2>
            <p class="hint">These fields are used directly by the current recruitment workflow and applicant list.</p>
        </div>

        <form method="post" action="<%= request.getContextPath() %>/mo/post-job" class="role-form-card">
            <div class="role-form-grid">
                <div class="role-form-field full">
                    <label for="title">Title</label>
                    <input
                            type="text"
                            id="title"
                            name="title"
                            placeholder="e.g. Introduction to Java Teaching Assistant"
                            value="<%= job.getTitle() == null ? "" : job.getTitle() %>">
                </div>

                <div class="role-form-field">
                    <label for="hours">Hours</label>
                    <input
                            type="text"
                            id="hours"
                            name="hours"
                            placeholder="e.g. 10 hours/week"
                            value="<%= job.getHours() == null ? "" : job.getHours() %>">
                </div>

                <div class="role-form-field">
                    <label for="deadline">Deadline</label>
                    <input
                            type="date"
                            id="deadline"
                            name="deadline"
                            value="<%= job.getDeadline() == null ? "" : job.getDeadline() %>">
                </div>

                <div class="role-form-field full">
                    <label for="schedule">Schedule</label>
                    <input
                            type="text"
                            id="schedule"
                            name="schedule"
                            placeholder="e.g. Monday, Wednesday, Friday 2-4pm"
                            value="<%= job.getSchedule() == null ? "" : job.getSchedule() %>">
                </div>

                <div class="role-form-field full">
                    <label for="description">Description</label>
                    <textarea
                            id="description"
                            name="description"
                            rows="5"
                            placeholder="Describe the job responsibilities."><%= job.getDescription() == null ? "" : job.getDescription() %></textarea>
                </div>

                <div class="role-form-field full">
                    <label for="requirements">Requirements</label>
                    <textarea
                            id="requirements"
                            name="requirements"
                            rows="5"
                            placeholder="List the skills or experience required."><%= job.getRequirements() == null ? "" : job.getRequirements() %></textarea>
                </div>
            </div>

            <div class="role-submit-row">
                <a class="link-btn secondary" href="<%= request.getContextPath() %>/mo/jobs">Cancel</a>
                <button type="submit">Post Job</button>
            </div>
        </form>
    </section>
</main>
</body>
</html>
