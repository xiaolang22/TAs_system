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
    <h1>Create / Edit Applicant Profile</h1>
    <p class="hint">US01 + US03: maintain profile and auto-fill from CV extraction.</p>

    <form method="get" action="${pageContext.request.contextPath}/profile" class="lookup-form">
        <label for="lookupStudentId">Open existing profile by Student ID</label>
        <div class="row">
            <input id="lookupStudentId" name="studentId" type="text" placeholder="e.g. 231221618">
            <button type="submit">Open</button>
        </div>
    </form>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert info ${empty info ? 'hidden' : ''}">${info}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section class="panel">
        <h2>US03 Auto-Fill From CV</h2>
        <p class="hint">Upload CV file (PDF/DOC/DOCX/TXT) or paste CV text. System extracts education, skills and experience.</p>
        <form method="post" action="${pageContext.request.contextPath}/upload-cv" enctype="multipart/form-data" class="profile-form">
            <label for="cvStudentId">Student ID (recommended)</label>
            <input id="cvStudentId" name="studentId" type="text" value="${profile.studentId}" placeholder="Use ID to merge with existing profile">

            <label for="cvFile">Upload CV File</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx,.txt">

            <label for="cvText">Or Paste CV Text</label>
            <textarea id="cvText" name="cvText" rows="6" placeholder="Paste plain text CV here for more accurate extraction"></textarea>

            <button type="submit">Extract And Pre-Fill</button>
        </form>
        <p class="hint ${empty profile.cvFileName ? 'hidden' : ''}">Latest uploaded CV: ${profile.cvFileName}</p>
        <pre class="preview ${empty extractedText ? 'hidden' : ''}">${extractedText}</pre>
    </section>

    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form">
        <h2>Editable Profile Form</h2>
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

        <label for="experience">Experience (from CV extraction)</label>
        <textarea id="experience" name="experience" rows="4">${profile.experience}</textarea>

        <label for="availability">Availability *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">Save Profile</button>
    </form>
</main>
</body>
</html>
