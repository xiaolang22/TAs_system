<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.model.Job" %>
<%
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
    Boolean showAllAttr = (Boolean) request.getAttribute("showAll");
    boolean showAll = Boolean.TRUE.equals(showAllAttr);
    Integer ownedJobCountAttr = (Integer) request.getAttribute("ownedJobCount");
    int ownedJobCount = ownedJobCountAttr == null ? 0 : ownedJobCountAttr;
    Integer allJobCountAttr = (Integer) request.getAttribute("allJobCount");
    int allJobCount = allJobCountAttr == null ? 0 : allJobCountAttr;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>岗位列表 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>岗位列表</h1>
            <p class="hint">默认展示你发布的岗位，也可以切换查看全部岗位。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <section class="card">
        <div class="ta-job-toolbar-actions">
            <a class="ta-tab-btn <%= showAll ? "" : "is-active" %>" href="${pageContext.request.contextPath}/mo/jobs">我发布的岗位</a>
            <a class="ta-tab-btn <%= showAll ? "is-active" : "" %>" href="${pageContext.request.contextPath}/mo/jobs?showAll=1">全部岗位</a>
        </div>
        <div class="ta-job-summary role-home-summary">
            当前显示 <strong><%= jobs.size() %></strong> 个岗位。
            <% if (showAll) { %>
            其中你负责 <strong><%= ownedJobCount %></strong> 个，系统共 <strong><%= allJobCount %></strong> 个岗位。
            <% } else { %>
            你当前共负责 <strong><%= ownedJobCount %></strong> 个岗位。
            <% } %>
        </div>
    </section>

    <section class="card">
        <div class="ta-job-list">
            <% if (jobs.isEmpty()) { %>
            <div class="empty-state">
                <%= showAll ? "当前没有可查看的岗位。" : "你当前还没有负责任何岗位。" %>
            </div>
            <% } else { %>
            <% for (Job job : jobs) { %>
            <article class="ta-job-card">
                <div class="ta-job-card-head">
                    <h2><%= job.getTitle() == null ? "" : job.getTitle() %></h2>
                    <p class="ta-job-badges">
                        <span class="status-pill tag-warning">截止：<%= job.getDeadline() == null ? "" : job.getDeadline() %></span>
                        <span class="status-pill <%= "OPEN".equalsIgnoreCase(job.getStatus()) ? "tag-good" : "tag-neutral" %>">
                            状态：<%= "OPEN".equalsIgnoreCase(job.getStatus()) || job.getStatus() == null ? "开放中" : "已关闭" %>
                        </span>
                    </p>
                </div>
                <dl class="ta-job-meta-grid">
                    <div>
                        <dt>岗位编号</dt>
                        <dd><%= job.getJobId() == null ? "" : job.getJobId() %></dd>
                    </div>
                    <div>
                        <dt>负责 MO</dt>
                        <dd><%= job.getOwnerMoUserId() == null ? "-" : job.getOwnerMoUserId() %></dd>
                    </div>
                    <div>
                        <dt>时间安排</dt>
                        <dd><%= job.getSchedule() == null ? "" : job.getSchedule() %></dd>
                    </div>
                    <div>
                        <dt>岗位描述</dt>
                        <dd><%= job.getDescription() == null ? "" : job.getDescription() %></dd>
                    </div>
                </dl>
                <div class="ta-job-actions">
                    <a class="link-btn" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= job.getJobId() %>">查看申请</a>
                </div>
            </article>
            <% } %>
            <% } %>
        </div>
    </section>
</main>
</body>
</html>
