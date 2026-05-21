<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.dto.TaApplicationOverview" %>
<%@ page import="com.group19.dto.TaTimelineStep" %>
<%
    @SuppressWarnings("unchecked")
    List<TaApplicationOverview> applications = (List<TaApplicationOverview>) request.getAttribute("applications");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My applications - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container wide">
    <header class="page-header">
        <div>
            <h1>Application status</h1>
            <p class="hint">Track the current stage and history for each position you applied to.</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/jobs">Browse jobs</a>
        </div>
    </header>

    <% if (applications == null || applications.isEmpty()) { %>
    <section class="card">
        <p class="hint">You have not submitted any applications yet.</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/jobs">Find open positions</a>
    </section>
    <% } else { %>
    <div class="application-status-list">
        <% for (TaApplicationOverview app : applications) { %>
        <section class="card application-status-card">
            <div class="application-status-head">
                <div>
                    <h2 class="application-job-title"><%= app.getJobTitle() == null ? "" : app.getJobTitle() %></h2>
                    <p class="hint application-job-meta">Application ID: <code><%= app.getApplicationId() == null ? "" : app.getApplicationId() %></code></p>
                </div>
                <div class="application-status-badges">
                    <span class="<%= app.getStatusPillClass() == null ? "status-pill tag-neutral" : app.getStatusPillClass() %>">
                        <%= app.getStatusLabel() == null ? "" : app.getStatusLabel() %>
                    </span>
                </div>
            </div>
            <div class="application-last-updated">
                <span class="label">Latest update</span>
                <span class="value"><%= app.getLastUpdatedDisplay() == null || app.getLastUpdatedDisplay().isEmpty() ? "—" : app.getLastUpdatedDisplay() %></span>
            </div>
            <h3 class="section-title timeline-title">Timeline</h3>
            <ol class="timeline">
                <% 
                    List<TaTimelineStep> steps = app.getTimelineSteps();
                    if (steps != null) {
                        for (TaTimelineStep step : steps) {
                %>
                <li class="timeline-item">
                    <div class="timeline-marker" aria-hidden="true"></div>
                    <div class="timeline-body">
                        <div class="timeline-row">
                            <span class="<%= step.getPillClass() == null ? "status-pill tag-neutral" : step.getPillClass() %>">
                                <%= step.getTitle() == null ? "" : step.getTitle() %>
                            </span>
                            <time class="timeline-time"><%= step.getOccurredAtDisplay() == null ? "" : step.getOccurredAtDisplay() %></time>
                        </div>
                        <% if (step.getDetail() != null && !step.getDetail().isEmpty()) { %>
                        <p class="timeline-detail"><%= step.getDetail() %></p>
                        <% } %>
                    </div>
                </li>
                <% 
                        }
                    }
                %>
            </ol>
        </section>
        <% } %>
    </div>
    <% } %>
</main>
</body>
</html>
