<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <h1>Welcome, ${loginUser.displayName}</h1>
            <p class="hint">
                Logged in as <strong>${loginUser.role}</strong>
                (<code>${loginUser.username}</code>)
            </p>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit" class="secondary-btn">Logout</button>
        </form>
    </header>

    <section class="card ${loginUser.role == 'TA' ? '' : 'hidden'}">
        <h2>TA Workspace</h2>
        <p>You can continue to create or edit your profile information.</p>
        <a class="link-btn" href="${pageContext.request.contextPath}/profile">
            Go to TA Profile
        </a>
    </section>

    <section class="card ${loginUser.role == 'MO' ? '' : 'hidden'}">
        <h2>MO Workspace</h2>
        <p>Your role has been identified as MO. MO-specific functions can be added here next.</p>
    </section>
</main>
</body>
</html>
