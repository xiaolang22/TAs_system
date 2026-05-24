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
    boolean showMatchDetails = Boolean.TRUE.equals(request.getAttribute("moShowMatchDetails"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO Home - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<div class="ta-app-shell">
    <header class="ta-topbar">
        <a class="ta-brand-link" href="${pageContext.request.contextPath}/mo/home" aria-label="Back to MO Home">
            <span class="ta-brand-logo" aria-hidden="true">
                <img src="${pageContext.request.contextPath}/assets/logo_1.jpg" alt="TA Recruitment System Logo">
            </span>
        </a>

        <div class="ta-topbar-actions">
            <div class="ta-welcome-chip">Welcome! <%= attr(displayName) %></div>

            <a class="ta-avatar-entry" href="${pageContext.request.contextPath}/mo/account" aria-label="Open account center">
                <span class="ta-avatar <%= avatarUrl.isEmpty() ? "ta-avatar-fallback" : "" %>">
                    <% if (!avatarUrl.isEmpty()) { %>
                    <img src="<%= attr(avatarUrl) %>" alt="User avatar">
                    <% } else { %>
                    <span><%= firstChar(displayName, "M") %></span>
                    <% } %>
                </span>
            </a>

            <a class="ta-message-btn" href="#moNotificationCenter" aria-label="View New Application Notifications">
                <span class="ta-message-icon" aria-hidden="true">&#128276;</span>
                <span>New Applications</span>
                <% if (unreadCount > 0) { %>
                <span class="ta-notification-dot"></span>
                <% } %>
            </a>

            <form method="post" action="${pageContext.request.contextPath}/logout" class="ta-logout-form">
                <button type="submit" class="ta-logout-btn">Sign Out</button>
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
                            <h1>TA Directory</h1>
                        </div>
                    </div>

                    <form method="get" action="${pageContext.request.contextPath}/mo/home" class="ta-filter-form ta-filter-inline-form">
                        <div class="ta-filter-grid">
                            <div class="ta-filter-field">
                                <label for="keyword">Keyword</label>
                                <input id="keyword" name="keyword" type="text" value="${moFilterKeyword}" placeholder="Name, student ID, or skills">
                            </div>
                            <div class="ta-filter-field">
                                <label for="programme">Programme</label>
                                <input id="programme" name="programme" type="text" value="${moFilterProgramme}" placeholder="For example: Computer Science">
                            </div>
                            <div class="ta-filter-field">
                                <label for="availability">Availability</label>
                                <input id="availability" name="availability" type="text" value="${moFilterAvailability}" placeholder="For example: Wednesday afternoon / Monday">
                            </div>
                        </div>
                        <div class="ta-filter-actions">
                            <button type="submit">Filter</button>
                            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">Reset</a>
                        </div>
                    </form>
                </section>

                <div class="ta-job-summary">
                    Currently showing <strong><%= filteredCandidateCount %></strong> / <strong><%= totalCandidateCount %></strong> TAs.
                    <span>Current mode: Keyword Filter</span>
                </div>

                <div class="ta-job-list-wrap">
                    <div class="ta-job-list">
                        <% if (moCandidates != null && !moCandidates.isEmpty()) {
                            for (MoTaCandidateCard candidate : moCandidates) { %>
                        <article class="ta-job-card">
                            <div class="ta-job-card-head ta-job-card-head-inline">
                                <h2><%= attr(candidate.getDisplayName()) %></h2>
                                <p class="ta-job-badges">
                                    <span class="status-pill tag-info">Programme: <%= attr(candidate.getProgrammeDisplay()) %></span>
                                    <span class="status-pill <%= candidate.hasCv() ? "tag-good" : (candidate.isProfileCompleted() ? "tag-warning" : "tag-neutral") %>"><%= attr(candidate.getArchiveStatusLabel()) %></span>
                                </p>
                            </div>
                            <dl class="ta-job-meta-grid">
                                <div>
                                    <dt>Skills</dt>
                                    <dd><%= attr(candidate.getSkillsDisplay()) %></dd>
                                </div>
                                <div>
                                    <dt>Availability</dt>
                                    <dd><%= attr(candidate.getAvailabilityDisplay()) %></dd>
                                </div>
                            </dl>
                            <% if (showMatchDetails) { %>
                            <div class="ta-card-footnote">
                                <strong>Match score:</strong> <%= candidate.getMatchScore() %>%&nbsp;
                                <strong>Notes:</strong> <%= attr(candidate.getMatchNote()) %>
                            </div>
                            <% } %>
                            <div class="ta-job-actions">
                                <a class="link-btn" href="${pageContext.request.contextPath}/mo/ta-profile?studentId=<%= attr(candidate.getStudentId()) %>">View Profile / Resume</a>
                            </div>
                        </article>
                        <% }
                        } else { %>
                        <div class="empty-state">
                            No TAs match the current filters.
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            <button type="button" class="ta-scroll-top-btn" id="moScrollTopBtn" aria-label="Back to the top of the TA board">Back to Top</button>
        </section>

        <aside class="ta-dashboard-pane">
            <section class="ta-dashboard-card">
                <h2>Dashboard</h2>
                <div class="ta-side-stat-list">
                    <div class="ta-side-stat-item">Jobs I Manage <strong><%= ownedJobCount %></strong></div>
                    <div class="ta-side-stat-item">Total Jobs in System <strong><%= allJobCount %></strong></div>
                    <div class="ta-side-stat-item">Unread New Applications <strong><%= unreadCount %></strong></div>
                </div>
                <div class="ta-quick-links">
                    <a class="link-btn" href="${pageContext.request.contextPath}/mo/post-job">Post Job</a>
                    <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">Job List</a>
                </div>
            </section>

            <section class="ta-dashboard-card" id="moNotificationCenter">
                <h2>New Application Notifications</h2>
                <% if (moNotifications == null || moNotifications.isEmpty()) { %>
                <p class="hint">There are no new application notifications for the jobs you manage.</p>
                <% } else { %>
                <ul class="ta-mini-list is-scrollable">
                    <% for (MoNotificationView item : moNotifications) { %>
                    <li class="ta-mini-list-item <%= item.isUnread() ? "is-unread" : "" %>">
                        <p><%= item.getMessage() %></p>
                        <span><%= item.getCreatedAtDisplay() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= item.getActionUrl() %>">View Related Application</a>
                        </div>
                    </li>
                    <% } %>
                </ul>
                <% } %>
            </section>

            <section class="ta-dashboard-card">
                <h2>Job Deadline Reminders</h2>
                <% if (deadlineReminders == null || deadlineReminders.isEmpty()) { %>
                <p class="hint">There are no upcoming deadlines for the jobs you manage in the next 14 days.</p>
                <% } else { %>
                <ul class="ta-mini-list is-scrollable">
                    <% for (DeadlineReminderView reminder : deadlineReminders) { %>
                    <li class="ta-mini-list-item">
                        <p><%= reminder.getJobTitle() %></p>
                        <span><%= reminder.getDeadlineDisplay() %> | <%= reminder.getDaysLabel() %></span>
                        <div class="role-home-inline-link">
                            <a href="<%= reminder.getActionUrl() %>">View Job Applications</a>
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
