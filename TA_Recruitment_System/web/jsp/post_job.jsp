<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%
    Job job = (Job) request.getAttribute("job");
    if (job == null) {
        job = new Job();
    }

    String errorMsg = (String) request.getAttribute("errorMsg");
    String success = request.getParameter("success");
    boolean editing = Boolean.TRUE.equals(request.getAttribute("editing")) || job.getJobId() != null;
    boolean edited = "true".equals(request.getParameter("edited"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= editing ? "Edit Job" : "Post Job" %> - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1><%= editing ? "Edit Job" : "Post Job" %></h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">Back to Home</a>
        </div>
    </header>

    <p class="alert success <%= "true".equals(success) ? "" : "hidden" %>"><%= edited ? "Job information has been updated." : "Job posted successfully." %></p>
    <p class="alert error <%= errorMsg == null || errorMsg.trim().isEmpty() ? "hidden" : "" %>"><%= errorMsg == null ? "" : errorMsg %></p>

    <form method="post" action="<%= request.getContextPath() %>/mo/post-job" class="profile-form">
        <input type="hidden" name="jobId" value="<%= job.getJobId() == null ? "" : job.getJobId() %>">

        <label for="title">Job Title</label>
        <input
                type="text"
                id="title"
                name="title"
                placeholder="e.g. Java Programming Teaching Assistant"
                value="<%= job.getTitle() == null ? "" : job.getTitle() %>">

        <label for="hours">Workload</label>
        <input
                type="text"
                id="hours"
                name="hours"
                placeholder="e.g. 6 hours/week"
                value="<%= job.getHours() == null ? "" : job.getHours() %>">

        <label for="description">Job Description</label>
        <textarea
                id="description"
                name="description"
                rows="4"
                placeholder="Describe the key responsibilities for this role."><%= job.getDescription() == null ? "" : job.getDescription() %></textarea>

        <label for="requirements">Skill Requirements</label>
        <textarea
                id="requirements"
                name="requirements"
                rows="4"
                placeholder="List the skills or experience expected from the TA."><%= job.getRequirements() == null ? "" : job.getRequirements() %></textarea>

        <label for="schedule">Schedule</label>
        <input
                type="text"
                id="schedule"
                name="schedule"
                placeholder="e.g. Tuesday 14:00-16:00, Thursday online office hours"
                value="<%= job.getSchedule() == null ? "" : job.getSchedule() %>">

        <label for="deadline">Application Deadline</label>
        <input
                type="date"
                id="deadline"
                name="deadline"
                value="<%= job.getDeadline() == null ? "" : job.getDeadline() %>">

        <button type="submit"><%= editing ? "Save Changes" : "Post Job" %></button>
    </form>
</main>
</body>
</html>
