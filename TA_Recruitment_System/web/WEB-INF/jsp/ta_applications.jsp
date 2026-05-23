<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.dto.TaApplicationOverview" %>
<%@ page import="com.group19.dto.TaTimelineStep" %>
<%@ page import="com.group19.dto.TaNotificationView" %>
<%
    @SuppressWarnings("unchecked")
    List<TaApplicationOverview> applications = (List<TaApplicationOverview>) request.getAttribute("applications");
    @SuppressWarnings("unchecked")
    List<TaNotificationView> taNotifications = (List<TaNotificationView>) request.getAttribute("taNotifications");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>申请通知与进度 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>申请通知与进度</h1>
            <p class="hint">查看消息提醒、岗位申请状态与完整时间线。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">返回首页</a>
        </div>
    </header>

    <section class="card notification-panel" id="notification-center">
        <h2>申请通知</h2>
        <% if (taNotifications == null || taNotifications.isEmpty()) { %>
        <p class="hint">目前没有新的申请通知。</p>
        <% } else { %>
        <ul class="notification-list">
            <% for (TaNotificationView item : taNotifications) { %>
            <li class="notification-item <%= item.isUnread() ? "notification-unread" : "" %>">
                <p class="notification-message"><%= item.getMessage() %></p>
                <p class="hint notification-time"><%= item.getCreatedAtDisplay() %></p>
            </li>
            <% } %>
        </ul>
        <% } %>
    </section>

    <% if (applications == null || applications.isEmpty()) { %>
    <section class="card">
        <p class="hint">你还没有提交任何申请。</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/home">去浏览职位</a>
    </section>
    <% } else { %>
    <div class="application-status-list">
        <% for (TaApplicationOverview app : applications) { %>
        <section class="card application-status-card">
            <div class="application-status-head">
                <div>
                    <h2 class="application-job-title"><%= app.getJobTitle() == null ? "" : app.getJobTitle() %></h2>
                    <p class="hint application-job-meta">申请编号：<code><%= app.getApplicationId() == null ? "" : app.getApplicationId() %></code></p>
                </div>
                <div class="application-status-badges">
                    <span class="<%= app.getStatusPillClass() == null ? "status-pill tag-neutral" : app.getStatusPillClass() %>">
                        <%= app.getStatusLabel() == null ? "" : app.getStatusLabel() %>
                    </span>
                </div>
            </div>
            <div class="application-last-updated">
                <span class="label">最近更新时间</span>
                <span class="value"><%= app.getLastUpdatedDisplay() == null || app.getLastUpdatedDisplay().isEmpty() ? "—" : app.getLastUpdatedDisplay() %></span>
            </div>
            <h3 class="section-title timeline-title">状态时间线</h3>
            <ol class="timeline">
                <%
                    List<TaTimelineStep> steps = app.getTimelineSteps();
                    if (steps != null) {
                        for (TaTimelineStep step : steps) {
                %>
                <li class="timeline-item">
                    <div class="timeline-marker" aria-hidden="true"></div>
                    <div class="timeline-body">
                        <div class="timeline-row">
                            <span class="<%= step.getPillClass() == null ? "status-pill tag-neutral" : step.getPillClass() %>">
                                <%= step.getTitle() == null ? "" : step.getTitle() %>
                            </span>
                            <time class="timeline-time"><%= step.getOccurredAtDisplay() == null ? "" : step.getOccurredAtDisplay() %></time>
                        </div>
                        <% if (step.getDetail() != null && !step.getDetail().isEmpty()) { %>
                        <p class="timeline-detail"><%= step.getDetail() %></p>
                        <% } %>
                    </div>
                </li>
                <%      }
                    }
                %>
            </ol>
        </section>
        <% } %>
    </div>
    <% } %>
</main>
</body>
</html>
