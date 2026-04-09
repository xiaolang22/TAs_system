<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <h1>Create / Edit Applicant Profile</h1>
            <p class="hint">Signed in as <strong>${loginUser.role}</strong> (<code>${loginUser.username}</code>)</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="secondary-btn">Logout</button>
            </form>
        </div>
    </header>
    <p class="hint">US01: enter and update your profile information.</p>

    <form method="get" action="${pageContext.request.contextPath}/profile" class="lookup-form">
        <label for="lookupStudentId">Open existing profile by Student ID</label>
        <div class="row">
            <input id="lookupStudentId" name="studentId" type="text" placeholder="e.g. 231221618">
            <button type="submit">Open</button>
        </div>
    </form>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form">
        <label for="name">Name *</label>
        <input id="name" name="name" type="text" value="${profile.name}" required>

        <label for="studentId">Student ID *</label>
        <input id="studentId" name="studentId" type="text" value="${profile.studentId}" required>

        <label for="email">Email *</label>
        <input id="email" name="email" type="email" value="${profile.email}" required>

        <label for="programme">Programme *</label>
        <input id="programme" name="programme" type="text" value="${profile.programme}" required>

        <label for="skills">Skills *</label>
        <textarea id="skills" name="skills" rows="4" required>${profile.skills}</textarea>

        <label for="availability">Availability *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">Save Profile</button>
    </form>
</main>
</body>
</html>
