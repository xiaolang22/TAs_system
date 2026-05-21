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
        <p class="hint">Current CV: <strong id="currentCvName">${empty cvFilename ? 'No CV uploaded.' : cvFilename}</strong></p>
        <form id="saveCvForm" method="post" action="${pageContext.request.contextPath}/ta/upload-cv" enctype="multipart/form-data" class="profile-form">
            <input id="uploadStudentId" type="hidden" name="studentId" value="${profile.studentId}">
            <label for="cvFile">Choose resume file *</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>
        </form>
        <div class="row action-row">
            <button type="button" id="saveCvBtn">Save Resume</button>
            <button type="button" id="parseCvBtn" class="secondary-btn">Auto-Fill Fields</button>
        </div>
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
    const saveCvForm = document.getElementById('saveCvForm');
    const parseBtn = document.getElementById('parseCvBtn');
    const saveCvBtn = document.getElementById('saveCvBtn');
    const parseStatus = document.getElementById('parseStatus');
    const contextPath = '${pageContext.request.contextPath}';
    const fileInput = document.getElementById('cvFile');
    const studentIdInput = document.getElementById('studentId');
    const uploadStudentIdInput = document.getElementById('uploadStudentId');
    const currentCvName = document.getElementById('currentCvName');

    saveCvBtn.addEventListener('click', async function() {
        if (!fileInput.files || fileInput.files.length === 0) {
            showStatus('Please select a resume file before saving.', 'error');
            return;
        }
        if (uploadStudentIdInput && studentIdInput) {
            uploadStudentIdInput.value = studentIdInput.value;
        }
        if (!uploadStudentIdInput.value.trim()) {
            showStatus('Please enter the Student ID before saving the resume.', 'error');
            return;
        }

        saveCvBtn.disabled = true;
        parseBtn.disabled = true;
        showStatus('Saving resume, please wait...', 'info');

        const formData = new FormData(saveCvForm);

        try {
            formData.append('ajax', 'true');
            const response = await fetch(saveCvForm.action, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData
            });

            const responseText = await response.text();
            if (!response.ok) {
                console.error('Save resume request failed:', response.status, responseText);
            }
            let result = null;
            try {
                result = JSON.parse(responseText);
            } catch (parseError) {
                console.error('Unexpected save response:', responseText);
                showStatus('Failed to save resume: server returned an unexpected response.', 'error');
                return;
            }

            if (result.success && result.data && result.data.profile && result.data.profile.cvFilePath) {
                currentCvName.textContent = extractFileName(result.data.profile.cvFilePath);
                fileInput.value = '';
                showStatus(result.message || 'Resume saved successfully.', 'success');
            } else {
                showStatus(result.message || 'Failed to save resume.', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showStatus('An error occurred while saving the resume.', 'error');
        } finally {
            saveCvBtn.disabled = false;
            parseBtn.disabled = false;
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

    function extractFileName(filePath) {
        if (!filePath) {
            return 'No CV uploaded.';
        }
        const normalized = String(filePath).replace(/\\/g, '/');
        const lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }
});
</script>
</body>
</html>
