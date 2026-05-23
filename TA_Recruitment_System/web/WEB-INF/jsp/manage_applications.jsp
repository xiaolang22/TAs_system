<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.model.Application" %>
<%!
    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String statusClass(String status) {
        if (status == null) {
            return "tag-neutral";
        }
        String normalized = status.trim().toLowerCase();
        if ("accepted".equals(normalized)) {
            return "tag-good";
        }
        if ("shortlisted".equals(normalized)) {
            return "tag-warning";
        }
        if ("rejected".equals(normalized)) {
            return "tag-alert";
        }
        if ("in_review".equals(normalized)) {
            return "tag-info";
        }
        return "tag-neutral";
    }
%>
<%
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    if (applications == null) {
        applications = new ArrayList<>();
    }

    String jobId = (String) request.getAttribute("jobId");
    String errorMsg = (String) request.getAttribute("errorMsg");
    String updated = request.getParameter("updated");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Applications - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide ta-subpage-shell review-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">Applicant Management</span>
            <h1>Manage Applications</h1>
            <p class="hint">Current Job ID: <strong><%= safe(jobId) %></strong></p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn secondary" href="<%= request.getContextPath() %>/mo/jobs">Back to Jobs</a>
            <a class="link-btn secondary" href="<%= request.getContextPath() %>/home">Back to Home</a>
        </div>
    </header>

    <% if ("true".equals(updated)) { %>
    <p class="alert success">Application status updated successfully.</p>
    <% } %>

    <% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <p class="alert error"><%= errorMsg %></p>
    <% } %>

    <% if (applications.isEmpty()) { %>
    <section class="card">
        <div class="empty-state">No applications found for this job.</div>
    </section>
    <% } else { %>
    <section class="card table-card applicant-table-card">
        <table>
            <thead>
            <tr>
                <th>Applicant</th>
                <th>Student ID</th>
                <th>CV File</th>
                <th>Status</th>
                <th>Submitted At</th>
                <th>Updated At</th>
                <th>Decision Note</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <% for (Application app : applications) { %>
            <tr>
                <td><strong><%= safe(app.getTaName()) %></strong></td>
                <td><%= safe(app.getTaStudentId()) %></td>
                <td><%= safe(app.getCvFilePath()) %></td>
                <td>
                    <span class="status-pill <%= statusClass(app.getStatus()) %>"><%= safe(app.getStatus()) %></span>
                </td>
                <td><%= safe(app.getSubmittedAt()) %></td>
                <td><%= safe(app.getUpdatedAt()) %></td>
                <td><%= safe(app.getDecisionNote()) %></td>
                <td>
                    <form method="post" action="<%= request.getContextPath() %>/mo/applications" class="inline-form">
                        <input type="hidden" name="jobId" value="<%= safe(jobId) %>">
                        <input type="hidden" name="applicationId" value="<%= safe(app.getApplicationId()) %>">

                        <select name="status">
                            <option value="SHORTLISTED" <%= "SHORTLISTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>SHORTLISTED</option>
                            <option value="ACCEPTED" <%= "ACCEPTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>ACCEPTED</option>
                            <option value="REJECTED" <%= "REJECTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>REJECTED</option>
                        </select>

                        <textarea name="decisionNote" placeholder="Optional feedback"><%= safe(app.getDecisionNote()) %></textarea>

                        <button type="submit">Update Decision</button>
                    </form>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </section>
    <% } %>
</main>
</body>
</html>
