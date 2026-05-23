<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.dto.AdminFeedItem" %>
<%@ page import="com.group19.model.Job" %>
<%@ page import="com.group19.model.LoginUser" %>
<%@ page import="com.group19.model.UserAccount" %>
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

    private String firstChar(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return fallback;
        }
        return trimmed.substring(0, 1);
    }

    private String jobStatusLabel(Job job) {
        if (job == null || job.getStatus() == null) {
            return "开放中";
        }
        return "CLOSED".equalsIgnoreCase(job.getStatus()) ? "已关闭" : "开放中";
    }
%>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    @SuppressWarnings("unchecked")
    List<UserAccount> taAccounts = (List<UserAccount>) request.getAttribute("taAccounts");
    @SuppressWarnings("unchecked")
    List<UserAccount> moAccounts = (List<UserAccount>) request.getAttribute("moAccounts");
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    @SuppressWarnings("unchecked")
    List<AdminFeedItem> recentJobs = (List<AdminFeedItem>) request.getAttribute("recentJobs");
    @SuppressWarnings("unchecked")
    List<AdminFeedItem> recentApplications = (List<AdminFeedItem>) request.getAttribute("recentApplications");
    @SuppressWarnings("unchecked")
    List<AdminFeedItem> recentAlerts = (List<AdminFeedItem>) request.getAttribute("recentAlerts");
    if (taAccounts == null) {
        taAccounts = new ArrayList<>();
    }
    if (moAccounts == null) {
        moAccounts = new ArrayList<>();
    }
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
    if (recentJobs == null) {
        recentJobs = new ArrayList<>();
    }
    if (recentApplications == null) {
        recentApplications = new ArrayList<>();
    }
    if (recentAlerts == null) {
        recentAlerts = new ArrayList<>();
    }
    UserAccount selectedTa = (UserAccount) request.getAttribute("selectedTa");
    UserAccount selectedMo = (UserAccount) request.getAttribute("selectedMo");
    Job selectedJob = (Job) request.getAttribute("selectedJob");
    String displayName = loginUser == null ? "管理员" : loginUser.getDisplayName();
    String avatarPath = loginUser == null ? "" : loginUser.getAvatarPath();
    String avatarUrl = avatarPath == null || avatarPath.isBlank() ? "" : request.getContextPath() + avatarPath;
    String successMsg = (String) request.getAttribute("successMsg");
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员首页 - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<div class="ta-app-shell admin-app-shell">
    <header class="ta-topbar">
        <a class="ta-brand-link" href="${pageContext.request.contextPath}/admin/home" aria-label="返回管理员首页">
            <span class="ta-brand-logo" aria-hidden="true">
                <img src="${pageContext.request.contextPath}/assets/logo_1.jpg" alt="TA Recruitment System Logo">
            </span>
        </a>

        <div class="ta-topbar-actions">
            <div class="ta-welcome-chip">Welcome! <%= attr(displayName) %></div>

            <a class="ta-avatar-entry" href="${pageContext.request.contextPath}/admin/account" aria-label="进入个人中心">
                <span class="ta-avatar <%= avatarUrl.isEmpty() ? "ta-avatar-fallback" : "" %>">
                    <% if (!avatarUrl.isEmpty()) { %>
                    <img src="<%= attr(avatarUrl) %>" alt="用户头像">
                    <% } else { %>
                    <span><%= firstChar(displayName, "A") %></span>
                    <% } %>
                </span>
            </a>

            <form method="post" action="${pageContext.request.contextPath}/logout" class="ta-logout-form">
                <button type="submit" class="ta-logout-btn">退出登录</button>
            </form>
        </div>
    </header>

    <main class="admin-home-layout">
        <section class="admin-workbench-pane">
            <div class="ta-alert-stack">
                <p class="alert success <%= successMsg == null || successMsg.isBlank() ? "hidden" : "" %>"><%= successMsg == null ? "" : attr(successMsg) %></p>
                <p class="alert error <%= errorMsg == null || errorMsg.isBlank() ? "hidden" : "" %>"><%= errorMsg == null ? "" : attr(errorMsg) %></p>
            </div>

            <section class="admin-panel-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>TA管理</h2>
                        <span><%= taAccounts.size() %> 位 TA</span>
                    </div>
                </div>

                <% if (selectedTa != null) { %>
                <section class="admin-detail-card">
                    <div class="admin-detail-head">
                        <strong><%= attr(selectedTa.getDisplayName()) %></strong>
                        <span class="status-pill <%= selectedTa.isFrozen() ? "tag-alert" : "tag-good" %>">
                            <%= selectedTa.isFrozen() ? "已冻结" : "正常" %>
                        </span>
                    </div>
                    <div class="admin-detail-grid">
                        <div><span class="label">账号</span><span class="value"><%= attr(selectedTa.getUsername()) %></span></div>
                        <div><span class="label">学号</span><span class="value"><%= attr(selectedTa.getUserId()) %></span></div>
                    </div>
                    <div class="admin-inline-actions">
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="resetTaPassword">
                            <input type="hidden" name="userId" value="<%= attr(selectedTa.getUserId()) %>">
                            <input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>">
                            <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <button type="submit" class="secondary-btn">重置密码</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="toggleTaFreeze">
                            <input type="hidden" name="userId" value="<%= attr(selectedTa.getUserId()) %>">
                            <input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>">
                            <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <button type="submit" class="secondary-btn"><%= selectedTa.isFrozen() ? "解冻账号" : "冻结账号" %></button>
                        </form>
                    </div>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (UserAccount account : taAccounts) { %>
                    <article class="admin-list-item <%= selectedTa != null && account.getUserId().equalsIgnoreCase(selectedTa.getUserId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(account.getDisplayName()) %></strong>
                            <span><%= attr(account.getUsername()) %> · <%= account.isFrozen() ? "已冻结" : "正常" %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/home?selectedTaId=<%= attr(account.getUserId()) %><% if (selectedMo != null) { %>&selectedMoId=<%= attr(selectedMo.getUserId()) %><% } %><% if (selectedJob != null) { %>&selectedJobId=<%= attr(selectedJob.getJobId()) %><% } %>">查看详情</a>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>

            <section class="admin-panel-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>MO管理</h2>
                        <span><%= moAccounts.size() %> 位 MO</span>
                    </div>
                </div>

                <% if (selectedMo != null) { %>
                <section class="admin-detail-card">
                    <div class="admin-detail-head">
                        <strong><%= attr(selectedMo.getDisplayName()) %></strong>
                        <span class="status-pill <%= selectedMo.isFrozen() ? "tag-alert" : "tag-good" %>">
                            <%= selectedMo.isFrozen() ? "已冻结" : "正常" %>
                        </span>
                    </div>
                    <div class="admin-detail-grid">
                        <div><span class="label">账号</span><span class="value"><%= attr(selectedMo.getUsername()) %></span></div>
                        <div><span class="label">编号</span><span class="value"><%= attr(selectedMo.getUserId()) %></span></div>
                    </div>
                    <div class="admin-inline-actions">
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="resetMoPassword">
                            <input type="hidden" name="userId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                            <input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <button type="submit" class="secondary-btn">重置密码</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="toggleMoFreeze">
                            <input type="hidden" name="userId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                            <input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <button type="submit" class="secondary-btn"><%= selectedMo.isFrozen() ? "解冻账号" : "冻结账号" %></button>
                        </form>
                    </div>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (UserAccount account : moAccounts) { %>
                    <article class="admin-list-item <%= selectedMo != null && account.getUserId().equalsIgnoreCase(selectedMo.getUserId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(account.getDisplayName()) %></strong>
                            <span><%= attr(account.getUsername()) %> · <%= account.isFrozen() ? "已冻结" : "正常" %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/home?selectedMoId=<%= attr(account.getUserId()) %><% if (selectedTa != null) { %>&selectedTaId=<%= attr(selectedTa.getUserId()) %><% } %><% if (selectedJob != null) { %>&selectedJobId=<%= attr(selectedJob.getJobId()) %><% } %>">查看详情</a>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>

            <section class="admin-panel-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>岗位管理</h2>
                        <span><%= jobs.size() %> 个岗位</span>
                    </div>
                </div>

                <% if (selectedJob != null) { %>
                <section class="admin-detail-card">
                    <div class="admin-detail-head">
                        <strong><%= attr(selectedJob.getTitle()) %></strong>
                        <span class="status-pill <%= "CLOSED".equalsIgnoreCase(selectedJob.getStatus()) ? "tag-neutral" : "tag-good" %>">
                            <%= jobStatusLabel(selectedJob) %>
                        </span>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/admin/home" class="admin-edit-form">
                        <input type="hidden" name="action" value="updateJob">
                        <input type="hidden" name="jobId" value="<%= attr(selectedJob.getJobId()) %>">
                        <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                        <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                        <input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>">

                        <label for="adminJobTitle">岗位名称</label>
                        <input id="adminJobTitle" name="title" type="text" value="<%= attr(selectedJob.getTitle()) %>" required>

                        <div class="admin-edit-grid">
                            <div>
                                <label for="adminJobHours">工作时长</label>
                                <input id="adminJobHours" name="hours" type="text" value="<%= attr(selectedJob.getHours()) %>" required>
                            </div>
                            <div>
                                <label for="adminJobDeadline">截止时间</label>
                                <input id="adminJobDeadline" name="deadline" type="date" value="<%= attr(selectedJob.getDeadline()) %>" required>
                            </div>
                            <div>
                                <label for="adminJobStatus">岗位状态</label>
                                <select id="adminJobStatus" name="status">
                                    <option value="OPEN" <%= "CLOSED".equalsIgnoreCase(selectedJob.getStatus()) ? "" : "selected" %>>OPEN</option>
                                    <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(selectedJob.getStatus()) ? "selected" : "" %>>CLOSED</option>
                                </select>
                            </div>
                        </div>

                        <label for="adminJobSchedule">时间安排</label>
                        <input id="adminJobSchedule" name="schedule" type="text" value="<%= attr(selectedJob.getSchedule()) %>" required>

                        <label for="adminJobDescription">岗位描述</label>
                        <textarea id="adminJobDescription" name="description" rows="3" required><%= attr(selectedJob.getDescription()) %></textarea>

                        <label for="adminJobRequirements">技能要求</label>
                        <textarea id="adminJobRequirements" name="requirements" rows="3" required><%= attr(selectedJob.getRequirements()) %></textarea>

                        <div class="admin-inline-actions">
                            <button type="submit">保存修改</button>
                        </div>
                    </form>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (Job job : jobs) { %>
                    <article class="admin-list-item <%= selectedJob != null && job.getJobId().equalsIgnoreCase(selectedJob.getJobId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(job.getTitle()) %></strong>
                            <span><%= attr(job.getJobId()) %> · <%= jobStatusLabel(job) %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/home?selectedJobId=<%= attr(job.getJobId()) %><% if (selectedTa != null) { %>&selectedTaId=<%= attr(selectedTa.getUserId()) %><% } %><% if (selectedMo != null) { %>&selectedMoId=<%= attr(selectedMo.getUserId()) %><% } %>">查看详情</a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/home" onsubmit="return confirm('确认删除该岗位吗？');">
                                <input type="hidden" name="action" value="deleteJob">
                                <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                                <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                                <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                                <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                                <button type="submit" class="secondary-btn">删除岗位</button>
                            </form>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>
        </section>

        <aside class="admin-monitor-pane">
            <section class="admin-panel-card admin-monitor-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>监控栏</h2>
                        <span>全局概览</span>
                    </div>
                </div>

                <div class="admin-stat-grid">
                    <div class="summary-card">
                        <span class="label">当前开放岗位数</span>
                        <span class="value">${openJobCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">已关闭岗位数</span>
                        <span class="value">${closedJobCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">TA 总人数</span>
                        <span class="value">${taCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">MO 总人数</span>
                        <span class="value">${moCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">待处理申请数</span>
                        <span class="value">${pendingApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">已录用数</span>
                        <span class="value">${acceptedApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">已拒绝数</span>
                        <span class="value">${rejectedApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">工作量预警人数</span>
                        <span class="value">${workloadWarningCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">时间冲突人数</span>
                        <span class="value">${timeConflictCount}</span>
                    </div>
                </div>
            </section>

            <section class="admin-panel-card admin-feed-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>最近新增岗位</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentJobs.isEmpty()) { %>
                    <div class="empty-state">暂无数据</div>
                    <% } else { for (AdminFeedItem item : recentJobs) { %>
                    <article class="admin-feed-item">
                        <strong><%= attr(item.getTitle()) %></strong>
                        <span><%= attr(item.getMeta()) %></span>
                    </article>
                    <% }} %>
                </div>
            </section>

            <section class="admin-panel-card admin-feed-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>最近新增申请</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentApplications.isEmpty()) { %>
                    <div class="empty-state">暂无数据</div>
                    <% } else { for (AdminFeedItem item : recentApplications) { %>
                    <article class="admin-feed-item">
                        <strong><%= attr(item.getTitle()) %></strong>
                        <span><%= attr(item.getMeta()) %></span>
                    </article>
                    <% }} %>
                </div>
            </section>

            <section class="admin-panel-card admin-feed-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>最近异常操作</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentAlerts.isEmpty()) { %>
                    <div class="empty-state">暂无异常</div>
                    <% } else { for (AdminFeedItem item : recentAlerts) { %>
                    <article class="admin-feed-item">
                        <strong><%= attr(item.getTitle()) %></strong>
                        <span><%= attr(item.getMeta()) %></span>
                    </article>
                    <% }} %>
                </div>
            </section>
        </aside>
    </main>
</div>
</body>
</html>
