<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.LoginUser" %>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    String displayName = loginUser == null || loginUser.getDisplayName() == null ? "Administrator" : loginUser.getDisplayName();

    Object totalTas = request.getAttribute("adminTotalTAs");
    Object totalJobs = request.getAttribute("adminTotalJobs");
    Object activeApplications = request.getAttribute("adminActiveApplications");
    Object overloadedTas = request.getAttribute("adminOverloadedTAs");
    Object acceptedApplications = request.getAttribute("adminAcceptedApplications");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page role-page">
<main class="container wide role-home-shell role-page-shell">
    <header class="role-hero">
        <div>
            <span class="role-eyebrow">Administrator Console</span>
            <h1>Admin Dashboard</h1>
            <p class="hint">Welcome back, <strong><%= displayName %></strong>. Monitor recruitment activity and workload risk from one place.</p>
        </div>
        <div class="role-hero-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/workload">Workload</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="secondary-btn">Logout</button>
            </form>
        </div>
    </header>

    <section class="review-summary-grid role-home-summary role-stat-grid">
        <div class="summary-card">
            <span class="label">Total TAs</span>
            <span class="value"><%= totalTas == null ? 0 : totalTas %></span>
            <span class="trend">Profiles in TA data</span>
        </div>
        <div class="summary-card">
            <span class="label">Total Jobs</span>
            <span class="value"><%= totalJobs == null ? 0 : totalJobs %></span>
            <span class="trend">Open and historical posts</span>
        </div>
        <div class="summary-card">
            <span class="label">Active Applications</span>
            <span class="value"><%= activeApplications == null ? 0 : activeApplications %></span>
            <span class="trend">Submitted, in review or shortlisted</span>
        </div>
        <div class="summary-card">
            <span class="label">Overloaded TAs</span>
            <span class="value"><%= overloadedTas == null ? 0 : overloadedTas %></span>
            <span class="trend">Warnings from workload rules</span>
        </div>
    </section>

    <section class="role-dashboard-layout">
        <section class="card role-home-card">
            <h2 class="section-title">System Management</h2>
            <div class="role-card-list">
                <div class="role-action-card">
                    <div>
                        <strong>TA workload dashboard</strong>
                        <p>Review accepted assignments, weekly hours, conflicts and overload warnings.</p>
                    </div>
                    <a class="link-btn" href="${pageContext.request.contextPath}/admin/workload">Open Workload</a>
                </div>
                <div class="role-action-card">
                    <div>
                        <strong>Recruitment visibility</strong>
                        <p>User, job and application data continues to use the existing JSON-backed flow.</p>
                    </div>
                    <span class="status-pill tag-info">Read-only summary</span>
                </div>
            </div>
        </section>

        <aside class="card role-home-card workload-summary-panel">
            <h2 class="section-title">Workload Summary</h2>
            <div class="workload-summary-row">
                <span>Accepted assignments</span>
                <strong><%= acceptedApplications == null ? 0 : acceptedApplications %></strong>
            </div>
            <div class="workload-summary-row">
                <span>Warning threshold</span>
                <strong>20 hours/week</strong>
            </div>
            <div class="workload-summary-row">
                <span>Risk state</span>
                <strong><%= overloadedTas == null || String.valueOf(overloadedTas).equals("0") ? "Normal" : "Needs review" %></strong>
            </div>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/workload">View detailed workload table</a>
        </aside>
    </section>
</main>
</body>
</html>
