<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    boolean showMatchColumn = "match".equals(request.getAttribute("sortMode"));
%>
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
            <p class="hint">当前岗位：<strong>${empty jobTitle ? jobId : jobTitle}</strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">返回岗位列表</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <section class="card review-summary review-summary-compact">
        <div class="review-sort-bar">
            <div class="review-sort-meta">
                <span>共 <strong>${applicantCount}</strong> 位申请人</span>
            </div>
            <div class="choice-group choice-group-simple review-sort-chips">
                <a class="choice-chip choice-chip-mode ${sortMode eq 'match' ? 'is-active' : ''}"
                   href="${pageContext.request.contextPath}/mo/applications?jobId=${jobId}&sort=match">匹配度</a>
                <a class="choice-chip choice-chip-mode ${sortMode eq 'status' ? 'is-active' : ''}"
                   href="${pageContext.request.contextPath}/mo/applications?jobId=${jobId}&sort=status">申请状态</a>
            </div>
        </div>

        <p class="alert success ${empty updated ? 'hidden' : ''}">申请状态已更新。</p>
        <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>
    </section>

    <section class="card table-card">
        <table>
            <thead>
            <tr>
                <th>申请人</th>
                <th>核心技能</th>
                <% if (showMatchColumn) { %>
                <th>匹配度</th>
                <% } %>
                <th>当前工作量</th>
                <th>申请状态</th>
                <th>档案 / 简历</th>
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
