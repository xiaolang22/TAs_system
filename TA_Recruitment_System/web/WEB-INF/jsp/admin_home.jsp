<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.LoginUser" %>
<%
    LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
    String displayName = loginUser == null || loginUser.getDisplayName() == null ? "管理员" : loginUser.getDisplayName();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员首页 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide role-home-shell">
    <header class="page-header">
        <div>
            <h1>管理员首页</h1>
            <p class="hint">欢迎，<%= displayName %>。从这里进入管理员子页面。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </div>
    </header>

    <section class="review-summary-grid role-home-summary">
        <div class="summary-card">
            <span class="label">当前身份</span>
            <span class="value">ADMIN</span>
        </div>
        <div class="summary-card">
            <span class="label">首页入口</span>
            <span class="value">/admin/home</span>
        </div>
        <div class="summary-card">
            <span class="label">可用模块</span>
            <span class="value">1</span>
        </div>
    </section>

    <section class="card role-home-card">
        <h2 class="section-title">功能入口</h2>
        <div class="role-home-actions">
            <a class="link-btn" href="${pageContext.request.contextPath}/admin/workload">工作量看板</a>
        </div>
    </section>
</main>
</body>
</html>
