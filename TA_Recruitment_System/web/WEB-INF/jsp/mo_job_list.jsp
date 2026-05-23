<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
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

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
%>
<%
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
    boolean showAll = Boolean.TRUE.equals(request.getAttribute("showAll"));
    boolean showingHidden = Boolean.TRUE.equals(request.getAttribute("showingHidden"));
    int ownedJobCount = request.getAttribute("ownedJobCount") == null ? 0 : (Integer) request.getAttribute("ownedJobCount");
    int allJobCount = request.getAttribute("allJobCount") == null ? 0 : (Integer) request.getAttribute("allJobCount");
    int filteredCount = request.getAttribute("filteredCount") == null ? jobs.size() : (Integer) request.getAttribute("filteredCount");
    int openJobCount = request.getAttribute("openJobCount") == null ? 0 : (Integer) request.getAttribute("openJobCount");
    int hiddenFromOpenCount = request.getAttribute("hiddenFromOpenCount") == null ? 0 : (Integer) request.getAttribute("hiddenFromOpenCount");
    int hiddenPoolCount = request.getAttribute("hiddenPoolCount") == null ? 0 : (Integer) request.getAttribute("hiddenPoolCount");
    String detailLinkPrefix = (String) request.getAttribute("detailLinkPrefix");
    if (detailLinkPrefix == null || detailLinkPrefix.isEmpty()) {
        detailLinkPrefix = request.getContextPath() + "/mo/jobs?jobId=";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job List - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>Job List</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">Back to Home</a>
        </div>
    </header>

    <p class="alert success ${empty param.success ? 'hidden' : ''}">${param.success}</p>
    <p class="alert error ${empty param.error ? 'hidden' : ''}">${param.error}</p>

    <section class="card">
        <div class="ta-job-toolbar-actions">
            <a class="ta-tab-btn <%= showAll ? "" : "is-active" %>" href="${pageContext.request.contextPath}/mo/jobs">My Jobs</a>
            <a class="ta-tab-btn <%= showAll ? "is-active" : "" %>" href="${pageContext.request.contextPath}/mo/jobs?showAll=1">All Jobs</a>
        </div>
    </section>

    <section class="ta-filter-card ta-filter-card-compact">
        <form method="get" action="${pageContext.request.contextPath}/mo/jobs" class="ta-filter-form ta-filter-inline-form">
            <% if (showAll) { %>
            <input type="hidden" name="showAll" value="1">
            <% } %>
            <% if (showAll && showingHidden) { %>
            <input type="hidden" name="showHidden" value="1">
            <% } %>
            <div class="ta-filter-topbar">
                <div class="ta-filter-copy">
                    <h1><%= showAll ? "All Jobs" : "My Jobs" %></h1>
                </div>
            </div>
            <div class="ta-filter-grid">
                <div class="ta-filter-field">
                    <label for="keyword">Keyword</label>
                    <input id="keyword" name="keyword" type="text" value="${filterKeyword}" placeholder="Job title, description, or schedule">
                </div>
                <div class="ta-filter-field">
                    <label for="schedule">Schedule</label>
                    <input id="schedule" name="schedule" type="text" value="${filterSchedule}" placeholder="For example: Wednesday afternoon">
                </div>
                <div class="ta-filter-field">
                    <label for="skills">Requirements</label>
                    <input id="skills" name="skills" type="text" value="${filterSkills}" placeholder="For example: Java / Python">
                </div>
            </div>
            <div class="ta-filter-actions">
                <button type="submit">Filter Jobs</button>
                <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs<%= showAll ? (showingHidden ? "?showAll=1&amp;showHidden=1" : "?showAll=1") : "" %>">Reset</a>
            </div>
        </form>
    </section>

    <div class="ta-job-summary role-home-summary">
        <% if (showAll) { %>
            <% if (showingHidden) { %>
            Currently showing <strong><%= filteredCount %></strong> / <strong><%= hiddenPoolCount %></strong> closed jobs.
            <a class="ta-summary-link" href="${viewOpenJobsUrl}">Back to Open Jobs</a>
            <% } else { %>
            Currently showing <strong><%= filteredCount %></strong> / <strong><%= openJobCount %></strong> open jobs.
            <% if (hiddenFromOpenCount > 0) { %>
            There are also <strong><%= hiddenFromOpenCount %></strong> closed jobs.
            <a class="ta-summary-link" href="${viewHiddenJobsUrl}">View Closed Jobs</a>
            <% } %>
            <% } %>
        <% } else { %>
        Currently showing <strong><%= filteredCount %></strong> / <strong><%= ownedJobCount %></strong> of my posted jobs.
        <span>Total jobs in the system: <strong><%= allJobCount %></strong>.</span>
        <% } %>
    </div>

    <section class="card">
        <div class="ta-job-list">
            <% if (jobs.isEmpty()) { %>
            <div class="empty-state">
                <%= showAll ? "No jobs are available under the current filters." : "No jobs posted by you match the current filters." %>
            </div>
            <% } else { %>
            <% for (Job job : jobs) { %>
            <article class="ta-job-card">
                <div class="ta-job-card-head ta-job-card-head-inline">
                    <h2><%= attr(job.getTitle()) %></h2>
                    <p class="ta-job-badges">
                        <% if (showAll) { %>
                        <span class="status-pill tag-warning">Deadline: <%= attr(job.getDeadline()) %></span>
                        <span class="status-pill tag-info">Workload: <%= attr(job.getHours()) %></span>
                        <% if (showingHidden || "CLOSED".equalsIgnoreCase(job.getStatus())) { %>
                        <span class="status-pill tag-neutral">Closed</span>
                        <% } %>
                        <% } else { %>
                        <span class="status-pill tag-neutral">Job ID: <%= attr(job.getJobId()) %></span>
                        <span class="status-pill <%= "OPEN".equalsIgnoreCase(job.getStatus()) || job.getStatus() == null ? "tag-good" : "tag-neutral" %>">
                            <%= "OPEN".equalsIgnoreCase(job.getStatus()) || job.getStatus() == null ? "Open" : "Closed" %>
                        </span>
                        <% } %>
                    </p>
                </div>

                <% if (showAll) { %>
                <dl class="ta-job-meta-grid">
                    <div>
                        <dt>Job Description</dt>
                        <dd><%= attr(job.getDescription()) %></dd>
                    </div>
                    <div>
                        <dt>Requirements</dt>
                        <dd><%= attr(job.getRequirements()) %></dd>
                    </div>
                </dl>
                <div class="ta-job-actions">
                    <a class="link-btn" href="<%= attr(detailLinkPrefix) %><%= enc(job.getJobId()) %>">View Details</a>
                </div>
                <% } else { %>
                <dl class="ta-job-meta-grid ta-job-meta-grid-owned">
                    <div>
                        <dt>Job Description</dt>
                        <dd><%= attr(job.getDescription()) %></dd>
                    </div>
                    <div>
                        <dt>Schedule</dt>
                        <dd><%= attr(job.getSchedule()) %></dd>
                    </div>
                </dl>
                <div class="ta-job-actions">
                    <a class="link-btn" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= enc(job.getJobId()) %>">View Applications</a>
                    <a class="link-btn secondary" href="<%= attr(detailLinkPrefix) %><%= enc(job.getJobId()) %>">View Details</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/post-job?jobId=<%= enc(job.getJobId()) %>">Edit Job</a>
                    <form method="post" action="${pageContext.request.contextPath}/mo/jobs" class="save-job-form" onsubmit="return confirm('Delete this job?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                        <input type="hidden" name="keyword" value="${filterKeyword}">
                        <input type="hidden" name="schedule" value="${filterSchedule}">
                        <input type="hidden" name="skills" value="${filterSkills}">
                        <button type="submit" class="secondary-btn">Delete Job</button>
                    </form>
                </div>
                <% } %>
            </article>
            <% } %>
            <% } %>
        </div>
    </section>
</main>
</body>
</html>
