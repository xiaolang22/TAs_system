<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    boolean showMatchColumn = "match".equals(request.getAttribute("sortMode"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Applicant Review - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>Applicant Review</h1>
            <p class="hint">Current job: <strong>${empty jobTitle ? jobId : jobTitle}</strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">Back to Job List</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">Back to Home</a>
        </div>
    </header>

    <section class="card review-summary review-summary-compact">
        <div class="review-sort-bar">
            <div class="review-sort-meta">
                <span>Total <strong>${applicantCount}</strong> applicants</span>
            </div>
            <div class="choice-group choice-group-simple review-sort-chips">
                <a class="choice-chip choice-chip-mode ${sortMode eq 'match' ? 'is-active' : ''}"
                   href="${pageContext.request.contextPath}/mo/applications?jobId=${jobId}&sort=match">Match Score</a>
                <a class="choice-chip choice-chip-mode ${sortMode eq 'status' ? 'is-active' : ''}"
                   href="${pageContext.request.contextPath}/mo/applications?jobId=${jobId}&sort=status">Application Status</a>
            </div>
        </div>

        <p class="alert success ${empty updated ? 'hidden' : ''}">Application status updated.</p>
        <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>
    </section>

    <section class="card table-card">
        <table>
            <thead>
            <tr>
                <th>Applicant</th>
                <th>Core Skills</th>
                <% if (showMatchColumn) { %>
                <th>Match Score</th>
                <% } %>
                <th>Current Workload</th>
                <th>Application Status</th>
                <th>Profile / Resume</th>
                <th>Update Decision</th>
            </tr>
            </thead>
            <tbody>
            ${applicantRowsHtml}
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
