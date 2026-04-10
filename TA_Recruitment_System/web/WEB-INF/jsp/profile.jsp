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
        <h2>Parse CV to Auto-Fill Profile</h2>
        <p class="hint">US03: upload a CV file to auto-fill your profile. You can manually edit the results.</p>
        <form id="parseCvForm" enctype="multipart/form-data" class="profile-form">
            <label for="cvParseFile">Choose CV file *</label>
            <input id="cvParseFile" name="cvParseFile" type="file" accept=".pdf" required>
            <button type="submit" id="parseCvBtn">Parse & Auto-Fill</button>
        </form>
        <p class="alert info hidden" id="parseStatus"></p>
    </section>

    <section class="card">
        <h2>Upload CV</h2>
        <p class="hint">US02: upload a PDF or DOC/DOCX file. The latest upload will replace the previous one.</p>
        <p class="hint">Current CV: <strong>${empty cvFilename ? 'No CV uploaded.' : cvFilename}</strong></p>
        <form method="post" action="${pageContext.request.contextPath}/ta/upload-cv" enctype="multipart/form-data" class="profile-form">
            <input type="hidden" name="studentId" value="${profile.studentId}">
            <label for="cvFile">Choose file *</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>
            <button type="submit">Upload CV</button>
        </form>
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

        <label for="availability">Availability *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">Save Profile</button>
    </form>
</main>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const parseForm = document.getElementById('parseCvForm');
    const parseBtn = document.getElementById('parseCvBtn');
    const parseStatus = document.getElementById('parseStatus');
    const contextPath = '${pageContext.request.contextPath}';

    parseForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const fileInput = document.getElementById('cvParseFile');
        if (!fileInput.files || fileInput.files.length === 0) {
            showStatus('Please select a CV file.', 'error');
            return;
        }

        parseBtn.disabled = true;
        parseBtn.textContent = 'Parsing...';
        showStatus('Parsing CV, please wait...', 'info');

        const formData = new FormData();
        formData.append('cvParseFile', fileInput.files[0]);

        try {
            const response = await fetch(contextPath + '/ta/parse-cv', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (result.success && result.data) {
                const data = result.data;
                fillForm(data);
                showStatus('CV parsed successfully! Review and edit the fields below.', 'success');
            } else {
                showStatus(result.message || 'Failed to parse CV.', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showStatus('An error occurred while parsing the CV.', 'error');
        } finally {
            parseBtn.disabled = false;
            parseBtn.textContent = 'Parse & Auto-Fill';
        }
    });

    function fillForm(data) {
        if (data.name) {
            const nameInput = document.getElementById('name');
            if (nameInput && !nameInput.value.trim()) {
                nameInput.value = data.name;
            }
        }
        if (data.email) {
            const emailInput = document.getElementById('email');
            if (emailInput && !emailInput.value.trim()) {
                emailInput.value = data.email;
            }
        }
        if (data.studentId) {
            const studentIdInput = document.getElementById('studentId');
            if (studentIdInput && !studentIdInput.value.trim()) {
                studentIdInput.value = data.studentId;
            }
        }
        if (data.programme) {
            const programmeInput = document.getElementById('programme');
            if (programmeInput && !programmeInput.value.trim()) {
                programmeInput.value = data.programme;
            }
        }
        if (data.skills) {
            const skillsInput = document.getElementById('skills');
            if (skillsInput && !skillsInput.value.trim()) {
                skillsInput.value = data.skills;
            }
        }
        if (data.availability) {
            const availabilityInput = document.getElementById('availability');
            if (availabilityInput && !availabilityInput.value.trim()) {
                availabilityInput.value = data.availability;
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
