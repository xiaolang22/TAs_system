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
    String displayName = loginUser == null || loginUser.getDisplayName() == null ? "Module Organiser" : loginUser.getDisplayName();

    Object postedJobs = request.getAttribute("moPostedJobsCount");
    Object pendingApplications = request.getAttribute("moPendingApplicationsCount");
    Object shortlistedCandidates = request.getAttribute("moShortlistedCandidatesCount");
    Object closedJobs = request.getAttribute("moClosedJobsCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO Dashboard - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide role-home-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">Module Organiser Workspace</span>
            <h1>MO Dashboard</h1>
            <p class="hint">Welcome back, <strong><%= displayName %></strong>. Manage posts, review applicants and keep the recruitment flow moving.</p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">Job list</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="secondary-btn">Logout</button>
            </form>
        </div>
    </header>

    <section class="review-summary-grid role-home-summary role-stat-grid">
        <div class="summary-card">
            <span class="label">Posted Jobs</span>
            <span class="value"><%= postedJobs == null ? 0 : postedJobs %></span>
            <span class="trend">All recruitment posts</span>
        </div>
        <div class="summary-card">
            <span class="label">Pending Applications</span>
            <span class="value"><%= pendingApplications == null ? 0 : pendingApplications %></span>
            <span class="trend">Submitted or in review</span>
        </div>
        <div class="summary-card">
            <span class="label">Shortlisted Candidates</span>
            <span class="value"><%= shortlistedCandidates == null ? 0 : shortlistedCandidates %></span>
            <span class="trend">Ready for final decision</span>
        </div>
        <div class="summary-card">
            <span class="label">Closed Jobs</span>
            <span class="value"><%= closedJobs == null ? 0 : closedJobs %></span>
            <span class="trend">Closed or past deadline</span>
        </div>
    </section>

    <section class="role-dashboard-layout">
        <div class="detail-stack">
            <section class="card role-home-card">
                <h2 class="section-title">Primary Actions</h2>
                <div class="role-card-list">
                    <div class="role-action-card">
                        <div>
                            <strong>Post a new TA position</strong>
                            <p>Create a recruitment post with requirements, workload and deadline.</p>
                        </div>
                        <a class="link-btn" href="${pageContext.request.contextPath}/mo/post-job">Post Job</a>
                    </div>
                    <div class="role-action-card">
                        <div>
                            <strong>Review applicants by job</strong>
                            <p>Open a job and make shortlist, accept or reject decisions.</p>
                        </div>
                        <a class="link-btn" href="${pageContext.request.contextPath}/mo/jobs">View Jobs</a>
                    </div>
                    <div class="role-action-card">
                        <div>
                            <strong>Check candidate skill match</strong>
                            <p>Compare applicant skills against the requirement keywords.</p>
                        </div>
                        <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/review">Skill Review</a>
                    </div>
                </div>
            </section>

            <section class="card role-home-card">
                <h2 class="section-title">New Application Notifications</h2>
                <% if (moNotifications == null || moNotifications.isEmpty()) { %>
                <div class="empty-state">No new application notifications.</div>
                <% } else { %>
                <ul class="ta-mini-list">
                    <% for (MoNotificationView item : moNotifications) { %>
                    <li class="ta-mini-list-item <%= item.isUnread() ? "is-unread" : "" %>">
                        <p><%= item.getMessage() %></p>
                        <span><%= item.getCreatedAtDisplay() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= item.getActionUrl() %>">Open application</a>
                        </div>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>
        </div>

        <aside class="detail-stack">
            <section class="card role-home-card">
                <h2 class="section-title">Inbox Snapshot</h2>
                <div class="review-summary-grid">
                    <div class="summary-card">
                        <span class="label">Unread</span>
                        <span class="value"><%= unreadCount %></span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Preview</span>
                        <span class="value"><%= moNotifications == null ? 0 : moNotifications.size() %></span>
                    </div>
                </div>
            </section>

            <section class="card role-home-card">
                <h2 class="section-title">Deadline Reminders</h2>
                <% if (deadlineReminders == null || deadlineReminders.isEmpty()) { %>
                <div class="empty-state">No upcoming job deadline reminders.</div>
                <% } else { %>
                <ul class="ta-mini-list">
                    <% for (DeadlineReminderView reminder : deadlineReminders) { %>
                    <li class="ta-mini-list-item">
                        <p><%= reminder.getJobTitle() %></p>
                        <span><%= reminder.getDeadlineDisplay() %> - <%= reminder.getDaysLabel() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= reminder.getActionUrl() %>">Open job</a>
                        </div>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>
        </aside>
    </section>
</main>
</body>
</html>
