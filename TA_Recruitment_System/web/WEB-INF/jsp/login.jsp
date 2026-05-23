<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录/注册 - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<main class="auth-shell">
    <section
            class="auth-card auth-card-user"
            id="userAuthCard"
            data-initial-role="${empty selectedRole ? 'TA' : selectedRole}"
            data-initial-mode="${empty selectedMode ? 'LOGIN' : selectedMode}"
            data-ta-username="${empty taPresetUsername ? 'ta001' : taPresetUsername}"
            data-ta-password="${empty taPresetPassword ? 'ta123456' : taPresetPassword}"
            data-mo-username="${empty moPresetUsername ? 'mo001' : moPresetUsername}"
            data-mo-password="${empty moPresetPassword ? 'mo123456' : moPresetPassword}">
        <a class="admin-entry" href="${pageContext.request.contextPath}/login?view=admin">管理员入口</a>

        <div class="auth-accent"></div>
        <h1 class="auth-title-fixed">登录/注册</h1>

        <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
        <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

        <form method="post" action="${pageContext.request.contextPath}/login" class="auth-form" id="userAuthForm">
            <input type="hidden" name="portal" value="user">
            <input type="hidden" name="role" id="userRoleInput" value="${empty selectedRole ? 'TA' : selectedRole}">
            <input type="hidden" name="actionType" id="userActionTypeInput" value="${empty selectedMode ? 'LOGIN' : selectedMode}">

            <div class="choice-group choice-group-soft" aria-label="身份">
                <button type="button" class="choice-chip choice-chip-role" data-role-option="TA">我是TA</button>
                <button type="button" class="choice-chip choice-chip-role" data-role-option="MO">我是MO</button>
            </div>

            <div class="choice-group choice-group-simple" aria-label="模式">
                <button type="button" class="choice-chip choice-chip-mode" data-mode-option="LOGIN">登录</button>
                <button type="button" class="choice-chip choice-chip-mode" data-mode-option="REGISTER">注册</button>
            </div>

            <div class="auth-fields">
                <div class="auth-field ${selectedMode eq 'REGISTER' ? '' : 'hidden'}" data-register-only="true">
                    <label class="sr-only" for="displayName">姓名</label>
                    <input id="displayName" name="displayName" type="text" placeholder="姓名" value="${displayName}">
                </div>

                <div class="auth-field ${selectedMode eq 'REGISTER' ? '' : 'hidden'}" data-register-only="true">
                    <label class="sr-only" for="userId" id="userIdLabel">编号</label>
                    <input id="userId" name="userId" type="text" placeholder="学号" value="${userId}">
                </div>

                <div class="auth-field">
                    <label class="sr-only" for="username">账号</label>
                    <input id="username" name="username" type="text" placeholder="账号" value="${username}" required>
                </div>

                <div class="auth-field">
                    <label class="sr-only" for="password">密码</label>
                    <input id="password" name="password" type="password" placeholder="密码" required>
                </div>

                <div class="auth-field ${selectedMode eq 'REGISTER' ? '' : 'hidden'}" data-register-only="true">
                    <label class="sr-only" for="confirmPassword">确认密码</label>
                    <input id="confirmPassword" name="confirmPassword" type="password" placeholder="确认密码">
                </div>
            </div>

            <button type="submit" class="auth-submit auth-submit-main" id="userSubmitButton">
                ${selectedMode eq 'REGISTER' ? '注册' : '登录'}
            </button>
        </form>

        <section class="preset-panel">
            <details class="preset-card preset-card-current" id="userPresetCard">
                <summary id="userPresetSummary">TA 预设账号</summary>
                <div class="preset-body">
                    <p>账号：<code id="userPresetUsername">${empty taPresetUsername ? 'ta001' : taPresetUsername}</code></p>
                    <p>密码：<code id="userPresetPassword">${empty taPresetPassword ? 'ta123456' : taPresetPassword}</code></p>
                </div>
            </details>
        </section>
    </section>
</main>

<script>
    (function () {
        var card = document.getElementById("userAuthCard");
        if (!card) {
            return;
        }

        var roleInput = document.getElementById("userRoleInput");
        var actionTypeInput = document.getElementById("userActionTypeInput");
        var submitButton = document.getElementById("userSubmitButton");
        var userIdInput = document.getElementById("userId");
        var userIdLabel = document.getElementById("userIdLabel");
        var registerFields = card.querySelectorAll("[data-register-only='true']");
        var roleButtons = card.querySelectorAll("[data-role-option]");
        var modeButtons = card.querySelectorAll("[data-mode-option]");
        var presetSummary = document.getElementById("userPresetSummary");
        var presetUsername = document.getElementById("userPresetUsername");
        var presetPassword = document.getElementById("userPresetPassword");
        var currentRole = card.getAttribute("data-initial-role") || "TA";
        var currentMode = card.getAttribute("data-initial-mode") || "LOGIN";

        function readPreset(role, field) {
            var key = "data-" + role.toLowerCase() + "-" + field;
            return card.getAttribute(key) || "";
        }

        function syncPreset(role) {
            var username = readPreset(role, "username");
            var password = readPreset(role, "password");

            presetSummary.textContent = role === "TA" ? "TA 预设账号" : "MO 预设账号";
            presetUsername.textContent = username || (role === "TA" ? "ta001" : "mo001");
            presetPassword.textContent = password || (role === "TA" ? "ta123456" : "mo123456");
        }

        function setRole(role) {
            currentRole = role === "MO" ? "MO" : "TA";
            roleInput.value = currentRole;
            userIdInput.placeholder = currentRole === "TA" ? "学号" : "工号";
            userIdLabel.textContent = currentRole === "TA" ? "学号" : "工号";
            syncPreset(currentRole);

            roleButtons.forEach(function (button) {
                var active = button.getAttribute("data-role-option") === currentRole;
                button.classList.toggle("is-active", active);
                button.setAttribute("aria-pressed", active ? "true" : "false");
            });
        }

        function setMode(mode) {
            currentMode = mode === "REGISTER" ? "REGISTER" : "LOGIN";
            actionTypeInput.value = currentMode;

            var isRegister = currentMode === "REGISTER";
            submitButton.textContent = isRegister ? "注册" : "登录";

            registerFields.forEach(function (field) {
                field.classList.toggle("hidden", !isRegister);
                field.querySelectorAll("input").forEach(function (input) {
                    input.required = isRegister;
                });
            });

            modeButtons.forEach(function (button) {
                var active = button.getAttribute("data-mode-option") === currentMode;
                button.classList.toggle("is-active", active);
                button.setAttribute("aria-pressed", active ? "true" : "false");
            });
        }

        roleButtons.forEach(function (button) {
            button.addEventListener("click", function () {
                setRole(button.getAttribute("data-role-option"));
            });
        });

        modeButtons.forEach(function (button) {
            button.addEventListener("click", function () {
                setMode(button.getAttribute("data-mode-option"));
            });
        });

        setRole(currentRole);
        setMode(currentMode);
    })();
</script>
</body>
</html>
