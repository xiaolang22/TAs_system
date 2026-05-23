<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.group19.model.Job" %>
<%@ page import="com.group19.model.LoginUser" %>
<%@ page import="com.group19.dto.TaNotificationView" %>
<%@ page import="com.group19.dto.DeadlineReminderView" %>
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

%>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    String role = loginUser == null ? "" : loginUser.getRole();
    boolean isTa = "TA".equalsIgnoreCase(role);
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    List<Job> savedJobs = (List<Job>) request.getAttribute("savedJobs");
    Set<String> savedJobIds = (Set<String>) request.getAttribute("savedJobIds");
    List<TaNotificationView> taNotifications = (List<TaNotificationView>) request.getAttribute("taNotifications");
    List<DeadlineReminderView> deadlineReminders = (List<DeadlineReminderView>) request.getAttribute("deadlineReminders");
    Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
    int unreadCount = unreadNotificationCount == null ? 0 : unreadNotificationCount;
    String currentRequestPath = (String) request.getAttribute("currentRequestPath");
    if (currentRequestPath == null || currentRequestPath.isBlank()) {
        currentRequestPath = request.getContextPath() + "/home";
    }
    String avatarPath = loginUser == null ? "" : loginUser.getAvatarPath();
    String avatarUrl = avatarPath == null || avatarPath.isBlank() ? "" : request.getContextPath() + avatarPath;
    String displayName = loginUser == null ? "" : loginUser.getDisplayName();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA 工作台 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<% if (isTa) { %>
<div class="ta-app-shell">
    <header class="ta-topbar">
        <a class="ta-brand-link" href="${pageContext.request.contextPath}/home" aria-label="返回 TA 首页">
            <span class="ta-brand-mark" aria-hidden="true">
                <svg viewBox="0 0 180 180">
                    <defs>
                        <linearGradient id="homeBrandBlue" x1="0%" y1="0%" x2="100%" y2="100%">
                            <stop offset="0%" stop-color="#0f2e8c"/>
                            <stop offset="55%" stop-color="#1663d6"/>
                            <stop offset="100%" stop-color="#2ab8ff"/>
                        </linearGradient>
                    </defs>
                    <circle cx="32" cy="90" r="12" fill="#1663d6"/>
                    <circle cx="148" cy="90" r="12" fill="#27aef5"/>
                    <circle cx="32" cy="130" r="12" fill="#f5a623"/>
                    <circle cx="148" cy="130" r="12" fill="#27aef5"/>
                    <circle cx="90" cy="154" r="12" fill="#2c78ff"/>
                    <path d="M90 28 L145 52 L125 63 C115 58 103 55 90 55 C77 55 65 58 55 63 L35 52 Z" fill="url(#homeBrandBlue)"/>
                    <path d="M131 56 L131 88" stroke="#1663d6" stroke-width="6" stroke-linecap="round"/>
                    <circle cx="131" cy="92" r="7" fill="#1663d6"/>
                    <path d="M131 97 L125 114 H137 Z" fill="#1663d6"/>
                    <path d="M90 62 C122 62 148 88 148 120 C148 133 144 144 137 154 L137 170 L121 161 C112 167 101 170 90 170 C58 170 32 144 32 112 C32 80 58 62 90 62 Z" fill="#ffffff" stroke="url(#homeBrandBlue)" stroke-width="8" stroke-linejoin="round"/>
                    <circle cx="90" cy="104" r="18" fill="url(#homeBrandBlue)"/>
                    <path d="M62 145 C67 129 79 121 90 121 C101 121 113 129 118 145 Z" fill="url(#homeBrandBlue)"/>
                </svg>
            </span>
            <span class="ta-brand-copy">
                <strong>TAHub</strong>
                <span>TA Recruitment System</span>
            </span>
        </a>

        <div class="ta-topbar-actions">
            <a class="ta-message-btn" href="${pageContext.request.contextPath}/ta/applications#notification-center" aria-label="查看申请通知">
                <span class="ta-message-icon" aria-hidden="true">🔔</span>
                <span>消息</span>
                <% if (unreadCount > 0) { %>
                <span class="ta-notification-dot"></span>
                <% } %>
            </a>

            <a class="ta-user-chip" href="${pageContext.request.contextPath}/ta/account">
                <span class="ta-avatar ${avatarUrl.isEmpty() ? "ta-avatar-fallback" : ""}">
                    <% if (!avatarUrl.isEmpty()) { %>
                    <img src="<%= attr(avatarUrl) %>" alt="用户头像">
                    <% } else { %>
                    <span><%= firstChar(displayName, "T") %></span>
                    <% } %>
                </span>
                <span class="ta-user-meta">
                    <strong><%= attr(displayName) %></strong>
                    <span>个人中心</span>
                </span>
            </a>
        </div>
    </header>

    <main class="ta-home-layout">
        <section class="ta-jobs-pane">
            <p class="alert success ${empty param.applySuccess ? 'hidden' : ''}">申请提交成功。</p>
            <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">${savedJobMessage}</p>
            <p class="alert error ${empty savedJobError ? 'hidden' : ''}">${savedJobError}</p>
            <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

            <div class="ta-jobs-scroll" id="taJobsScroll">
                <section class="ta-filter-card">
                    <form method="get" action="${pageContext.request.contextPath}/home" class="ta-filter-form">
                        <% if (Boolean.TRUE.equals(request.getAttribute("showingHidden"))) { %>
                        <input type="hidden" name="showHidden" value="1">
                        <% } %>
                        <div class="ta-filter-topbar">
                            <div class="ta-filter-copy">
                                <h1>职位信息栏</h1>
                                <p class="hint">筛选条件与职位列表同步滚动。</p>
                            </div>
                            <div class="ta-job-toolbar-actions">
                                <a class="ta-tab-btn ${!showingHidden ? 'is-active' : ''}" href="${pageContext.request.contextPath}/home">开放职位</a>
                                <a class="ta-tab-btn ${showingHidden ? 'is-active' : ''}" href="${viewHiddenJobsUrl}">历史职位</a>
                            </div>
                        </div>
                        <div class="ta-filter-grid">
                            <div class="ta-filter-field">
                                <label for="keyword">关键词</label>
                                <input id="keyword" name="keyword" type="text" value="${filterKeyword}" placeholder="岗位名、描述、时间">
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
                            <button type="submit">筛选职位</button>
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home${showingHidden ? '?showHidden=1' : ''}">重置</a>
                        </div>
                    </form>
                </section>

                <div class="ta-job-summary">
                    <% if (!Boolean.TRUE.equals(request.getAttribute("showingHidden"))) { %>
                    当前显示 <strong>${filteredCount}</strong> / <strong>${openJobCount}</strong> 个开放职位。
                    <% if ((Integer) request.getAttribute("hiddenFromOpenCount") > 0) { %>
                    另有 <strong>${hiddenFromOpenCount}</strong> 个已结束或关闭职位。
                    <a class="ta-summary-link" href="${viewHiddenJobsUrl}">查看已结束或关闭职位</a>
                    <% } %>
                    <% } else { %>
                    当前显示 <strong>${filteredCount}</strong> / <strong>${hiddenPoolCount}</strong> 个历史职位。
                    <a class="ta-summary-link" href="${pageContext.request.contextPath}/home">返回开放职位</a>
                    <% } %>
                </div>

                <div class="ta-job-list-wrap">
                    <div class="ta-job-list">
                        <% if (jobs != null && !jobs.isEmpty()) {
                            for (Job job : jobs) {
                                boolean saved = savedJobIds != null && savedJobIds.contains(job.getJobId());
                        %>
                        <article class="ta-job-card">
                            <div class="ta-job-card-head">
                                <h2><%= job.getTitle() == null ? "" : job.getTitle() %></h2>
                                <p class="ta-job-badges">
                                    <span class="status-pill tag-warning">截止：<%= job.getDeadline() == null ? "" : job.getDeadline() %></span>
                                    <span class="status-pill tag-info">工作时长：<%= job.getHours() == null ? "" : job.getHours() %></span>
                                </p>
                            </div>
                            <dl class="ta-job-meta-grid">
                                <div>
                                    <dt>岗位描述</dt>
                                    <dd><%= job.getDescription() == null ? "" : job.getDescription() %></dd>
                                </div>
                                <div>
                                    <dt>技能要求</dt>
                                    <dd><%= job.getRequirements() == null ? "" : job.getRequirements() %></dd>
                                </div>
                            </dl>
                            <div class="ta-job-actions">
                                <a class="link-btn" href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>">查看详情 / 申请</a>
                                <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form">
                                    <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                                    <input type="hidden" name="action" value="<%= saved ? "remove" : "save" %>">
                                    <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
                                    <button type="submit" class="<%= saved ? "secondary-btn save-toggle saved" : "save-toggle" %>">
                                        <%= saved ? "取消收藏" : "收藏职位" %>
                                    </button>
                                </form>
                            </div>
                        </article>
                        <% }
                        } else { %>
                        <div class="empty-state">当前筛选条件下没有找到职位。</div>
                        <% } %>
                    </div>
                </div>
            </div>
            <button type="button" class="ta-scroll-top-btn" id="taScrollTopBtn" aria-label="返回职位信息顶部">返回顶部</button>
        </section>

        <aside class="ta-dashboard-pane">
            <section class="ta-dashboard-card">
                <h2>工作台</h2>
                <div class="ta-quick-links">
                    <a class="link-btn" href="${pageContext.request.contextPath}/profile">申请资料</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/account">个人中心</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/applications">申请进度</a>
                </div>
            </section>

            <section class="ta-dashboard-card">
                <h2>申请通知</h2>
                <% if (taNotifications == null || taNotifications.isEmpty()) { %>
                <p class="hint">暂时没有新的申请通知。</p>
                <% } else { %>
                <ul class="ta-mini-list">
                    <% for (TaNotificationView item : taNotifications) { %>
                    <li class="ta-mini-list-item <%= item.isUnread() ? "is-unread" : "" %>">
                        <p><%= item.getMessage() %></p>
                        <span><%= item.getCreatedAtDisplay() %></span>
                    </li>
                    <% } %>
                </ul>
                <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/applications#notification-center">查看全部通知</a>
                <% } %>
            </section>

            <section class="ta-dashboard-card">
                <h2>截止提醒</h2>
                <% if (deadlineReminders == null || deadlineReminders.isEmpty()) { %>
                <p class="hint">未来 14 天内暂无新的截止提醒。</p>
                <% } else { %>
                <ul class="ta-mini-list">
                    <% for (DeadlineReminderView reminder : deadlineReminders) { %>
                    <li class="ta-mini-list-item">
                        <p><%= reminder.getJobTitle() %></p>
                        <span><%= reminder.getDeadlineDisplay() %> · <%= reminder.getDaysLabel() %></span>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>

            <section class="ta-dashboard-card">
                <h2>已收藏职位</h2>
                <% if (savedJobs == null || savedJobs.isEmpty()) { %>
                <p class="hint">你还没有收藏任何职位。</p>
                <% } else { %>
                <ul class="ta-mini-list">
                    <% for (Job savedJob : savedJobs) { %>
                    <li class="ta-mini-list-item">
                        <p><%= savedJob.getTitle() == null ? "" : savedJob.getTitle() %></p>
                        <span>截止：<%= savedJob.getDeadline() == null ? "" : savedJob.getDeadline() %></span>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>
        </aside>
    </main>
</div>
<% } else { %>
<main class="container">
    <header class="page-header">
        <div>
            <h1>首页</h1>
            <p class="hint">当前页面已保留，TA 主界面改版仅作用于 TA 角色。</p>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit" class="secondary-btn">退出登录</button>
        </form>
    </header>
</main>
<% } %>
<script>
    (function () {
        var jobsScroll = document.getElementById('taJobsScroll');
        var scrollTopBtn = document.getElementById('taScrollTopBtn');
        if (!jobsScroll || !scrollTopBtn) {
            return;
        }

        function useWindowScroll() {
            return jobsScroll.scrollHeight <= jobsScroll.clientHeight + 8;
        }

        function currentScrollTop() {
            if (useWindowScroll()) {
                return window.pageYOffset || document.documentElement.scrollTop || 0;
            }
            return jobsScroll.scrollTop;
        }

        function updateScrollTopButton() {
            if (currentScrollTop() > 140) {
                scrollTopBtn.classList.add('is-visible');
            } else {
                scrollTopBtn.classList.remove('is-visible');
            }
        }

        scrollTopBtn.addEventListener('click', function () {
            if (useWindowScroll()) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
            } else {
                jobsScroll.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });

        jobsScroll.addEventListener('scroll', updateScrollTopButton);
        window.addEventListener('scroll', updateScrollTopButton);
        window.addEventListener('resize', updateScrollTopButton);
        updateScrollTopButton();
    })();
</script>
</body>
</html>
