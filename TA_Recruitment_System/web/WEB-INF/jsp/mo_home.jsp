<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.model.LoginUser" %>
<%@ page import="com.group19.dto.MoNotificationView" %>
<%@ page import="com.group19.dto.DeadlineReminderView" %>
<%@ page import="com.group19.dto.MoTaCandidateCard" %>
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
    @SuppressWarnings("unchecked")
    List<MoTaCandidateCard> moCandidates = (List<MoTaCandidateCard>) request.getAttribute("moCandidates");
    @SuppressWarnings("unchecked")
    List<MoNotificationView> moNotifications = (List<MoNotificationView>) request.getAttribute("moNotifications");
    @SuppressWarnings("unchecked")
    List<DeadlineReminderView> deadlineReminders = (List<DeadlineReminderView>) request.getAttribute("deadlineReminders");
    Integer unreadCountAttr = (Integer) request.getAttribute("moUnreadNotificationCount");
    int unreadCount = unreadCountAttr == null ? 0 : unreadCountAttr;
    Integer ownedJobCountAttr = (Integer) request.getAttribute("moOwnedJobCount");
    int ownedJobCount = ownedJobCountAttr == null ? 0 : ownedJobCountAttr;
    Integer allJobCountAttr = (Integer) request.getAttribute("moAllJobCount");
    int allJobCount = allJobCountAttr == null ? 0 : allJobCountAttr;
    Integer totalCountAttr = (Integer) request.getAttribute("moCandidateTotalCount");
    int totalCandidateCount = totalCountAttr == null ? 0 : totalCountAttr;
    Integer filteredCountAttr = (Integer) request.getAttribute("moCandidateFilteredCount");
    int filteredCandidateCount = filteredCountAttr == null ? 0 : filteredCountAttr;
    String displayName = loginUser == null ? "" : loginUser.getDisplayName();
    String avatarPath = loginUser == null ? "" : loginUser.getAvatarPath();
    String avatarUrl = avatarPath == null || avatarPath.isBlank() ? "" : request.getContextPath() + avatarPath;
    String filterMode = (String) request.getAttribute("moFilterMode");
    boolean matchMode = "match".equalsIgnoreCase(filterMode);
    boolean showMatchDetails = Boolean.TRUE.equals(request.getAttribute("moShowMatchDetails"));
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO 首页 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<div class="ta-app-shell">
    <header class="ta-topbar">
        <a class="ta-brand-link" href="${pageContext.request.contextPath}/mo/home" aria-label="返回 MO 首页">
            <span class="ta-brand-logo" aria-hidden="true">
                <img src="${pageContext.request.contextPath}/assets/logo_1.jpg" alt="TA Recruitment System Logo">
            </span>
        </a>

        <div class="ta-topbar-actions">
            <div class="ta-welcome-chip">Welcome! <%= attr(displayName) %></div>

            <a class="ta-avatar-entry" href="${pageContext.request.contextPath}/mo/account" aria-label="进入个人中心">
                <span class="ta-avatar <%= avatarUrl.isEmpty() ? "ta-avatar-fallback" : "" %>">
                    <% if (!avatarUrl.isEmpty()) { %>
                    <img src="<%= attr(avatarUrl) %>" alt="用户头像">
                    <% } else { %>
                    <span><%= firstChar(displayName, "M") %></span>
                    <% } %>
                </span>
            </a>

            <a class="ta-message-btn" href="#moNotificationCenter" aria-label="查看新申请通知">
                <span class="ta-message-icon" aria-hidden="true">&#128276;</span>
                <span>新申请</span>
                <% if (unreadCount > 0) { %>
                <span class="ta-notification-dot"></span>
                <% } %>
            </a>

            <form method="post" action="${pageContext.request.contextPath}/logout" class="ta-logout-form">
                <button type="submit" class="ta-logout-btn">退出登录</button>
            </form>
        </div>
    </header>

    <main class="ta-home-layout">
        <section class="ta-jobs-pane">
            <div class="ta-alert-stack" id="moAlertStack">
                <p class="alert ta-transient-alert success ${empty moCandidateInfo ? 'hidden' : ''}">${moCandidateInfo}</p>
                <p class="alert ta-transient-alert error ${empty moCandidateError ? 'hidden' : ''}">${moCandidateError}</p>
            </div>

            <div class="ta-jobs-scroll" id="moCandidatesScroll">
                <section class="ta-filter-card ta-filter-card-compact">
                    <div class="ta-filter-topbar">
                        <div class="ta-filter-copy">
                            <h1>TA信息栏</h1>
                        </div>
                        <div class="choice-group choice-group-simple ta-mode-switch">
                            <a class="choice-chip choice-chip-mode <%= matchMode ? "" : "is-active" %>" href="${pageContext.request.contextPath}/mo/home">关键词筛选</a>
                            <a class="choice-chip choice-chip-mode <%= matchMode ? "is-active" : "" %>" href="${pageContext.request.contextPath}/mo/home?mode=match">候选人技能匹配</a>
                        </div>
                    </div>

                    <form method="get" action="${pageContext.request.contextPath}/mo/home" class="ta-filter-form ta-filter-inline-form">
                        <input type="hidden" name="mode" value="<%= matchMode ? "match" : "keyword" %>">
                        <% if (matchMode) { %>
                        <div class="ta-filter-grid ta-filter-grid-single">
                            <div class="ta-filter-field">
                                <label for="requiredSkills">技能关键词</label>
                                <input id="requiredSkills" name="requiredSkills" type="text" value="${moRequiredSkills}" placeholder="如：Java，沟通能力，数据结构">
                            </div>
                        </div>
                        <% } else { %>
                        <div class="ta-filter-grid">
                            <div class="ta-filter-field">
                                <label for="keyword">关键词</label>
                                <input id="keyword" name="keyword" type="text" value="${moFilterKeyword}" placeholder="姓名、学号、技能">
                            </div>
                            <div class="ta-filter-field">
                                <label for="programme">专业</label>
                                <input id="programme" name="programme" type="text" value="${moFilterProgramme}" placeholder="如：Computer Science">
                            </div>
                            <div class="ta-filter-field">
                                <label for="availability">可工作时间</label>
                                <input id="availability" name="availability" type="text" value="${moFilterAvailability}" placeholder="如：周三下午 / Monday">
                            </div>
                        </div>
                        <% } %>
                        <div class="ta-filter-actions">
                            <button type="submit"><%= matchMode ? "开始匹配" : "筛选" %></button>
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home<%= matchMode ? "?mode=match" : "" %>">重置</a>
                        </div>
                    </form>
                </section>

                <div class="ta-job-summary">
                    当前显示 <strong><%= filteredCandidateCount %></strong> / <strong><%= totalCandidateCount %></strong> 位 TA。
                    <% if (matchMode) { %>
                    <span>当前模式：候选人技能匹配</span>
                    <% } else { %>
                    <span>当前模式：关键词筛选</span>
                    <% } %>
                </div>

                <div class="ta-job-list-wrap">
                    <div class="ta-job-list">
                        <% if (moCandidates != null && !moCandidates.isEmpty()) {
                            for (MoTaCandidateCard candidate : moCandidates) { %>
                        <article class="ta-job-card">
                            <div class="ta-job-card-head ta-job-card-head-inline">
                                <h2><%= attr(candidate.getDisplayName()) %></h2>
                                <p class="ta-job-badges">
                                    <span class="status-pill tag-info">专业：<%= attr(candidate.getProgrammeDisplay()) %></span>
                                    <span class="status-pill <%= candidate.hasCv() ? "tag-good" : (candidate.isProfileCompleted() ? "tag-warning" : "tag-neutral") %>"><%= attr(candidate.getArchiveStatusLabel()) %></span>
                                </p>
                            </div>
                            <dl class="ta-job-meta-grid">
                                <div>
                                    <dt>技能信息</dt>
                                    <dd><%= attr(candidate.getSkillsDisplay()) %></dd>
                                </div>
                                <div>
                                    <dt>可工作时间</dt>
                                    <dd><%= attr(candidate.getAvailabilityDisplay()) %></dd>
                                </div>
                            </dl>
                            <% if (showMatchDetails) { %>
                            <div class="ta-card-footnote">
                                <strong>匹配度：</strong><%= candidate.getMatchScore() %>%　
                                <strong>说明：</strong><%= attr(candidate.getMatchNote()) %>
                            </div>
                            <% } %>
                            <div class="ta-job-actions">
                                <a class="link-btn" href="${pageContext.request.contextPath}/mo/ta-profile?studentId=<%= attr(candidate.getStudentId()) %>">查看档案 / 简历</a>
                            </div>
                        </article>
                        <% }
                        } else { %>
                        <div class="empty-state">
                            <%= matchMode ? "当前匹配条件下没有找到可展示的 TA。" : "当前筛选条件下没有找到 TA。" %>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            <button type="button" class="ta-scroll-top-btn" id="moScrollTopBtn" aria-label="返回推荐 TA 顶部">返回顶部</button>
        </section>

        <aside class="ta-dashboard-pane">
            <section class="ta-dashboard-card">
                <h2>工作台</h2>
                <div class="ta-side-stat-list">
                    <div class="ta-side-stat-item">我负责的岗位 <strong><%= ownedJobCount %></strong> 个</div>
                    <div class="ta-side-stat-item">系统岗位总数 <strong><%= allJobCount %></strong> 个</div>
                    <div class="ta-side-stat-item">未读新申请 <strong><%= unreadCount %></strong> 条</div>
                </div>
                <div class="ta-quick-links">
                    <a class="link-btn" href="${pageContext.request.contextPath}/mo/post-job">发布岗位</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">岗位列表</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/review">技能匹配页</a>
                </div>
            </section>

            <section class="ta-dashboard-card" id="moNotificationCenter">
                <h2>新申请通知</h2>
                <% if (moNotifications == null || moNotifications.isEmpty()) { %>
                <p class="hint">当前没有与你负责岗位相关的新申请通知。</p>
                <% } else { %>
                <ul class="ta-mini-list is-scrollable">
                    <% for (MoNotificationView item : moNotifications) { %>
                    <li class="ta-mini-list-item <%= item.isUnread() ? "is-unread" : "" %>">
                        <p><%= item.getMessage() %></p>
                        <span><%= item.getCreatedAtDisplay() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= item.getActionUrl() %>">查看对应申请</a>
                        </div>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>

            <section class="ta-dashboard-card">
                <h2>岗位截止提醒</h2>
                <% if (deadlineReminders == null || deadlineReminders.isEmpty()) { %>
                <p class="hint">未来 14 天内暂无与你负责岗位相关的截止提醒。</p>
                <% } else { %>
                <ul class="ta-mini-list is-scrollable">
                    <% for (DeadlineReminderView reminder : deadlineReminders) { %>
                    <li class="ta-mini-list-item">
                        <p><%= reminder.getJobTitle() %></p>
                        <span><%= reminder.getDeadlineDisplay() %> · <%= reminder.getDaysLabel() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= reminder.getActionUrl() %>">查看岗位申请</a>
                        </div>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>
        </aside>
    </main>
</div>

<script>
    (function () {
        var candidatesScroll = document.getElementById('moCandidatesScroll');
        var scrollTopBtn = document.getElementById('moScrollTopBtn');
        var transientAlerts = document.querySelectorAll('.ta-transient-alert');

        function dismissAlertsLater() {
            if (!transientAlerts.length) {
                return;
            }
            window.setTimeout(function () {
                transientAlerts.forEach(function (alertEl) {
                    if (alertEl.classList.contains('hidden')) {
                        return;
                    }
                    alertEl.classList.add('is-dismissing');
                    window.setTimeout(function () {
                        alertEl.classList.add('hidden');
                    }, 320);
                });
            }, 2600);
        }

        if (!candidatesScroll || !scrollTopBtn) {
            dismissAlertsLater();
            return;
        }

        function useWindowScroll() {
            return candidatesScroll.scrollHeight <= candidatesScroll.clientHeight + 8;
        }

        function currentScrollTop() {
            if (useWindowScroll()) {
                return window.pageYOffset || document.documentElement.scrollTop || 0;
            }
            return candidatesScroll.scrollTop;
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
                candidatesScroll.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });

        candidatesScroll.addEventListener('scroll', updateScrollTopButton);
        window.addEventListener('scroll', updateScrollTopButton);
        window.addEventListener('resize', updateScrollTopButton);
        updateScrollTopButton();
        dismissAlertsLater();
    })();
</script>
</body>
</html>
