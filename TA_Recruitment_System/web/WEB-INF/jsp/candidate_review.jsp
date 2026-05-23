<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidate Skill Match Review</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide ta-subpage-shell review-container review-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">Candidate Review</span>
            <h1>Skill Match Review</h1>
            <p class="hint">Signed in as <strong>${loginUser.role}</strong> (<code>${loginUser.username}</code>)</p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="secondary-btn">Logout</button>
            </form>
        </div>
    </header>

    <section class="card role-form-card">
        <div>
            <h2 class="section-title">Review Criteria</h2>
            <p class="hint">Enter job-required skills to calculate each applicant's match score and missing skills.</p>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/mo/review" class="role-form-card">
            <div class="role-form-field">
                <label for="requiredSkills">Job Required Skills *</label>
                <textarea
                        id="requiredSkills"
                        name="requiredSkills"
                        rows="3"
                        placeholder="Example: Java, communication, teaching experience"
                        required>${requiredSkills}</textarea>
            </div>
            <div class="role-submit-row">
                <button type="submit">Calculate Match Score</button>
            </div>
        </form>
    </section>

    <p class="alert success ${empty resultMessage ? 'hidden' : ''}">${resultMessage}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section id="reviewResultPanel" class="card table-card review-result-panel hidden">
        <h2>Review Results</h2>
        <div class="table-wrap">
            <table class="review-table">
                <thead>
                <tr>
                    <th>Candidate</th>
                    <th>Student ID</th>
                    <th>Profile Skills</th>
                    <th>Match Score</th>
                    <th>Missing Skills</th>
                    <th>Notes</th>
                </tr>
                </thead>
                <tbody id="reviewResultBody"></tbody>
            </table>
        </div>
    </section>
</main>

<script id="review-results-data" type="application/json">${reviewResultsJson}</script>
<script>
    (() => {
        const raw = document.getElementById("review-results-data").textContent.trim();
        let rows = [];

        try {
            rows = raw ? JSON.parse(raw) : [];
        } catch (e) {
            rows = [];
        }

        if (!Array.isArray(rows) || rows.length === 0) {
            return;
        }

        const panel = document.getElementById("reviewResultPanel");
        const body = document.getElementById("reviewResultBody");

        for (const item of rows) {
            const tr = document.createElement("tr");

            const missingSkills = Array.isArray(item.missingSkills) && item.missingSkills.length > 0
                ? item.missingSkills.join(", ")
                : "None";

            const cells = [
                item.candidateName || "-",
                item.studentId || "-",
                item.candidateSkillsText || "-",
                String(Number(item.matchScore || 0)) + "%",
                missingSkills,
                item.note || "-"
            ];

            for (const cellText of cells) {
                const td = document.createElement("td");
                td.textContent = cellText;
                tr.appendChild(td);
            }

            body.appendChild(tr);
        }

        panel.classList.remove("hidden");
    })();
</script>
</body>
</html>
