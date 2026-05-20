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

    <section class="card">
        <h2>Resume Upload & Auto-Fill</h2>
        <p class="hint">Upload a PDF or DOC/DOCX file. The latest uploaded resume replaces the previous one.</p>
        <p class="hint">Auto-Fill uses the latest saved resume for the current Student ID to extract education, skills and experience.</p>
        <p class="hint">Current CV: <strong>${empty cvFilename ? 'No CV uploaded.' : cvFilename}</strong></p>
        <form id="cvActionsForm" method="post" action="${pageContext.request.contextPath}/ta/upload-cv" enctype="multipart/form-data" class="profile-form">
            <input id="uploadStudentId" type="hidden" name="studentId" value="${profile.studentId}">
            <label for="cvFile">Choose resume file *</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>
            <div class="row action-row">
                <button type="submit" id="saveCvBtn">Save Resume</button>
                <button type="button" id="parseCvBtn" class="secondary-btn">Auto-Fill Fields</button>
            </div>
        </form>
        <p class="alert info hidden" id="parseStatus"></p>
    </section>

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

        <label for="experience">Experience (auto-filled from CV)</label>
        <textarea id="experience" name="experience" rows="4">${profile.experience}</textarea>

        <label for="availability">Availability *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">Save Profile</button>
    </form>
</main>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const cvActionsForm = document.getElementById('cvActionsForm');
    const parseBtn = document.getElementById('parseCvBtn');
    const saveCvBtn = document.getElementById('saveCvBtn');
    const parseStatus = document.getElementById('parseStatus');
    const contextPath = '${pageContext.request.contextPath}';
    const fileInput = document.getElementById('cvFile');
    const studentIdInput = document.getElementById('studentId');
    const uploadStudentIdInput = document.getElementById('uploadStudentId');

    cvActionsForm.addEventListener('submit', function() {
        if (uploadStudentIdInput && studentIdInput) {
            uploadStudentIdInput.value = studentIdInput.value;
        }
    });

    parseBtn.addEventListener('click', async function() {
        const studentIdValue = studentIdInput ? studentIdInput.value.trim() : '';
        if (!studentIdValue) {
            showStatus('Please enter the Student ID before using auto-fill.', 'error');
            return;
        }

        parseBtn.disabled = true;
        saveCvBtn.disabled = true;
        parseBtn.textContent = 'Parsing...';
        showStatus('Parsing the latest saved resume, please wait...', 'info');

        const formData = new FormData();
        formData.append('studentId', studentIdValue);

        try {
            const response = await fetch(contextPath + '/ta/parse-cv', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (result.success && result.data) {
                const data = result.data;
                fillForm(data);
                showStatus('Resume parsed successfully. Review and edit the fields below before saving the profile.', 'success');
            } else {
                showStatus(result.message || 'Failed to parse CV.', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showStatus('An error occurred while parsing the saved resume.', 'error');
        } finally {
            parseBtn.disabled = false;
            saveCvBtn.disabled = false;
            parseBtn.textContent = 'Auto-Fill Fields';
        }
    });

    function fillForm(data) {
        if (data.programme) {
            const programmeInput = document.getElementById('programme');
            if (programmeInput) {
                programmeInput.value = data.programme;
            }
        }
        if (data.skills) {
            const skillsInput = document.getElementById('skills');
            if (skillsInput) {
                skillsInput.value = data.skills;
            }
        }
        if (data.experience) {
            const experienceInput = document.getElementById('experience');
            if (experienceInput) {
                experienceInput.value = data.experience;
            }
        }
    }

    function showStatus(message, type) {
        parseStatus.textContent = message;
        parseStatus.className = 'alert ' + type;
        parseStatus.classList.remove('hidden');
    }
});
</script>
</body>
</html>
