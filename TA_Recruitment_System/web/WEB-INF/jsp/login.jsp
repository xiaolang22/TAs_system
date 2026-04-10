<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container login-container">
    <h1>User Login & Identity Recognition</h1>
    <p class="hint">US00: sign in with your TA or MO test account.</p>

    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <form method="post" action="${pageContext.request.contextPath}/login" class="profile-form">
        <label for="username">Account</label>
        <input id="username" name="username" type="text" value="${username}" required>

        <label for="password">Password</label>
        <input id="password" name="password" type="password" required>

        <button type="submit">Login</button>
    </form>

    <section class="tips">
        <h2>Built-in test accounts</h2>
        <p><code>ta001 / ta123456</code> (role: TA)</p>
        <p><code>mo001 / mo123456</code> (role: MO)</p>
    </section>
</main>
</body>
</html>
