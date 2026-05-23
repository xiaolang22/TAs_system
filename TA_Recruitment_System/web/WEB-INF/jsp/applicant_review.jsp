<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Applicant Review - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide ta-subpage-shell review-shell role-page-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>Applicant Review</h1>
            <p class="hint">
                Compare candidates for a single job, sort them by status or match score, and open each profile or resume.
            </p>
            <p class="hint">
                Job: <strong>${empty jobTitle ? jobId : jobTitle}</strong>
            </p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">Back to job list</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to home</a>
        </div>
    </header>

    <section class="card review-summary">
        <div class="review-toolbar">
            <div>
                <h2 class="section-title">Sort and Filter</h2>
                <p class="hint">Default sort is by match degree. Switching to status groups applicants by workflow stage.</p>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/mo/applications">
                <input type="hidden" name="jobId" value="${jobId}">
                <div class="toolbar-field">
                    <label for="sortMode">Sort by</label>
                    <select id="sortMode" name="sort">
                        <option value="match" ${sortMode eq 'match' ? 'selected' : ''}>Match degree</option>
                        <option value="status" ${sortMode eq 'status' ? 'selected' : ''}>Status</option>
                    </select>
                </div>
                <button type="submit">Apply</button>
            </form>
        </div>

        <div class="review-summary-grid">
            <div class="summary-card">
                <span class="label">Job status</span>
                <span class="value">${empty job.status ? 'OPEN' : job.status}</span>
            </div>
            <div class="summary-card">
                <span class="label">Applicants</span>
                <span class="value">${applicantCount}</span>
            </div>
            <div class="summary-card">
                <span class="label">Sorted by</span>
                <span class="value">${sortLabel}</span>
            </div>
        </div>

        <p class="hint">
            Status workflow: SUBMITTED -&gt; IN_REVIEW -&gt; SHORTLISTED -&gt; ACCEPTED/REJECTED. Once an application reaches a later stage, it cannot be moved back to an earlier one.
        </p>
        <p class="alert success ${empty updated ? 'hidden' : ''}">Application status updated successfully.</p>
        <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>
    </section>

    <section class="card recommendation-card">
        <div class="recommendation-head">
            <div>
                <h2 class="section-title">TA Recommendations</h2>
                <p class="hint">
                    Applicants are ranked for this job using 70% skill match and 30% accepted workload, so fairness is visible before decisions are updated.
                </p>
            </div>
        </div>
        <div class="recommendation-list">
            ${recommendationCardsHtml}
        </div>
    </section>

    <section class="card table-card applicant-table-card">
        <table>
            <thead>
            <tr>
                <th>Applicant</th>
                <th>Core skills</th>
                <th>Match degree</th>
                <th>Current workload</th>
                <th>Status</th>
                <th>Profile</th>
                <th>Resume</th>
                <th>Update decision</th>
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
