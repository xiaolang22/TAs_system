<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
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

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
%>
<%
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
    boolean showAll = Boolean.TRUE.equals(request.getAttribute("showAll"));
    boolean showingHidden = Boolean.TRUE.equals(request.getAttribute("showingHidden"));
    int ownedJobCount = request.getAttribute("ownedJobCount") == null ? 0 : (Integer) request.getAttribute("ownedJobCount");
    int allJobCount = request.getAttribute("allJobCount") == null ? 0 : (Integer) request.getAttribute("allJobCount");
    int filteredCount = request.getAttribute("filteredCount") == null ? jobs.size() : (Integer) request.getAttribute("filteredCount");
    int openJobCount = request.getAttribute("openJobCount") == null ? 0 : (Integer) request.getAttribute("openJobCount");
    int hiddenFromOpenCount = request.getAttribute("hiddenFromOpenCount") == null ? 0 : (Integer) request.getAttribute("hiddenFromOpenCount");
    int hiddenPoolCount = request.getAttribute("hiddenPoolCount") == null ? 0 : (Integer) request.getAttribute("hiddenPoolCount");
    String detailLinkPrefix = (String) request.getAttribute("detailLinkPrefix");
    if (detailLinkPrefix == null || detailLinkPrefix.isEmpty()) {
        detailLinkPrefix = request.getContextPath() + "/mo/jobs?jobId=";
    }
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
            <p class="hint">默认显示我发布的岗位，可切换查看全部岗位。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
        </div>
    </header>

    <p class="alert success ${empty param.success ? 'hidden' : ''}">${param.success}</p>
    <p class="alert error ${empty param.error ? 'hidden' : ''}">${param.error}</p>

    <section class="card">
        <div class="ta-job-toolbar-actions">
            <a class="ta-tab-btn <%= showAll ? "" : "is-active" %>" href="${pageContext.request.contextPath}/mo/jobs">我发布的岗位</a>
            <a class="ta-tab-btn <%= showAll ? "is-active" : "" %>" href="${pageContext.request.contextPath}/mo/jobs?showAll=1">全部岗位</a>
        </div>
    </section>

    <section class="ta-filter-card ta-filter-card-compact">
        <form method="get" action="${pageContext.request.contextPath}/mo/jobs" class="ta-filter-form ta-filter-inline-form">
            <% if (showAll) { %>
            <input type="hidden" name="showAll" value="1">
            <% } %>
            <% if (showAll && showingHidden) { %>
            <input type="hidden" name="showHidden" value="1">
            <% } %>
            <div class="ta-filter-topbar">
                <div class="ta-filter-copy">
                    <h1><%= showAll ? "全部岗位" : "我发布的岗位" %></h1>
                </div>
            </div>
            <div class="ta-filter-grid">
                <div class="ta-filter-field">
                    <label for="keyword">关键词</label>
                    <input id="keyword" name="keyword" type="text" value="${filterKeyword}" placeholder="岗位名称、描述、时间">
                </div>
                <div class="ta-filter-field">
                    <label for="schedule">时间安排</label>
                    <input id="schedule" name="schedule" type="text" value="${filterSchedule}" placeholder="如：周三下午">
                </div>
                <div class="ta-filter-field">
                    <label for="skills">技能要求</label>
                    <input id="skills" name="skills" type="text" value="${filterSkills}" placeholder="如：Java / Python">
                </div>
            </div>
            <div class="ta-filter-actions">
                <button type="submit">筛选岗位</button>
                <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs<%= showAll ? (showingHidden ? "?showAll=1&amp;showHidden=1" : "?showAll=1") : "" %>">重置</a>
            </div>
        </form>
    </section>

    <div class="ta-job-summary role-home-summary">
        <% if (showAll) { %>
            <% if (showingHidden) { %>
            当前显示 <strong><%= filteredCount %></strong> / <strong><%= hiddenPoolCount %></strong> 个已关闭岗位。
            <a class="ta-summary-link" href="${viewOpenJobsUrl}">返回开放中岗位</a>
            <% } else { %>
            当前显示 <strong><%= filteredCount %></strong> / <strong><%= openJobCount %></strong> 个开放中岗位。
            <% if (hiddenFromOpenCount > 0) { %>
            另有 <strong><%= hiddenFromOpenCount %></strong> 个已关闭岗位。
            <a class="ta-summary-link" href="${viewHiddenJobsUrl}">查看已关闭岗位</a>
            <% } %>
            <% } %>
        <% } else { %>
        当前显示 <strong><%= filteredCount %></strong> / <strong><%= ownedJobCount %></strong> 个我发布的岗位。
        <span>系统共 <strong><%= allJobCount %></strong> 个岗位。</span>
        <% } %>
    </div>

    <section class="card">
        <div class="ta-job-list">
            <% if (jobs.isEmpty()) { %>
            <div class="empty-state">
                <%= showAll ? "当前筛选条件下没有可查看的岗位。" : "当前筛选条件下没有你发布的岗位。" %>
            </div>
            <% } else { %>
            <% for (Job job : jobs) { %>
            <article class="ta-job-card">
                <div class="ta-job-card-head ta-job-card-head-inline">
                    <h2><%= attr(job.getTitle()) %></h2>
                    <p class="ta-job-badges">
                        <% if (showAll) { %>
                        <span class="status-pill tag-warning">截止：<%= attr(job.getDeadline()) %></span>
                        <span class="status-pill tag-info">工作时长：<%= attr(job.getHours()) %></span>
                        <% if (showingHidden || "CLOSED".equalsIgnoreCase(job.getStatus())) { %>
                        <span class="status-pill tag-neutral">已关闭</span>
                        <% } %>
                        <% } else { %>
                        <span class="status-pill tag-neutral">岗位编号：<%= attr(job.getJobId()) %></span>
                        <span class="status-pill <%= "OPEN".equalsIgnoreCase(job.getStatus()) || job.getStatus() == null ? "tag-good" : "tag-neutral" %>">
                            <%= "OPEN".equalsIgnoreCase(job.getStatus()) || job.getStatus() == null ? "开放中" : "已关闭" %>
                        </span>
                        <% } %>
                    </p>
                </div>

                <% if (showAll) { %>
                <dl class="ta-job-meta-grid">
                    <div>
                        <dt>岗位描述</dt>
                        <dd><%= attr(job.getDescription()) %></dd>
                    </div>
                    <div>
                        <dt>技能要求</dt>
                        <dd><%= attr(job.getRequirements()) %></dd>
                    </div>
                </dl>
                <div class="ta-job-actions">
                    <a class="link-btn" href="<%= attr(detailLinkPrefix) %><%= enc(job.getJobId()) %>">查看详情</a>
                </div>
                <% } else { %>
                <dl class="ta-job-meta-grid ta-job-meta-grid-owned">
                    <div>
                        <dt>岗位描述</dt>
                        <dd><%= attr(job.getDescription()) %></dd>
                    </div>
                    <div>
                        <dt>时间安排</dt>
                        <dd><%= attr(job.getSchedule()) %></dd>
                    </div>
                </dl>
                <div class="ta-job-actions">
                    <a class="link-btn" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= enc(job.getJobId()) %>">查看申请</a>
                    <a class="link-btn secondary" href="<%= attr(detailLinkPrefix) %><%= enc(job.getJobId()) %>">查看详情</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/post-job?jobId=<%= enc(job.getJobId()) %>">修改信息</a>
                    <form method="post" action="${pageContext.request.contextPath}/mo/jobs" class="save-job-form" onsubmit="return confirm('确认删除该岗位吗？');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                        <input type="hidden" name="keyword" value="${filterKeyword}">
                        <input type="hidden" name="schedule" value="${filterSchedule}">
                        <input type="hidden" name="skills" value="${filterSkills}">
                        <button type="submit" class="secondary-btn">删除岗位</button>
                    </form>
                </div>
                <% } %>
            </article>
            <% } %>
            <% } %>
        </div>
    </section>
</main>
</body>
</html>
