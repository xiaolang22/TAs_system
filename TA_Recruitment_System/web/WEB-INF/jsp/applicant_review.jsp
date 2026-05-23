<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>候选人评审 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>候选人评审</h1>
            <p class="hint">围绕单个岗位查看申请人、匹配度、工作量与申请状态。</p>
            <p class="hint">当前岗位：<strong>${empty jobTitle ? jobId : jobTitle}</strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">返回岗位列表</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <section class="card review-summary">
        <div class="review-toolbar">
            <div>
                <h2 class="section-title">排序方式</h2>
                <p class="hint">默认按匹配度排序，也可以切换为按申请状态排序。</p>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/mo/applications">
                <input type="hidden" name="jobId" value="${jobId}">
                <div class="toolbar-field">
                    <label for="sortMode">排序依据</label>
                    <select id="sortMode" name="sort">
                        <option value="match" ${sortMode eq 'match' ? 'selected' : ''}>匹配度</option>
                        <option value="status" ${sortMode eq 'status' ? 'selected' : ''}>申请状态</option>
                    </select>
                </div>
                <button type="submit">应用</button>
            </form>
        </div>

        <div class="review-summary-grid">
            <div class="summary-card">
                <span class="label">岗位状态</span>
                <span class="value">${empty job.status ? 'OPEN' : job.status}</span>
            </div>
            <div class="summary-card">
                <span class="label">申请人数</span>
                <span class="value">${applicantCount}</span>
            </div>
            <div class="summary-card">
                <span class="label">当前排序</span>
                <span class="value">${sortLabel}</span>
            </div>
        </div>

        <p class="hint">申请流程：已提交 -> 审核中 -> 已入围 -> 已录用 / 已拒绝。</p>
        <p class="alert success ${empty updated ? 'hidden' : ''}">申请状态已更新。</p>
        <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>
    </section>

    <section class="card recommendation-card">
        <div class="recommendation-head">
            <div>
                <h2 class="section-title">TA 推荐结果</h2>
                <p class="hint">系统综合匹配技能与当前工作量，对候选人进行排序推荐。</p>
            </div>
        </div>
        <div class="recommendation-list">
            ${recommendationCardsHtml}
        </div>
    </section>

    <section class="card table-card">
        <table>
            <thead>
            <tr>
                <th>申请人</th>
                <th>核心技能</th>
                <th>匹配度</th>
                <th>当前工作量</th>
                <th>申请状态</th>
                <th>档案</th>
                <th>简历</th>
                <th>更新决策</th>
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
