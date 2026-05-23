<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
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

%>
<%
    String jobListAction = request.getContextPath() + "/jobs";
    boolean showingHidden = Boolean.TRUE.equals(request.getAttribute("showingHidden"));
    Set<String> savedJobIds = (Set<String>) request.getAttribute("savedJobIds");
    boolean canSaveJobs = savedJobIds != null;
    String currentRequestPath = (String) request.getAttribute("currentRequestPath");
    if (currentRequestPath == null || currentRequestPath.isBlank()) {
        currentRequestPath = request.getContextPath() + "/jobs";
    }
    Object hfObj = request.getAttribute("hiddenFromOpenCount");
    int hiddenFromOpen = hfObj instanceof Number ? ((Number) hfObj).intValue() : 0;
    Object hpObj = request.getAttribute("hiddenPoolCount");
    int hiddenPool = hpObj instanceof Number ? ((Number) hpObj).intValue() : 0;
    String viewHiddenJobsUrl = (String) request.getAttribute("viewHiddenJobsUrl");
    if (viewHiddenJobsUrl == null || viewHiddenJobsUrl.isBlank()) {
        viewHiddenJobsUrl = request.getContextPath() + "/jobs?showHidden=1";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= showingHidden ? "历史职位" : "职位列表" %> - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1><%= showingHidden ? "历史职位" : "开放职位" %></h1>
            <p class="hint">
                <%= showingHidden
                        ? "这里展示已截止或已关闭的岗位。"
                        : "在这里搜索、筛选 TA 或监考岗位。已结束岗位会被收纳到历史职位中。" %>
            </p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">返回首页</a>
        </div>
    </header>

    <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">
        ${savedJobMessage}
    </p>
    <p class="alert error ${empty savedJobError ? 'hidden' : ''}">
        ${savedJobError}
    </p>

    <section class="card job-filters-card">
        <h2 class="section-title">搜索与筛选</h2>
        <form class="job-filters-form" method="get" action="<%= jobListAction %>">
            <% if (showingHidden) { %>
            <input type="hidden" name="showHidden" value="1">
            <% } %>
            <div class="filter-grid">
                <div class="form-field">
                    <label for="keyword">关键词</label>
                    <input type="text" id="keyword" name="keyword" autocomplete="off"
                           placeholder="岗位名、描述、时间..."
                           value="${filterKeyword}">
                </div>
                <div class="form-field">
                    <label for="schedule">时间安排</label>
                    <input type="text" id="schedule" name="schedule" placeholder="例如：周一、下午"
                           value="${filterSchedule}">
                </div>
                <div class="form-field">
                    <label for="skills">技能要求</label>
                    <input type="text" id="skills" name="skills" placeholder="要求中的关键词"
                           value="${filterSkills}">
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit">应用筛选</button>
                <a class="link-btn secondary" href="<%= showingHidden ? jobListAction + "?showHidden=1" : jobListAction %>">清空条件</a>
            </div>
        </form>
    </section>

    <div class="filter-summary-wrap">
        <p class="hint filter-summary">
            <% if (!showingHidden) { %>
            当前显示 <strong>${filteredCount}</strong> / <strong>${openJobCount}</strong> 个开放职位。
            <% if (hiddenFromOpen > 0) { %>
            另有 <strong><%= hiddenFromOpen %></strong> 个已关闭或已截止岗位未在此列表显示。
            <% } %>
            <% } else { %>
            当前显示 <strong>${filteredCount}</strong> / <strong><%= hiddenPool %></strong> 个历史职位。
            <% } %>
        </p>
        <% if (!showingHidden && hiddenFromOpen > 0) { %>
        <a class="link-btn secondary filter-summary-btn" href="<%= viewHiddenJobsUrl %>">查看历史职位</a>
        <% } %>
        <% if (showingHidden) { %>
        <a class="link-btn secondary filter-summary-btn" href="${pageContext.request.contextPath}/jobs">返回开放职位</a>
        <% } %>
    </div>

    <section class="job-list">
        <%
            List<Job> jobs = (List<Job>) request.getAttribute("jobs");
            if (jobs != null && !jobs.isEmpty()) {
                for (Job job : jobs) {
        %>
        <article class="card job-card">
            <h3><%= job.getTitle() %></h3>
            <dl class="job-meta">
                <dt>技能要求</dt>
                <dd><%= job.getRequirements() == null ? "" : job.getRequirements() %></dd>
                <dt>时间安排</dt>
                <dd><%= job.getSchedule() == null ? "" : job.getSchedule() %></dd>
                <dt>截止时间</dt>
                <dd><%= job.getDeadline() == null ? "" : job.getDeadline() %></dd>
            </dl>
            <div class="job-card-actions">
                <a href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>" class="link-btn">
                    查看详情 / 申请
                </a>
                <% if (canSaveJobs) {
                    boolean saved = savedJobIds.contains(job.getJobId());
                %>
                <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form">
                    <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                    <input type="hidden" name="action" value="<%= saved ? "remove" : "save" %>">
                    <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
                    <button type="submit" class="<%= saved ? "secondary-btn save-toggle saved" : "save-toggle" %>">
                        <%= saved ? "取消收藏" : "收藏职位" %>
                    </button>
                </form>
                <% } %>
            </div>
        </article>
        <%
                }
            } else {
        %>
        <p class="hint"><%= showingHidden ? "当前筛选条件下没有历史职位。" : "当前筛选条件下没有开放职位。" %></p>
        <%
            }
        %>
    </section>
</main>
</body>
</html>
