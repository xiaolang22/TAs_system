<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.model.LoginUser" %>
<%@ page import="com.group19.dto.MoNotificationView" %>
<%@ page import="com.group19.dto.DeadlineReminderView" %>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    @SuppressWarnings("unchecked")
    List<MoNotificationView> moNotifications = (List<MoNotificationView>) request.getAttribute("moNotifications");
    @SuppressWarnings("unchecked")
    List<DeadlineReminderView> deadlineReminders = (List<DeadlineReminderView>) request.getAttribute("deadlineReminders");
    Integer unreadCountAttr = (Integer) request.getAttribute("moUnreadNotificationCount");
    int unreadCount = unreadCountAttr == null ? 0 : unreadCountAttr;
    String displayName = loginUser == null || loginUser.getDisplayName() == null ? "MO" : loginUser.getDisplayName();
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
<main class="container wide role-home-shell">
    <header class="page-header">
        <div>
            <h1>MO 首页</h1>
            <p class="hint">欢迎，<%= displayName %>。从这里进入 MO 相关功能页面。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </div>
    </header>

    <section class="review-summary-grid role-home-summary">
        <div class="summary-card">
            <span class="label">未读通知</span>
            <span class="value"><%= unreadCount %></span>
        </div>
        <div class="summary-card">
            <span class="label">通知预览</span>
            <span class="value"><%= moNotifications == null ? 0 : moNotifications.size() %></span>
        </div>
        <div class="summary-card">
            <span class="label">截止提醒</span>
            <span class="value"><%= deadlineReminders == null ? 0 : deadlineReminders.size() %></span>
        </div>
    </section>

    <section class="card role-home-card">
        <h2 class="section-title">功能入口</h2>
        <div class="role-home-actions">
            <a class="link-btn" href="${pageContext.request.contextPath}/mo/post-job">发布岗位</a>
            <a class="link-btn" href="${pageContext.request.contextPath}/mo/jobs">岗位列表</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/review">候选人评审</a>
        </div>
    </section>

    <section class="role-home-grid">
        <section class="card role-home-card">
            <h2 class="section-title">新申请通知</h2>
            <% if (moNotifications == null || moNotifications.isEmpty()) { %>
            <p class="hint">当前没有新的申请通知。</p>
            <% } else { %>
            <ul class="ta-mini-list">
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

        <section class="card role-home-card">
            <h2 class="section-title">岗位截止提醒</h2>
            <% if (deadlineReminders == null || deadlineReminders.isEmpty()) { %>
            <p class="hint">当前没有新的截止提醒。</p>
            <% } else { %>
            <ul class="ta-mini-list">
                <% for (DeadlineReminderView reminder : deadlineReminders) { %>
                <li class="ta-mini-list-item">
                    <p><%= reminder.getJobTitle() %></p>
                    <span><%= reminder.getDeadlineDisplay() %> · <%= reminder.getDaysLabel() %></span>
                    <div class="role-home-inline-link">
                        <a href="<%= reminder.getActionUrl() %>">查看岗位</a>
                    </div>
                </li>
                <% } %>
            </ul>
            <% } %>
        </section>
    </section>
</main>
</body>
</html>
