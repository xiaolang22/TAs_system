<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.dto.MoTaCandidateCard" %>
<%@ page import="com.group19.model.LoginUser" %>
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
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    MoTaCandidateCard candidate = (MoTaCandidateCard) request.getAttribute("candidate");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA Profile - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>TA Profile</h1>
            <p class="hint">The profile is shown first. If a resume exists in the system, you can open it as well.</p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${backUrl}">${backLabel}</a>
            <% if (candidate != null && candidate.hasCv()) { %>
            <a class="link-btn" href="<%= attr(candidate.getCvUrl()) %>" target="_blank" rel="noopener">View Resume</a>
            <% } %>
        </div>
    </header>

    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <% if (candidate != null) { %>
    <section class="card detail-card">
        <div class="ta-profile-hero">
            <div class="ta-profile-avatar <%= candidate.getAvatarUrl() == null || candidate.getAvatarUrl().isBlank() ? "ta-avatar-fallback" : "" %>">
                <% if (candidate.getAvatarUrl() != null && !candidate.getAvatarUrl().isBlank()) { %>
                <img src="<%= attr(candidate.getAvatarUrl()) %>" alt="TA avatar">
                <% } else { %>
                <span><%= attr(candidate.getAvatarInitial()) %></span>
                <% } %>
            </div>
            <div class="ta-profile-hero-copy">
                <h2><%= attr(candidate.getDisplayName()) %></h2>
                <div class="review-badges">
                    <span class="status-pill tag-info">Programme: <%= attr(candidate.getProgrammeDisplay()) %></span>
                    <span class="status-pill <%= candidate.hasCv() ? "tag-good" : (candidate.isProfileCompleted() ? "tag-warning" : "tag-neutral") %>">
                        <%= attr(candidate.getArchiveStatusLabel()) %>
                    </span>
                    <span class="status-pill tag-neutral">Student ID: <%= attr(candidate.getStudentId()) %></span>
                </div>
            </div>
        </div>
    </section>

    <section class="detail-layout">
        <section class="card">
            <h2 class="section-title">Profile Details</h2>
            <div class="detail-grid">
                <div class="detail-item">
                    <span class="label">Username</span>
                    <div class="value"><%= attr(candidate.getUsername()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">Email</span>
                    <div class="value"><%= attr(candidate.getEmailDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">Skills</span>
                    <div class="value"><%= attr(candidate.getSkillsDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">Availability</span>
                    <div class="value"><%= attr(candidate.getAvailabilityDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">Experience</span>
                    <div class="value"><%= attr(candidate.getExperienceDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">Last Updated</span>
                    <div class="value"><%= attr(candidate.getUpdatedAtDisplay()) %></div>
                </div>
            </div>
        </section>

        <aside class="detail-stack">
            <section class="card">
                <h2 class="section-title">Profile Status</h2>
                <div class="detail-item">
                    <span class="label">Personal Profile</span>
                    <div class="value"><%= candidate.isProfileCompleted() ? "Completed" : "Incomplete" %></div>
                </div>
                <div class="detail-item detail-note">
                    <span class="label">Resume File</span>
                    <div class="value"><%= candidate.hasCv() ? "Uploaded and available" : "Not uploaded or file not found" %></div>
                </div>
            </section>
        </aside>
    </section>
    <% } %>
</main>
</body>
</html>
