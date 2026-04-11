<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.model.Job" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job List - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <h1>Available Jobs</h1>
            <p class="hint">Browse and apply for TA/Invigilator positions</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
        </div>
    </header>

    <section class="job-list">
        <%
            List<Job> jobs = (List<Job>) request.getAttribute("jobs");
            if (jobs != null && !jobs.isEmpty()) {
                for (Job job : jobs) {
        %>
        <div class="card job-card">
            <h3><%= job.getTitle() %></h3>
            <p><strong>Category:</strong> <%= job.getCategory() %></p>
            <p><strong>Hours:</strong> <%= job.getHours() %></p>
            <p><strong>Deadline:</strong> <%= job.getDeadline() %></p>
            <p><%= job.getDescription() %></p>
            <div style="margin-top: 10px;">
                <a href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>" class="link-btn">
                    View Details & Apply
                </a>
            </div>
        </div>
        <%
                }
            } else {
        %>
        <p class="hint">No jobs available at the moment.</p>
        <%
            }
        %>
    </section>
</main>
</body>
</html>
