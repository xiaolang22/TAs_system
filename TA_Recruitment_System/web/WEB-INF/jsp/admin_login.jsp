<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员登录 - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<main class="auth-shell">
    <section class="auth-card auth-card-admin">
        <a class="admin-entry" href="${pageContext.request.contextPath}/login">返回用户入口</a>

        <div class="auth-accent"></div>
        <h1 class="auth-title-fixed">管理员登录</h1>

        <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

        <form method="post" action="${pageContext.request.contextPath}/login" class="auth-form auth-form-simple">
            <input type="hidden" name="portal" value="admin">

            <div class="auth-fields">
                <div class="auth-field">
                    <label class="sr-only" for="adminUsername">账号</label>
                    <input id="adminUsername" name="username" type="text" placeholder="账号" value="${username}" required>
                </div>

                <div class="auth-field">
                    <label class="sr-only" for="adminPassword">密码</label>
                    <input id="adminPassword" name="password" type="password" placeholder="密码" required>
                </div>
            </div>

            <button type="submit" class="auth-submit auth-submit-main">登录</button>
        </form>

        <section class="preset-panel preset-panel-single">
            <details class="preset-card">
                <summary>管理员预设账号</summary>
                <div class="preset-body">
                    <p>账号：<code>${empty adminPresetUsername ? 'admin001' : adminPresetUsername}</code></p>
                    <p>密码：<code>${empty adminPresetPassword ? 'admin123456' : adminPresetPassword}</code></p>
                </div>
            </details>
        </section>
    </section>
</main>
</body>
</html>
