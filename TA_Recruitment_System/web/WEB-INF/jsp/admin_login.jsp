<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Sign In - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<main class="auth-shell">
    <section class="auth-card auth-card-admin">
        <a class="admin-entry" href="${pageContext.request.contextPath}/login">Back to User Access</a>

        <section class="auth-brand auth-brand-compact">
            <div class="auth-brand-mark" aria-hidden="true">
                <svg viewBox="0 0 180 180" role="img">
                    <defs>
                        <linearGradient id="brandBlueAdmin" x1="0%" y1="0%" x2="100%" y2="100%">
                            <stop offset="0%" stop-color="#0f2e8c"/>
                            <stop offset="55%" stop-color="#1663d6"/>
                            <stop offset="100%" stop-color="#2ab8ff"/>
                        </linearGradient>
                    </defs>
                    <circle cx="32" cy="90" r="12" fill="#1663d6"/>
                    <circle cx="148" cy="90" r="12" fill="#27aef5"/>
                    <circle cx="32" cy="130" r="12" fill="#f5a623"/>
                    <circle cx="148" cy="130" r="12" fill="#27aef5"/>
                    <circle cx="90" cy="154" r="12" fill="#2c78ff"/>
                    <path d="M90 28 L145 52 L125 63 C115 58 103 55 90 55 C77 55 65 58 55 63 L35 52 Z" fill="url(#brandBlueAdmin)"/>
                    <path d="M131 56 L131 88" stroke="#1663d6" stroke-width="6" stroke-linecap="round"/>
                    <circle cx="131" cy="92" r="7" fill="#1663d6"/>
                    <path d="M131 97 L125 114 H137 Z" fill="#1663d6"/>
                    <path d="M90 62 C122 62 148 88 148 120 C148 133 144 144 137 154 L137 170 L121 161 C112 167 101 170 90 170 C58 170 32 144 32 112 C32 80 58 62 90 62 Z" fill="#ffffff" stroke="url(#brandBlueAdmin)" stroke-width="8" stroke-linejoin="round"/>
                    <circle cx="90" cy="104" r="18" fill="url(#brandBlueAdmin)"/>
                    <path d="M62 145 C67 129 79 121 90 121 C101 121 113 129 118 145 Z" fill="url(#brandBlueAdmin)"/>
                </svg>
            </div>
            <div class="auth-brand-text">
                <div class="auth-brand-word">TAHub</div>
            </div>
        </section>

        <div class="auth-accent"></div>
        <h1 class="auth-title-fixed">Admin Sign In</h1>

        <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

        <form method="post" action="${pageContext.request.contextPath}/login" class="auth-form auth-form-simple">
            <input type="hidden" name="portal" value="admin">

            <div class="auth-fields">
                <div class="auth-field">
                    <label class="sr-only" for="adminUsername">Username</label>
                    <input id="adminUsername" name="username" type="text" placeholder="Username" value="${username}" required>
                </div>

                <div class="auth-field">
                    <label class="sr-only" for="adminPassword">Password</label>
                    <input id="adminPassword" name="password" type="password" placeholder="Password" required>
                </div>
            </div>

            <button type="submit" class="auth-submit auth-submit-main">Sign In</button>
        </form>

        <section class="preset-panel preset-panel-single">
            <details class="preset-card">
                <summary>Admin Preset Credentials</summary>
                <div class="preset-body">
                    <p>Username: <code>${empty adminPresetUsername ? 'admin001' : adminPresetUsername}</code></p>
                    <p>Password: <code>${empty adminPresetPassword ? 'admin123456' : adminPresetPassword}</code></p>
                </div>
            </details>
        </section>
    </section>
</main>
</body>
</html>
