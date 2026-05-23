<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>候选人档案 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>候选人档案</h1>
            <p class="hint">在更新决策前，查看该申请人的档案、简历与匹配摘要。</p>
            <p class="hint">当前岗位：<strong>${empty jobTitle ? jobId : jobTitle}</strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${backUrl}">返回申请列表</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">返回岗位列表</a>
        </div>
    </header>

    <section class="detail-layout">
        <div class="detail-stack">
            <section class="card detail-card">
                <h2 class="section-title">${applicant.taName}</h2>
                <div class="review-badges">
                    <span class="status-pill tag-neutral">匹配度：${applicant.matchScore}%</span>
                    <span class="status-pill tag-neutral">${applicant.currentWorkloadLabel}</span>
                    <span class="status-pill tag-neutral">${applicationStatusLabel}</span>
                </div>
            </section>

            <section class="card">
                <h2 class="section-title">档案信息</h2>
                <div class="detail-grid">
                    <div class="detail-item">
                        <span class="label">学号</span>
                        <div class="value">${applicant.taStudentId}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">专业</span>
                        <div class="value">${empty applicant.programme ? '未填写' : applicant.programme}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">技能</span>
                        <div class="value">
                            <div class="skill-cloud">${applicant.coreSkillsHtml}</div>
                        </div>
                    </div>
                    <div class="detail-item">
                        <span class="label">可工作时间</span>
                        <div class="value">${empty applicant.availability ? '未填写' : applicant.availability}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">经历</span>
                        <div class="value">${empty applicant.experience ? '未填写' : applicant.experience}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">申请状态</span>
                        <div class="value">${applicationStatusLabel}</div>
                    </div>
                </div>
            </section>
        </div>

        <aside class="detail-stack">
            <section class="card detail-card">
                <h2 class="section-title">简历</h2>
                <p class="value ${resumeAvailableClass}">
                    <a class="link-btn" href="${resumeHref}" target="_blank" rel="noopener">打开简历</a>
                </p>
                <p class="value ${resumeMissingClass} muted">未上传简历。</p>
            </section>

            <section class="card">
                <h2 class="section-title">匹配摘要</h2>
                <div class="detail-item">
                    <span class="label">已匹配技能</span>
                    <div class="value">${empty applicant.matchedSkillsText ? '暂无' : applicant.matchedSkillsText}</div>
                </div>
                <div class="detail-item detail-note">
                    <span class="label">缺少技能</span>
                    <div class="value">${empty applicant.missingSkillsText ? '暂无' : applicant.missingSkillsText}</div>
                </div>
            </section>
        </aside>
    </section>
</main>
</body>
</html>
