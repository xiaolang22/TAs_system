<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.dto.TARecommendation" %>
<%@ page import="com.group19.model.Job" %>
<%@ page import="com.group19.model.LoginUser" %>
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

    private String display(String value) {
        return value == null || value.isBlank() ? "None" : attr(value);
    }
%>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    @SuppressWarnings("unchecked")
    List<TARecommendation> recommendations = (List<TARecommendation>) request.getAttribute("recommendations");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
    if (recommendations == null) {
        recommendations = new ArrayList<>();
    }
    Job selectedJob = (Job) request.getAttribute("selectedJob");
    String selectedJobId = (String) request.getAttribute("selectedJobId");
    DecimalFormat percentFormat = new DecimalFormat("0.0%");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA Recommendations - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>TA Recommendations</h1>
            <p class="hint">Signed in as <strong><%= attr(loginUser == null ? "Admin" : loginUser.getDisplayName()) %></strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/home">Back to Admin Home</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/workload">Workload Dashboard</a>
        </div>
    </header>

    <section class="card review-summary review-summary-compact">
        <form method="get" action="${pageContext.request.contextPath}/admin/recommendations" class="ta-filter-form ta-filter-inline-form">
            <div class="ta-filter-grid ta-filter-grid-single">
                <div class="ta-filter-field">
                    <label for="jobId">Target Job</label>
                    <select id="jobId" name="jobId">
                        <% for (Job job : jobs) { %>
                        <option value="<%= attr(job.getJobId()) %>" <%= job.getJobId().equalsIgnoreCase(selectedJobId == null ? "" : selectedJobId) ? "selected" : "" %>>
                            <%= attr(job.getTitle()) %> (<%= attr(job.getJobId()) %>)
                        </option>
                        <% } %>
                    </select>
                </div>
            </div>
            <div class="ta-filter-actions">
                <button type="submit">View Recommendations</button>
            </div>
        </form>

        <% if (selectedJob != null) { %>
        <div class="review-sort-meta">
            <span>Requirements: <strong><%= attr(selectedJob.getRequirements()) %></strong></span>
        </div>
        <% } %>
        <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>
    </section>

    <section class="card table-card">
        <table>
            <thead>
            <tr>
                <th>TA</th>
                <th>Matched Skills</th>
                <th>Missing Skills</th>
                <th>Accepted Workload</th>
                <th>Skill Score</th>
                <th>Workload Score</th>
                <th>Final Score</th>
                <th>Explanation</th>
            </tr>
            </thead>
            <tbody>
            <% if (recommendations.isEmpty()) { %>
            <tr><td colspan="8"><div class="empty-state">No recommendations are available.</div></td></tr>
            <% } else { for (TARecommendation recommendation : recommendations) { %>
            <tr>
                <td>
                    <div class="table-main"><strong><%= attr(recommendation.getTaName()) %></strong></div>
                    <div class="table-subtext"><%= attr(recommendation.getTaStudentId()) %></div>
                </td>
                <td><%= display(recommendation.getMatchedSkillsText()) %></td>
                <td><%= display(recommendation.getMissingSkillsText()) %></td>
                <td><%= recommendation.getCurrentWorkload() %></td>
                <td><span class="status-pill tag-info"><%= percentFormat.format(recommendation.getSkillMatchScore()) %></span></td>
                <td><span class="status-pill tag-neutral"><%= percentFormat.format(recommendation.getWorkloadScore()) %></span></td>
                <td><span class="status-pill tag-good"><%= percentFormat.format(recommendation.getFinalScore()) %></span></td>
                <td><%= attr(recommendation.getExplanation()) %></td>
            </tr>
            <% }} %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
