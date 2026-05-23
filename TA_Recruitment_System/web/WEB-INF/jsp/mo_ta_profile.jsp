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
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA 档案 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>TA 档案</h1>
            <p class="hint">默认展示该 TA 的个人档案；若系统中存在已上传简历，可继续查看简历。</p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/home">返回首页</a>
            <% if (candidate != null && candidate.hasCv()) { %>
            <a class="link-btn" href="<%= attr(candidate.getCvUrl()) %>" target="_blank" rel="noopener">查看简历</a>
            <% } %>
        </div>
    </header>

    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <% if (candidate != null) { %>
    <section class="card detail-card">
        <div class="ta-profile-hero">
            <div class="ta-profile-avatar <%= candidate.getAvatarUrl() == null || candidate.getAvatarUrl().isBlank() ? "ta-avatar-fallback" : "" %>">
                <% if (candidate.getAvatarUrl() != null && !candidate.getAvatarUrl().isBlank()) { %>
                <img src="<%= attr(candidate.getAvatarUrl()) %>" alt="TA 头像">
                <% } else { %>
                <span><%= attr(candidate.getAvatarInitial()) %></span>
                <% } %>
            </div>
            <div class="ta-profile-hero-copy">
                <h2><%= attr(candidate.getDisplayName()) %></h2>
                <div class="review-badges">
                    <span class="status-pill tag-info">专业：<%= attr(candidate.getProgrammeDisplay()) %></span>
                    <span class="status-pill <%= candidate.hasCv() ? "tag-good" : (candidate.isProfileCompleted() ? "tag-warning" : "tag-neutral") %>">
                        <%= attr(candidate.getArchiveStatusLabel()) %>
                    </span>
                    <span class="status-pill tag-neutral">学号：<%= attr(candidate.getStudentId()) %></span>
                </div>
            </div>
        </div>
    </section>

    <section class="detail-layout">
        <section class="card">
            <h2 class="section-title">档案信息</h2>
            <div class="detail-grid">
                <div class="detail-item">
                    <span class="label">账号</span>
                    <div class="value"><%= attr(candidate.getUsername()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">邮箱</span>
                    <div class="value"><%= attr(candidate.getEmailDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">技能信息</span>
                    <div class="value"><%= attr(candidate.getSkillsDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">可工作时间</span>
                    <div class="value"><%= attr(candidate.getAvailabilityDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">经历</span>
                    <div class="value"><%= attr(candidate.getExperienceDisplay()) %></div>
                </div>
                <div class="detail-item">
                    <span class="label">最近更新</span>
                    <div class="value"><%= attr(candidate.getUpdatedAtDisplay()) %></div>
                </div>
            </div>
        </section>

        <aside class="detail-stack">
            <section class="card">
                <h2 class="section-title">档案状态</h2>
                <div class="detail-item">
                    <span class="label">个人档案</span>
                    <div class="value"><%= candidate.isProfileCompleted() ? "已完善" : "未完善" %></div>
                </div>
                <div class="detail-item detail-note">
                    <span class="label">简历文件</span>
                    <div class="value"><%= candidate.hasCv() ? "已上传并可查看" : "未上传或未找到文件" %></div>
                </div>
            </section>
        </aside>
    </section>
    <% } %>
</main>
</body>
</html>
