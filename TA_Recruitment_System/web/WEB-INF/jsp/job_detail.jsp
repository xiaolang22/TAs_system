<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%!
    private String attr(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String zhStatus(String value) {
        if (value == null) {
            return "";
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
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/jobs">返回职位列表</a>
        </div>
    </header>

    <%
        Job job = (Job) request.getAttribute("job");
        String applyBlockedReason = (String) request.getAttribute("applyBlockedReason");
        Object savedObj = request.getAttribute("jobSaved");
        boolean canSaveJob = savedObj instanceof Boolean;
        boolean jobSaved = Boolean.TRUE.equals(savedObj);
        String currentRequestPath = (String) request.getAttribute("currentRequestPath");
        if (currentRequestPath == null || currentRequestPath.isBlank()) {
            currentRequestPath = request.getContextPath() + "/jobs";
        }
        if (job != null) {
    %>
    <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">
        ${savedJobMessage}
    </p>
    <p class="alert error ${empty savedJobError ? 'hidden' : ''}">
        ${savedJobError}
    </p>

    <section class="card">
        <h2><%= job.getTitle() %></h2>
        <p><strong>岗位状态：</strong> <%= zhStatus(job.getStatus()) %></p>
        <p><strong>工作时长：</strong> <%= job.getHours() == null ? "" : job.getHours() %></p>
        <p><strong>时间安排：</strong> <%= job.getSchedule() == null ? "" : job.getSchedule() %></p>
        <p><strong>截止时间：</strong> <%= job.getDeadline() == null ? "" : job.getDeadline() %></p>

        <h3>岗位描述</h3>
        <p><%= job.getDescription() == null ? "" : job.getDescription() %></p>

        <h3>技能要求</h3>
        <p><%= job.getRequirements() == null ? "" : job.getRequirements() %></p>

        <% if (canSaveJob) { %>
        <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form detail-save-form">
            <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
            <input type="hidden" name="action" value="<%= jobSaved ? "remove" : "save" %>">
            <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
            <button type="submit" class="<%= jobSaved ? "secondary-btn save-toggle saved" : "save-toggle" %>">
                <%= jobSaved ? "取消收藏" : "收藏职位" %>
            </button>
        </form>
        <% } %>

        <% if (applyBlockedReason != null && !applyBlockedReason.isBlank()) { %>
        <p class="alert error"><%= applyBlockedReason %></p>
        <% } else { %>
        <div class="apply-form">
            <form method="post" action="${pageContext.request.contextPath}/apply">
                <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
                <button type="submit">申请该岗位</button>
            </form>
        </div>
        <% } %>
    </section>
    <%
        } else {
    %>
    <p class="alert error">未找到该岗位。</p>
    <%
        }
    %>
</main>
</body>
</html>
