<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidate Skill Matching - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container review-container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>Candidate Skill Matching</h1>
            <p class="hint">Enter the required skills for the role and the system will calculate each candidate's match score and missing skills.</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">Back to Home</a>
        </div>
    </header>

    <form method="get" action="${pageContext.request.contextPath}/mo/review" class="profile-form">
        <label for="requiredSkills">Required Skills</label>
        <textarea
                id="requiredSkills"
                name="requiredSkills"
                rows="3"
                placeholder="For example: Java, communication, teaching experience"
                required>${requiredSkills}</textarea>
        <button type="submit">Calculate Match Score</button>
    </form>

    <p class="alert success ${empty resultMessage ? 'hidden' : ''}">${resultMessage}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section id="reviewResultPanel" class="card review-result-panel hidden">
        <h2>Results</h2>
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
