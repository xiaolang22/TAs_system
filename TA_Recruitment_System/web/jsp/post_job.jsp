<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%
    Job job = (Job) request.getAttribute("job");
    if (job == null) {
        job = new Job();
    }

    String errorMsg = (String) request.getAttribute("errorMsg");
    String success = request.getParameter("success");
    boolean editing = Boolean.TRUE.equals(request.getAttribute("editing")) || job.getJobId() != null;
    boolean edited = "true".equals(request.getParameter("edited"));
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= editing ? "修改岗位" : "发布岗位" %> - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1><%= editing ? "修改岗位" : "发布岗位" %></h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <p class="alert success <%= "true".equals(success) ? "" : "hidden" %>"><%= edited ? "岗位信息已更新。" : "岗位发布成功。" %></p>
    <p class="alert error <%= errorMsg == null || errorMsg.trim().isEmpty() ? "hidden" : "" %>"><%= errorMsg == null ? "" : errorMsg %></p>

    <form method="post" action="<%= request.getContextPath() %>/mo/post-job" class="profile-form">
        <input type="hidden" name="jobId" value="<%= job.getJobId() == null ? "" : job.getJobId() %>">

        <label for="title">岗位名称</label>
        <input
                type="text"
                id="title"
                name="title"
                placeholder="例如：Java 程序设计课程助教"
                value="<%= job.getTitle() == null ? "" : job.getTitle() %>">

        <label for="hours">工作时长</label>
        <input
                type="text"
                id="hours"
                name="hours"
                placeholder="例如：6 小时/周"
                value="<%= job.getHours() == null ? "" : job.getHours() %>">

        <label for="description">岗位描述</label>
        <textarea
                id="description"
                name="description"
                rows="4"
                placeholder="说明该岗位的主要职责。"><%= job.getDescription() == null ? "" : job.getDescription() %></textarea>

        <label for="requirements">技能要求</label>
        <textarea
                id="requirements"
                name="requirements"
                rows="4"
                placeholder="说明希望 TA 具备的技能或经验。"><%= job.getRequirements() == null ? "" : job.getRequirements() %></textarea>

        <label for="schedule">时间安排</label>
        <input
                type="text"
                id="schedule"
                name="schedule"
                placeholder="例如：周二 14:00-16:00，周四线上答疑"
                value="<%= job.getSchedule() == null ? "" : job.getSchedule() %>">

        <label for="deadline">截止时间</label>
        <input
                type="date"
                id="deadline"
                name="deadline"
                value="<%= job.getDeadline() == null ? "" : job.getDeadline() %>">

        <button type="submit"><%= editing ? "保存修改" : "发布岗位" %></button>
    </form>
</main>
</body>
</html>
