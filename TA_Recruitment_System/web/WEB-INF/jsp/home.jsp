<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.group19.model.Job" %>
<%@ page import="com.group19.dto.TaNotificationView" %>
<%@ page import="com.group19.dto.MoNotificationView" %>
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
    List<Job> savedJobs = (List<Job>) request.getAttribute("savedJobs");
    List<TaNotificationView> taNotifications = (List<TaNotificationView>) request.getAttribute("taNotifications");
    Integer unreadNotificationCount = (Integer) request.getAttribute("unreadNotificationCount");
    int unreadCount = unreadNotificationCount == null ? 0 : unreadNotificationCount;
    List<MoNotificationView> moNotifications = (List<MoNotificationView>) request.getAttribute("moNotifications");
    Integer moUnreadNotificationCount = (Integer) request.getAttribute("moUnreadNotificationCount");
    int moUnreadCount = moUnreadNotificationCount == null ? 0 : moUnreadNotificationCount;
    String currentRequestPath = (String) request.getAttribute("currentRequestPath");
    if (currentRequestPath == null || currentRequestPath.isBlank()) {
        currentRequestPath = request.getContextPath() + "/home";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <h1>Welcome, ${loginUser.displayName}</h1>
            <p class="hint">
                Logged in as <strong>${loginUser.role}</strong>
                (<code>${loginUser.username}</code>)
            </p>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit" class="secondary-btn">Logout</button>
        </form>
    </header>

    <p class="alert success ${empty param.applySuccess ? 'hidden' : ''}">
        Application submitted successfully!
    </p>

    <p class="alert error ${empty error ? 'hidden' : ''}">
        ${error}
    </p>

    <p class="alert success ${empty savedJobMessage ? 'hidden' : ''}">
        ${savedJobMessage}
    </p>

    <p class="alert error ${empty savedJobError ? 'hidden' : ''}">
        ${savedJobError}
    </p>

    <section class="card notification-panel ${loginUser.role == 'TA' ? '' : 'hidden'}">
        <h2>Application notifications<% if (unreadCount > 0) { %> <span class="notification-badge"><%= unreadCount %> unread</span><% } %></h2>
        <% if (taNotifications == null || taNotifications.isEmpty()) { %>
        <p class="hint">No status updates yet. When a module officer changes your application status, you will see a message here.</p>
        <% } else { %>
        <ul class="notification-list">
            <% for (TaNotificationView item : taNotifications) { %>
            <li class="notification-item <%= item.isUnread() ? "notification-unread" : "" %>">
                <p class="notification-message"><%= item.getMessage() %></p>
                <p class="hint notification-time"><%= item.getCreatedAtDisplay() %></p>
            </li>
            <% } %>
        </ul>
        <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/applications">
            View all applications
        </a>
        <% } %>
    </section>

    <section class="card ${loginUser.role == 'TA' ? '' : 'hidden'}">
        <h2>TA Workspace</h2>
        <p>You can continue to create or edit your profile information.</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/profile">
            Go to TA Profile
        </a>
        <a class="link-btn" href="${pageContext.request.contextPath}/jobs">
            Browse Available Jobs
        </a>
        <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/applications">
            My application status
        </a>
    </section>

    <section class="card saved-jobs-panel ${loginUser.role == 'TA' ? '' : 'hidden'}">
        <h2>Saved positions</h2>
        <% if (savedJobs == null || savedJobs.isEmpty()) { %>
        <p class="hint">No saved positions yet.</p>
        <% } else { %>
        <div class="saved-job-list">
            <% for (Job job : savedJobs) { %>
            <article class="saved-job-item">
                <div>
                    <h3><%= job.getTitle() == null ? "" : job.getTitle() %></h3>
                    <p class="hint">
                        <%= job.getCategory() == null ? "" : job.getCategory() %>
                        <span aria-hidden="true"> &middot; </span>
                        Deadline: <%= job.getDeadline() == null ? "" : job.getDeadline() %>
                    </p>
                </div>
                <div class="saved-job-actions">
                    <a class="link-btn" href="${pageContext.request.contextPath}/jobs?jobId=<%= job.getJobId() %>">
                        View details &amp; apply
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/ta/saved-jobs" class="save-job-form">
                        <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                        <input type="hidden" name="action" value="remove">
                        <input type="hidden" name="returnTo" value="<%= attr(currentRequestPath) %>">
                        <button type="submit" class="secondary-btn save-toggle saved">Remove saved</button>
                    </form>
                </div>
            </article>
            <% } %>
        </div>
        <% } %>
    </section>

    <section class="card notification-panel ${loginUser.role == 'MO' ? '' : 'hidden'}">
        <h2>New application notifications<% if (moUnreadCount > 0) { %> <span class="notification-badge"><%= moUnreadCount %> unread</span><% } %></h2>
        <% if (moNotifications == null || moNotifications.isEmpty()) { %>
        <p class="hint">No new applications yet. When a TA submits an application, you will see a message here.</p>
        <% } else { %>
        <ul class="notification-list">
            <% for (MoNotificationView item : moNotifications) { %>
            <li class="notification-item <%= item.isUnread() ? "notification-unread" : "" %>">
                <p class="notification-message"><%= item.getMessage() %></p>
                <p class="hint notification-time"><%= item.getCreatedAtDisplay() %></p>
                <a class="link-btn secondary" href="<%= item.getActionUrl() %>">Review applicants</a>
            </li>
            <% } %>
        </ul>
        <% } %>
    </section>

    <section class="card ${loginUser.role == 'MO' ? '' : 'hidden'}">
        <h2>MO Workspace</h2>
        <p>Review applicants with skill match score and missing-skill notes (US10).</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/mo/jobs">
            View posted jobs
        </a>
        <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/review">
            Go to Candidate Review
        </a>
    </section>

    <section class="card ${loginUser.role == 'ADMIN' ? '' : 'hidden'}">
        <h2>Admin Workspace</h2>
        <p>Monitor TA workload summaries and assigned hours across positions.</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/admin/workload">
            View TA Workload
        </a>
    </section>
</main>
</body>
</html>
