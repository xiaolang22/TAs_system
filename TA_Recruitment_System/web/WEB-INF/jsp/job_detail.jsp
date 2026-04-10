<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
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
            <h1>Job Details</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/jobs">Back to Job List</a>
        </div>
    </header>

    <%
        Job job = (Job) request.getAttribute("job");
        if (job != null) {
    %>
    <section class="card">
        <h2><%= job.getTitle() %></h2>
        <p><strong>Category:</strong> <%= job.getCategory() %></p>
        <p><strong>Status:</strong> <%= job.getStatus() %></p>
        <p><strong>Hours:</strong> <%= job.getHours() %></p>
        <p><strong>Schedule:</strong> <%= job.getSchedule() %></p>
        <p><strong>Deadline:</strong> <%= job.getDeadline() %></p>

        <h3>Description</h3>
        <p><%= job.getDescription() %></p>

        <h3>Requirements</h3>
        <p><%= job.getRequirements() %></p>

        <div style="margin-top: 20px;">
            <form method="post" action="${pageContext.request.contextPath}/apply">
                <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
                <button type="submit" class="primary-btn">Apply for this Job</button>
            </form>
        </div>
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
