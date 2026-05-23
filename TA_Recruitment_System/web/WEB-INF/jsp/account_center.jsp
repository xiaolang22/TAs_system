<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Center - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>Account Center</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}${homePath}">Back to Home</a>
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
                        <img class="${empty avatarPreviewUrl ? 'hidden' : ''}" src="${avatarPreviewUrl}" alt="User avatar">
                        <span class="${empty avatarPreviewUrl ? '' : 'hidden'}">${avatarInitial}</span>
                    </div>
                    <label class="account-avatar-edit" for="avatarFile">Edit</label>
                    <input id="avatarFile" name="avatarFile" type="file" accept=".png,.jpg,.jpeg,.gif,.webp" class="sr-only" onchange="if (this.files && this.files.length) { this.form.submit(); }">
                </div>
            </form>
            <div class="hint">Current role: ${roleLabel}</div>
        </div>

        <div class="account-center-forms">
            <form method="post" action="${pageContext.request.contextPath}${accountPath}" class="profile-form account-section-form">
                <input type="hidden" name="action" value="profile">
                <h2>Profile Information</h2>

                <label for="displayName">Name</label>
                <input id="displayName" name="displayName" type="text" value="${account.displayName}" required>

                <label for="username">Username</label>
                <input id="username" name="username" type="text" value="${account.username}" required>

                <label for="userId">Student ID</label>
                <input id="userId" name="userId" type="text" value="${account.userId}" readonly>

                <button type="submit">Update Profile</button>
            </form>

            <form method="post" action="${pageContext.request.contextPath}${accountPath}" class="profile-form account-section-form">
                <input type="hidden" name="action" value="password">
                <h2>Change Password</h2>

                <label for="newPassword">New Password</label>
                <input id="newPassword" name="newPassword" type="password" placeholder="Enter a new password" required>

                <label for="confirmPassword">Confirm New Password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" placeholder="Re-enter the new password" required>

                <button type="submit">Change Password</button>
            </form>
        </div>
    </section>
</main>
</body>
</html>
