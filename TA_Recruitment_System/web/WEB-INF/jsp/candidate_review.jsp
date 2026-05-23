<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>候选人技能匹配 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container review-container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>候选人技能匹配</h1>
            <p class="hint">输入岗位所需技能后，系统会计算各候选人的匹配度与缺少技能。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <form method="get" action="${pageContext.request.contextPath}/mo/review" class="profile-form">
        <label for="requiredSkills">岗位所需技能</label>
        <textarea
                id="requiredSkills"
                name="requiredSkills"
                rows="3"
                placeholder="例如：Java，沟通能力，教学经验"
                required>${requiredSkills}</textarea>
        <button type="submit">计算匹配度</button>
    </form>

    <p class="alert success ${empty resultMessage ? 'hidden' : ''}">${resultMessage}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section id="reviewResultPanel" class="card review-result-panel hidden">
        <h2>计算结果</h2>
        <div class="table-wrap">
            <table class="review-table">
                <thead>
                <tr>
                    <th>候选人</th>
                    <th>学号</th>
                    <th>档案技能</th>
                    <th>匹配度</th>
                    <th>缺少技能</th>
                    <th>说明</th>
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
                : "无";

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
