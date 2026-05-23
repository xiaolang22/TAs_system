<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile and Resume - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>Profile and Resume</h1>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
        </div>
    </header>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section class="card">
        <h2>Resume Upload and Auto-Fill</h2>
        <p class="hint">Upload a PDF, DOC, or DOCX resume, then auto-fill your profile.</p>
        <p class="hint">Current resume: <strong id="currentCvName">${empty cvFilename ? 'No resume uploaded yet' : cvFilename}</strong></p>
        <form id="saveCvForm" method="post" action="${pageContext.request.contextPath}/ta/upload-cv" enctype="multipart/form-data" class="profile-form">
            <input id="uploadStudentId" type="hidden" name="studentId" value="${profile.studentId}">
            <label for="cvFile">Resume file *</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>
        </form>
        <div class="row action-row">
            <button type="button" id="saveCvBtn">Save Resume</button>
            <button type="button" id="parseCvBtn" class="secondary-btn">Auto-Fill</button>
        </div>
        <p class="alert info hidden" id="parseStatus"></p>
    </section>

    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form">
        <label for="name">Name *</label>
        <input id="name" name="name" type="text" value="${profile.name}" required>

        <label for="studentId">Student ID *</label>
        <input id="studentId" name="studentId" type="text" value="${profile.studentId}" readonly>

        <label for="email">Email *</label>
        <input id="email" name="email" type="email" value="${profile.email}" required>

        <label for="programme">Programme *</label>
        <input id="programme" name="programme" type="text" value="${profile.programme}" required>

        <label for="skills">Skills *</label>
        <textarea id="skills" name="skills" rows="4" required>${profile.skills}</textarea>

        <label for="experience">Experience</label>
        <textarea id="experience" name="experience" rows="4">${profile.experience}</textarea>

        <label for="availability">Availability *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">Save Profile</button>
    </form>
</main>

<script>
document.addEventListener('DOMContentLoaded', function () {
    const saveCvForm = document.getElementById('saveCvForm');
    const parseBtn = document.getElementById('parseCvBtn');
    const saveCvBtn = document.getElementById('saveCvBtn');
    const parseStatus = document.getElementById('parseStatus');
    const contextPath = '${pageContext.request.contextPath}';
    const fileInput = document.getElementById('cvFile');
    const uploadStudentIdInput = document.getElementById('uploadStudentId');
    const currentCvName = document.getElementById('currentCvName');

    saveCvBtn.addEventListener('click', async function () {
        if (!fileInput.files || fileInput.files.length === 0) {
            showStatus('Please choose a resume file first.', 'error');
            return;
        }
        if (!uploadStudentIdInput.value.trim()) {
            showStatus('The current account is missing a student ID, so upload is unavailable.', 'error');
            return;
        }

        saveCvBtn.disabled = true;
        parseBtn.disabled = true;
        showStatus('Saving the resume. Please wait...', 'info');

        const formData = new FormData(saveCvForm);
        formData.set('studentId', uploadStudentIdInput.value.trim());
        formData.append('ajax', 'true');

        try {
            const response = await fetch(saveCvForm.action, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData
            });
            const responseText = await response.text();
            let result = null;

            try {
                result = JSON.parse(responseText);
            } catch (parseError) {
                console.error('Unexpected save response:', responseText);
                showStatus('Resume save failed because the server response could not be parsed.', 'error');
                return;
            }

            if (result.success && result.data && result.data.profile && result.data.profile.cvFilePath) {
                currentCvName.textContent = extractFileName(result.data.profile.cvFilePath);
                fileInput.value = '';
                showStatus(result.message || 'Resume saved successfully.', 'success');
            } else {
                showStatus(result.message || 'Failed to save the resume.', 'error');
            }
        } catch (error) {
            console.error(error);
            showStatus('An unexpected error occurred while saving the resume.', 'error');
        } finally {
            saveCvBtn.disabled = false;
            parseBtn.disabled = false;
        }
    });

    parseBtn.addEventListener('click', async function () {
        if (!uploadStudentIdInput.value.trim()) {
            showStatus('The current account is missing a student ID, so auto-fill is unavailable.', 'error');
            return;
        }

        parseBtn.disabled = true;
        saveCvBtn.disabled = true;
        parseBtn.textContent = 'Parsing...';
        showStatus('Parsing the saved resume. Please wait...', 'info');

        const formData = new FormData();
        formData.append('studentId', uploadStudentIdInput.value.trim());

        try {
            const response = await fetch(contextPath + '/ta/parse-cv', {
                method: 'POST',
                body: formData
            });
            const result = await response.json();

            if (result.success && result.data) {
                fillForm(result.data);
                showStatus(result.message || 'Auto-fill completed. Please review and save your profile.', 'success');
            } else {
                showStatus(result.message || 'Auto-fill failed.', 'error');
            }
        } catch (error) {
            console.error(error);
            showStatus('An unexpected error occurred while parsing the resume.', 'error');
        } finally {
            parseBtn.disabled = false;
            saveCvBtn.disabled = false;
            parseBtn.textContent = 'Auto-Fill';
        }
    });

    function fillForm(data) {
        const programmeInput = document.getElementById('programme');
        const skillsInput = document.getElementById('skills');
        const experienceInput = document.getElementById('experience');

        if (programmeInput && data.programme) {
            programmeInput.value = data.programme;
        }
        if (skillsInput && data.skills) {
            skillsInput.value = data.skills;
        }
        if (experienceInput && data.experience) {
            experienceInput.value = data.experience;
        }
    }

    function showStatus(message, type) {
        parseStatus.textContent = message;
        parseStatus.className = 'alert ' + type;
        parseStatus.classList.remove('hidden');
    }

    function extractFileName(filePath) {
        if (!filePath) {
            return 'No resume uploaded yet';
        }
        const normalized = String(filePath).replace(/\\/g, '/');
        const lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }
});
</script>
</body>
</html>
