<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人中心 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>个人中心</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}${homePath}">返回首页</a>
        </div>
    </header>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section class="card account-center-card">
        <div class="account-center-avatar-block">
            <form method="post" action="${pageContext.request.contextPath}${accountPath}" enctype="multipart/form-data" class="account-avatar-form">
                <input type="hidden" name="action" value="avatar">
                <div class="account-avatar-shell">
                    <div class="account-avatar-large ${empty avatarPreviewUrl ? 'account-avatar-fallback' : ''}">
                        <img class="${empty avatarPreviewUrl ? 'hidden' : ''}" src="${avatarPreviewUrl}" alt="用户头像">
                        <span class="${empty avatarPreviewUrl ? '' : 'hidden'}">${avatarInitial}</span>
                    </div>
                    <label class="account-avatar-edit" for="avatarFile">编辑</label>
                    <input id="avatarFile" name="avatarFile" type="file" accept=".png,.jpg,.jpeg,.gif,.webp" class="sr-only" onchange="if (this.files && this.files.length) { this.form.submit(); }">
                </div>
            </form>
            <div class="hint">当前身份：${roleLabel}</div>
        </div>

        <div class="account-center-forms">
            <form method="post" action="${pageContext.request.contextPath}${accountPath}" class="profile-form account-section-form">
                <input type="hidden" name="action" value="profile">
                <h2>个人信息</h2>

                <label for="displayName">姓名</label>
                <input id="displayName" name="displayName" type="text" value="${account.displayName}" required>

                <label for="username">账号</label>
                <input id="username" name="username" type="text" value="${account.username}" required>

                <label for="userId">学号</label>
                <input id="userId" name="userId" type="text" value="${account.userId}" readonly>

                <button type="submit">修改个人信息</button>
            </form>

            <form method="post" action="${pageContext.request.contextPath}${accountPath}" class="profile-form account-section-form">
                <input type="hidden" name="action" value="password">
                <h2>修改密码</h2>

                <label for="newPassword">新密码</label>
                <input id="newPassword" name="newPassword" type="password" placeholder="请输入新密码" required>

                <label for="confirmPassword">确认新密码</label>
                <input id="confirmPassword" name="confirmPassword" type="password" placeholder="请再次输入新密码" required>

                <button type="submit">修改密码</button>
            </form>
        </div>
    </section>
</main>
</body>
</html>
