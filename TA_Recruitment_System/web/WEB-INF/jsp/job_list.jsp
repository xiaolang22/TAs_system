<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.model.Job" %>
<%
    String jobListAction = request.getContextPath() + "/jobs";
    boolean showingHidden = Boolean.TRUE.equals(request.getAttribute("showingHidden"));
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
    <title><%= showingHidden ? "Hidden positions" : "Open Jobs" %> - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container wide">
    <header class="page-header">
        <div>
            <h1><%= showingHidden ? "Hidden positions" : "Open positions" %></h1>
            <p class="hint">
                <%= showingHidden
                        ? "These jobs are closed or past the application deadline and are not shown in the open list."
                        : "Search and filter TA or invigilation roles. Closed or past-deadline jobs are hidden here." %>
            </p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
        </div>
    </header>

    <section class="card job-filters-card">
        <h2 class="section-title">Search &amp; filters</h2>
        <form class="job-filters-form" method="get" action="<%= jobListAction %>">
            <% if (showingHidden) { %>
            <input type="hidden" name="showHidden" value="1">
            <% } %>
            <div class="filter-grid">
                <div class="form-field">
                    <label for="keyword">Keyword</label>
                    <input type="text" id="keyword" name="keyword" autocomplete="off"
                           placeholder="Title, description, schedule…"
                           value="${filterKeyword}">
                </div>
                <div class="form-field">
                    <label for="category">Module / activity</label>
                    <select id="category" name="category">
                        <option value="" ${empty filterCategory ? 'selected' : ''}>All types</option>
                        <option value="TA" ${filterCategory eq 'TA' ? 'selected' : ''}>TA</option>
                        <option value="Invigilator" ${filterCategory eq 'Invigilator' ? 'selected' : ''}>Invigilator</option>
                    </select>
                </div>
                <div class="form-field">
                    <label for="schedule">Time (schedule)</label>
                    <input type="text" id="schedule" name="schedule" placeholder="e.g. Monday, April"
                           value="${filterSchedule}">
                </div>
                <div class="form-field">
                    <label for="skills">Skills</label>
                    <input type="text" id="skills" name="skills" placeholder="Keyword in requirements"
                           value="${filterSkills}">
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit">Apply filters</button>
                <a class="link-btn secondary" href="<%= showingHidden ? jobListAction + "?showHidden=1" : jobListAction %>">Clear</a>
            </div>
        </form>
    </section>

    <div class="filter-summary-wrap">
        <p class="hint filter-summary">
            <% if (!showingHidden) { %>
            Showing <strong>${filteredCount}</strong> of <strong>${openJobCount}</strong> open position(s).
            <% if (hiddenFromOpen > 0) { %>
            Additionally, <strong><%= hiddenFromOpen %></strong> position(s) are hidden from this list (closed or past deadline).
            <% } %>
            <% } else { %>
            Showing <strong>${filteredCount}</strong> of <strong><%= hiddenPool %></strong> hidden position(s) matching your filters.
            <% } %>
        </p>
        <% if (!showingHidden && hiddenFromOpen > 0) { %>
        <a class="link-btn secondary filter-summary-btn" href="<%= viewHiddenJobsUrl %>">View hidden positions</a>
        <% } %>
        <% if (showingHidden) { %>
        <a class="link-btn secondary filter-summary-btn" href="${pageContext.request.contextPath}/jobs">Back to open positions</a>
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
                <dt>Module / activity</dt>
                <dd><%= job.getCategory() == null ? "" : job.getCategory() %></dd>
                <dt>Required skills</dt>
                <dd><%= job.getRequirements() == null ? "" : job.getRequirements() %></dd>
                <dt>Schedule</dt>
                <dd><%= job.getSchedule() == null ? "" : job.getSchedule() %></dd>
                <dt>Deadline</dt>
                <dd><%= job.getDeadline() == null ? "" : job.getDeadline() %></dd>
            </dl>
            <div class="job-card-actions">
                <a href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>" class="link-btn">
                    View details &amp; apply
                </a>
            </div>
        </article>
        <%
                }
            } else {
        %>
        <p class="hint"><%= showingHidden ? "No hidden positions match your filters right now." : "No open jobs match your filters right now." %></p>
        <%
            }
        %>
    </section>
</main>
</body>
</html>
