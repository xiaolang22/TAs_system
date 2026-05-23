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
            <p class="hint">在这里更新姓名、账号、密码与头像。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">返回首页</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/profile">申请资料</a>
        </div>
    </header>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section class="card account-center-card">
        <div class="account-center-avatar-block">
            <div class="account-avatar-large ${empty avatarPreviewUrl ? 'account-avatar-fallback' : ''}">
                <img class="${empty avatarPreviewUrl ? 'hidden' : ''}" src="${avatarPreviewUrl}" alt="用户头像">
                <span class="${empty avatarPreviewUrl ? '' : 'hidden'}">${avatarInitial}</span>
            </div>
            <div class="hint">当前身份：TA</div>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/ta/account" enctype="multipart/form-data" class="profile-form">
            <label for="displayName">姓名</label>
            <input id="displayName" name="displayName" type="text" value="${account.displayName}" required>

            <label for="username">账号</label>
            <input id="username" name="username" type="text" value="${account.username}" required>

            <label for="userId">学号 / 编号</label>
            <input id="userId" name="userId" type="text" value="${account.userId}" readonly>

            <label for="avatarFile">头像图片</label>
            <input id="avatarFile" name="avatarFile" type="file" accept=".png,.jpg,.jpeg,.gif,.webp">

            <label for="newPassword">新密码</label>
            <input id="newPassword" name="newPassword" type="password" placeholder="不修改可留空">

            <label for="confirmPassword">确认新密码</label>
            <input id="confirmPassword" name="confirmPassword" type="password" placeholder="再次输入新密码">

            <button type="submit">保存个人信息</button>
        </form>
    </section>
</main>
</body>
</html>
