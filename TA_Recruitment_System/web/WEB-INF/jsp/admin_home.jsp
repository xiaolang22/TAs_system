<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.dto.AdminFeedItem" %>
<%@ page import="com.group19.model.Job" %>
<%@ page import="com.group19.model.LoginUser" %>
<%@ page import="com.group19.model.UserAccount" %>
<%!
    private static final String TA_PANEL = "admin-ta-panel";
    private static final String MO_PANEL = "admin-mo-panel";
    private static final String JOB_PANEL = "admin-job-panel";

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
            return "Open";
        }
        return "CLOSED".equalsIgnoreCase(job.getStatus()) ? "Closed" : "Open";
    }

    private String adminUrl(String contextPath, String taId, String moId, String jobId, String focusSection) {
        StringBuilder builder = new StringBuilder(contextPath).append("/admin/home");
        boolean hasQuery = false;
        hasQuery = appendParam(builder, hasQuery, "selectedTaId", taId);
        hasQuery = appendParam(builder, hasQuery, "selectedMoId", moId);
        hasQuery = appendParam(builder, hasQuery, "selectedJobId", jobId);
        if (focusSection != null && !focusSection.isEmpty()) {
            hasQuery = appendParam(builder, hasQuery, "focusSection", focusSection);
            builder.append('#').append(focusSection);
        }
        return builder.toString();
    }

    private boolean appendParam(StringBuilder builder, boolean hasQuery, String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            return hasQuery;
        }
        builder.append(hasQuery ? '&' : '?')
                .append(name)
                .append('=')
                .append(value.trim());
        return true;
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
    String displayName = loginUser == null ? "Administrator" : loginUser.getDisplayName();
    String avatarPath = loginUser == null ? "" : loginUser.getAvatarPath();
    String avatarUrl = avatarPath == null || avatarPath.isBlank() ? "" : request.getContextPath() + avatarPath;
    String successMsg = (String) request.getAttribute("successMsg");
    String errorMsg = (String) request.getAttribute("errorMsg");
    String selectedTaId = selectedTa == null ? null : selectedTa.getUserId();
    String selectedMoId = selectedMo == null ? null : selectedMo.getUserId();
    String selectedJobId = selectedJob == null ? null : selectedJob.getJobId();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Home - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<div class="ta-app-shell admin-app-shell">
    <header class="ta-topbar">
        <a class="ta-brand-link" href="${pageContext.request.contextPath}/admin/home" aria-label="Back to admin home">
            <span class="ta-brand-logo" aria-hidden="true">
                <img src="${pageContext.request.contextPath}/assets/logo_1.jpg" alt="TA Recruitment System Logo">
            </span>
        </a>

        <div class="ta-topbar-actions">
            <div class="ta-welcome-chip">Welcome! <%= attr(displayName) %></div>

            <a class="ta-avatar-entry" href="${pageContext.request.contextPath}/admin/account" aria-label="Open account center">
                <span class="ta-avatar <%= avatarUrl.isEmpty() ? "ta-avatar-fallback" : "" %>">
                    <% if (!avatarUrl.isEmpty()) { %>
                    <img src="<%= attr(avatarUrl) %>" alt="User avatar">
                    <% } else { %>
                    <span><%= firstChar(displayName, "A") %></span>
                    <% } %>
                </span>
            </a>

            <form method="post" action="${pageContext.request.contextPath}/logout" class="ta-logout-form">
                <button type="submit" class="ta-logout-btn">Sign Out</button>
            </form>
        </div>
    </header>

    <main class="admin-home-layout">
        <section class="admin-workbench-pane">
            <div class="ta-alert-stack">
                <p class="alert success <%= successMsg == null || successMsg.isBlank() ? "hidden" : "" %>"><%= successMsg == null ? "" : attr(successMsg) %></p>
                <p class="alert error <%= errorMsg == null || errorMsg.isBlank() ? "hidden" : "" %>"><%= errorMsg == null ? "" : attr(errorMsg) %></p>
            </div>

            <section class="admin-panel-card" id="<%= TA_PANEL %>">
                <div class="admin-panel-head">
                    <div>
                        <h2>TA Management</h2>
                        <span><%= taAccounts.size() %> TA accounts</span>
                    </div>
                </div>

                <% if (selectedTa != null) { %>
                <section class="admin-detail-card">
                    <div class="admin-detail-head">
                        <strong><%= attr(selectedTa.getDisplayName()) %></strong>
                        <span class="status-pill <%= selectedTa.isFrozen() ? "tag-alert" : "tag-good" %>">
                            <%= selectedTa.isFrozen() ? "Frozen" : "Active" %>
                        </span>
                    </div>
                    <div class="admin-detail-grid">
                        <div><span class="label">Username</span><span class="value"><%= attr(selectedTa.getUsername()) %></span></div>
                        <div><span class="label">Student ID</span><span class="value"><%= attr(selectedTa.getUserId()) %></span></div>
                    </div>
                    <div class="admin-inline-actions">
                        <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), null, selectedMoId, selectedJobId, TA_PANEL) %>">Hide Details</a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="resetTaPassword">
                            <input type="hidden" name="userId" value="<%= attr(selectedTa.getUserId()) %>">
                            <input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>">
                            <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <input type="hidden" name="focusSection" value="<%= TA_PANEL %>">
                            <button type="submit" class="secondary-btn">Reset Password</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="toggleTaFreeze">
                            <input type="hidden" name="userId" value="<%= attr(selectedTa.getUserId()) %>">
                            <input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>">
                            <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <input type="hidden" name="focusSection" value="<%= TA_PANEL %>">
                            <button type="submit" class="secondary-btn"><%= selectedTa.isFrozen() ? "Unfreeze Account" : "Freeze Account" %></button>
                        </form>
                    </div>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (UserAccount account : taAccounts) { %>
                    <article class="admin-list-item <%= selectedTa != null && account.getUserId().equalsIgnoreCase(selectedTa.getUserId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(account.getDisplayName()) %></strong>
                            <span><%= attr(account.getUsername()) %> | <%= account.isFrozen() ? "Frozen" : "Active" %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), account.getUserId(), selectedMoId, selectedJobId, TA_PANEL) %>">View Details</a>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>

            <section class="admin-panel-card" id="<%= MO_PANEL %>">
                <div class="admin-panel-head">
                    <div>
                        <h2>MO Management</h2>
                        <span><%= moAccounts.size() %> MO accounts</span>
                    </div>
                </div>

                <% if (selectedMo != null) { %>
                <section class="admin-detail-card">
                    <div class="admin-detail-head">
                        <strong><%= attr(selectedMo.getDisplayName()) %></strong>
                        <span class="status-pill <%= selectedMo.isFrozen() ? "tag-alert" : "tag-good" %>">
                            <%= selectedMo.isFrozen() ? "Frozen" : "Active" %>
                        </span>
                    </div>
                    <div class="admin-detail-grid">
                        <div><span class="label">Username</span><span class="value"><%= attr(selectedMo.getUsername()) %></span></div>
                        <div><span class="label">MO ID</span><span class="value"><%= attr(selectedMo.getUserId()) %></span></div>
                    </div>
                    <div class="admin-inline-actions">
                        <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), selectedTaId, null, selectedJobId, MO_PANEL) %>">Hide Details</a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="resetMoPassword">
                            <input type="hidden" name="userId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                            <input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <input type="hidden" name="focusSection" value="<%= MO_PANEL %>">
                            <button type="submit" class="secondary-btn">Reset Password</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin/home">
                            <input type="hidden" name="action" value="toggleMoFreeze">
                            <input type="hidden" name="userId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                            <input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>">
                            <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                            <input type="hidden" name="focusSection" value="<%= MO_PANEL %>">
                            <button type="submit" class="secondary-btn"><%= selectedMo.isFrozen() ? "Unfreeze Account" : "Freeze Account" %></button>
                        </form>
                    </div>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (UserAccount account : moAccounts) { %>
                    <article class="admin-list-item <%= selectedMo != null && account.getUserId().equalsIgnoreCase(selectedMo.getUserId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(account.getDisplayName()) %></strong>
                            <span><%= attr(account.getUsername()) %> | <%= account.isFrozen() ? "Frozen" : "Active" %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), selectedTaId, account.getUserId(), selectedJobId, MO_PANEL) %>">View Details</a>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>

            <section class="admin-panel-card" id="<%= JOB_PANEL %>">
                <div class="admin-panel-head">
                    <div>
                        <h2>Job Management</h2>
                        <span><%= jobs.size() %> jobs</span>
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
                    <div class="admin-inline-actions">
                        <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), selectedTaId, selectedMoId, null, JOB_PANEL) %>">Hide Details</a>
                        <a class="link-btn" href="${pageContext.request.contextPath}/admin/recommendations?jobId=<%= attr(selectedJob.getJobId()) %>">View TA Recommendations</a>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/admin/home" class="admin-edit-form">
                        <input type="hidden" name="action" value="updateJob">
                        <input type="hidden" name="jobId" value="<%= attr(selectedJob.getJobId()) %>">
                        <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                        <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                        <input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>">
                        <input type="hidden" name="focusSection" value="<%= JOB_PANEL %>">

                        <label for="adminJobTitle">Job Title</label>
                        <input id="adminJobTitle" name="title" type="text" value="<%= attr(selectedJob.getTitle()) %>" required>

                        <div class="admin-edit-grid">
                            <div>
                                <label for="adminJobHours">Workload</label>
                                <input id="adminJobHours" name="hours" type="text" value="<%= attr(selectedJob.getHours()) %>" required>
                            </div>
                            <div>
                                <label for="adminJobDeadline">Deadline</label>
                                <input id="adminJobDeadline" name="deadline" type="date" value="<%= attr(selectedJob.getDeadline()) %>" required>
                            </div>
                            <div>
                                <label for="adminJobStatus">Status</label>
                                <select id="adminJobStatus" name="status">
                                    <option value="OPEN" <%= "CLOSED".equalsIgnoreCase(selectedJob.getStatus()) ? "" : "selected" %>>OPEN</option>
                                    <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(selectedJob.getStatus()) ? "selected" : "" %>>CLOSED</option>
                                </select>
                            </div>
                        </div>

                        <label for="adminJobSchedule">Schedule</label>
                        <input id="adminJobSchedule" name="schedule" type="text" value="<%= attr(selectedJob.getSchedule()) %>" required>

                        <label for="adminJobDescription">Description</label>
                        <textarea id="adminJobDescription" name="description" rows="3" required><%= attr(selectedJob.getDescription()) %></textarea>

                        <label for="adminJobRequirements">Requirements</label>
                        <textarea id="adminJobRequirements" name="requirements" rows="3" required><%= attr(selectedJob.getRequirements()) %></textarea>

                        <div class="admin-inline-actions">
                            <button type="submit">Save Changes</button>
                        </div>
                    </form>
                </section>
                <% } %>

                <div class="admin-scroll-list">
                    <% for (Job job : jobs) { %>
                    <article class="admin-list-item <%= selectedJob != null && job.getJobId().equalsIgnoreCase(selectedJob.getJobId()) ? "is-selected" : "" %>">
                        <div class="admin-list-copy">
                            <strong><%= attr(job.getTitle()) %></strong>
                            <span><%= attr(job.getJobId()) %> | <%= jobStatusLabel(job) %></span>
                        </div>
                        <div class="admin-list-actions">
                            <a class="link-btn secondary" href="<%= adminUrl(request.getContextPath(), selectedTaId, selectedMoId, job.getJobId(), JOB_PANEL) %>">View Details</a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/home" onsubmit="return confirm('Delete this job?');">
                                <input type="hidden" name="action" value="deleteJob">
                                <input type="hidden" name="jobId" value="<%= attr(job.getJobId()) %>">
                                <% if (selectedTa != null) { %><input type="hidden" name="selectedTaId" value="<%= attr(selectedTa.getUserId()) %>"><% } %>
                                <% if (selectedMo != null) { %><input type="hidden" name="selectedMoId" value="<%= attr(selectedMo.getUserId()) %>"><% } %>
                                <% if (selectedJob != null) { %><input type="hidden" name="selectedJobId" value="<%= attr(selectedJob.getJobId()) %>"><% } %>
                                <input type="hidden" name="focusSection" value="<%= JOB_PANEL %>">
                                <button type="submit" class="secondary-btn">Delete Job</button>
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
                        <h2>Monitoring</h2>
                        <span>Global overview</span>
                    </div>
                </div>

                <div class="admin-stat-grid">
                    <div class="summary-card">
                        <span class="label">Open jobs</span>
                        <span class="value">${openJobCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Closed jobs</span>
                        <span class="value">${closedJobCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Total TAs</span>
                        <span class="value">${taCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Total MOs</span>
                        <span class="value">${moCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Pending applications</span>
                        <span class="value">${pendingApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Accepted</span>
                        <span class="value">${acceptedApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Rejected</span>
                        <span class="value">${rejectedApplicationCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Workload warnings</span>
                        <span class="value">${workloadWarningCount}</span>
                    </div>
                    <div class="summary-card">
                        <span class="label">Time conflicts</span>
                        <span class="value">${timeConflictCount}</span>
                    </div>
                </div>
                <div class="admin-inline-actions">
                    <a class="link-btn" href="${pageContext.request.contextPath}/admin/recommendations">TA Recommendations</a>
                </div>
            </section>

            <section class="admin-panel-card admin-feed-card">
                <div class="admin-panel-head">
                    <div>
                        <h2>Recently Added Jobs</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentJobs.isEmpty()) { %>
                    <div class="empty-state">No data available.</div>
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
                        <h2>Recent Applications</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentApplications.isEmpty()) { %>
                    <div class="empty-state">No data available.</div>
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
                        <h2>Recent Alerts</h2>
                    </div>
                </div>
                <div class="admin-feed-list">
                    <% if (recentAlerts.isEmpty()) { %>
                    <div class="empty-state">No alerts.</div>
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
