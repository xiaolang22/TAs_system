<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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
<%
    String jobListAction = request.getContextPath() + "/jobs";
    boolean showingHidden = Boolean.TRUE.equals(request.getAttribute("showingHidden"));
    Set<String> savedJobIds = (Set<String>) request.getAttribute("savedJobIds");
    boolean canSaveJobs = savedJobIds != null;
    String currentRequestPath = (String) request.getAttribute("currentRequestPath");
    if (currentRequestPath == null || currentRequestPath.isBlank()) {
        currentRequestPath = request.getContextPath() + "/jobs";
    }
    Object hfObj = request.getAttribute("hiddenFromOpenCount");
    int hiddenFromOpen = hfObj instanceof Number ? ((Number) hfObj).intValue() : 0;
    Object hpObj = request.getAttribute("hiddenPoolCount");
    int hiddenPool = hpObj instanceof Number ? ((Number) hpObj).intValue() : 0;
    String viewHiddenJobsUrl = (String) request.getAttribute("viewHiddenJobsUrl");
    if (viewHiddenJobsUrl == null || viewHiddenJobsUrl.isBlank()) {
        viewHiddenJobsUrl = request.getContextPath() + "/jobs?showHidden=1";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= showingHidden ? "Archived Jobs" : "Job List" %> - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1><%= showingHidden ? "Archived Jobs" : "Open Jobs" %></h1>
            <p class="hint">
                <%= showingHidden
                        ? "This section shows jobs that are closed or past their deadlines."
                        : "Search and filter TA or invigilation roles here. Closed positions are grouped into archived jobs." %>
            </p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
        </div>
    </header>

    <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">
        ${savedJobMessage}
    </p>
    <p class="alert error ${empty savedJobError ? 'hidden' : ''}">
        ${savedJobError}
    </p>

    <section class="card job-filters-card">
        <h2 class="section-title">Search and Filters</h2>
        <form class="job-filters-form" method="get" action="<%= jobListAction %>">
            <% if (showingHidden) { %>
            <input type="hidden" name="showHidden" value="1">
            <% } %>
            <div class="filter-grid">
                <div class="form-field">
                    <label for="keyword">Keyword</label>
                    <input type="text" id="keyword" name="keyword" autocomplete="off"
                           placeholder="Job title, description, or schedule..."
                           value="${filterKeyword}">
                </div>
                <div class="form-field">
                    <label for="schedule">Schedule</label>
                    <input type="text" id="schedule" name="schedule" placeholder="For example: Monday afternoon"
                           value="${filterSchedule}">
                </div>
                <div class="form-field">
                    <label for="skills">Requirements</label>
                    <input type="text" id="skills" name="skills" placeholder="Requirement keywords"
                           value="${filterSkills}">
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit">Apply Filters</button>
                <a class="link-btn secondary" href="<%= showingHidden ? jobListAction + "?showHidden=1" : jobListAction %>">Clear Filters</a>
            </div>
        </form>
    </section>

    <div class="filter-summary-wrap">
        <p class="hint filter-summary">
            <% if (!showingHidden) { %>
            Showing <strong>${filteredCount}</strong> / <strong>${openJobCount}</strong> open jobs.
            <% if (hiddenFromOpen > 0) { %>
            There are <strong><%= hiddenFromOpen %></strong> closed or expired jobs outside this list.
            <% } %>
            <% } else { %>
            Showing <strong>${filteredCount}</strong> / <strong><%= hiddenPool %></strong> archived jobs.
            <% } %>
        </p>
        <% if (!showingHidden && hiddenFromOpen > 0) { %>
        <a class="link-btn secondary filter-summary-btn" href="<%= viewHiddenJobsUrl %>">View Archived Jobs</a>
        <% } %>
        <% if (showingHidden) { %>
        <a class="link-btn secondary filter-summary-btn" href="${pageContext.request.contextPath}/jobs">Back to Open Jobs</a>
        <% } %>
    </div>

    <section class="job-list">
        <%
            List<Job> jobs = (List<Job>) request.getAttribute("jobs");
            if (jobs != null && !jobs.isEmpty()) {
                for (Job job : jobs) {
        %>
        <article class="card job-card">
            <h3><%= job.getTitle() %></h3>
            <dl class="job-meta">
                <dt>Requirements</dt>
                <dd><%= job.getRequirements() == null ? "" : job.getRequirements() %></dd>
                <dt>Schedule</dt>
                <dd><%= job.getSchedule() == null ? "" : job.getSchedule() %></dd>
                <dt>Deadline</dt>
                <dd><%= job.getDeadline() == null ? "" : job.getDeadline() %></dd>
            </dl>
            <div class="job-card-actions">
                <a href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>" class="link-btn">
                    View Details / Apply
                </a>
                <% if (canSaveJobs) {
                    boolean saved = savedJobIds.contains(job.getJobId());
                %>
                <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form">
                    <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                    <input type="hidden" name="action" value="<%= saved ? "remove" : "save" %>">
                    <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
                    <button type="submit" class="<%= saved ? "secondary-btn save-toggle saved" : "save-toggle" %>">
                        <%= saved ? "Remove Bookmark" : "Save Job" %>
                    </button>
                </form>
                <% } %>
            </div>
        </article>
        <%
                }
            } else {
        %>
        <p class="hint"><%= showingHidden ? "No archived jobs match the current filters." : "No open jobs match the current filters." %></p>
        <%
            }
        %>
    </section>
</main>
</body>
</html>
