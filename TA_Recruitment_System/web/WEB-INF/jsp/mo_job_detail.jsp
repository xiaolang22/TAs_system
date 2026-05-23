<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%!
    private String zhStatus(String value) {
        if (value == null || value.isBlank()) {
            return "开放中";
        }
        if ("OPEN".equalsIgnoreCase(value.trim())) {
            return "开放中";
        }
        if ("CLOSED".equalsIgnoreCase(value.trim())) {
            return "已关闭";
        }
        return value;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>岗位详情 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>岗位详情</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${backUrl}">返回岗位列表</a>
        </div>
    </header>

    <p class="alert error ${empty errorMsg ? 'hidden' : ''}">${errorMsg}</p>

    <%
        Job job = (Job) request.getAttribute("job");
        Boolean isOwnedJobAttr = (Boolean) request.getAttribute("isOwnedJob");
        boolean isOwnedJob = Boolean.TRUE.equals(isOwnedJobAttr);
        if (job != null) {
    %>
    <section class="card detail-card">
        <h2 class="section-title"><%= job.getTitle() == null ? "" : job.getTitle() %></h2>
        <div class="review-badges">
            <span class="status-pill <%= "开放中".equals(zhStatus(job.getStatus())) ? "tag-good" : "tag-neutral" %>">状态：<%= zhStatus(job.getStatus()) %></span>
            <span class="status-pill tag-warning">截止：<%= job.getDeadline() == null ? "" : job.getDeadline() %></span>
            <span class="status-pill tag-info">岗位编号：<%= job.getJobId() == null ? "" : job.getJobId() %></span>
        </div>
    </section>

    <section class="card">
        <div class="detail-grid">
            <div class="detail-item">
                <span class="label">工作时长</span>
                <div class="value"><%= job.getHours() == null ? "" : job.getHours() %></div>
            </div>
            <div class="detail-item">
                <span class="label">时间安排</span>
                <div class="value"><%= job.getSchedule() == null ? "" : job.getSchedule() %></div>
            </div>
            <div class="detail-item">
                <span class="label">岗位描述</span>
                <div class="value"><%= job.getDescription() == null ? "" : job.getDescription() %></div>
            </div>
            <div class="detail-item">
                <span class="label">技能要求</span>
                <div class="value"><%= job.getRequirements() == null ? "" : job.getRequirements() %></div>
            </div>
        </div>
    </section>

    <% if (isOwnedJob) { %>
    <section class="card">
        <div class="ta-job-actions">
            <a class="link-btn" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= job.getJobId() %>">查看申请</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/post-job?jobId=<%= job.getJobId() %>">修改信息</a>
        </div>
    </section>
    <% } %>
    <% } %>
</main>
</body>
</html>
