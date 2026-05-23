<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>申请资料 - TA 招聘系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container ta-subpage-shell">
    <header class="page-header">
        <div>
            <h1>申请资料</h1>
            <p class="hint">在这里维护你的姓名、学号、邮箱、技能、经历和简历。</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">返回首页</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/ta/account">个人中心</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="secondary-btn">退出登录</button>
            </form>
        </div>
    </header>

    <form method="get" action="${pageContext.request.contextPath}/profile" class="lookup-form">
        <label for="lookupStudentId">按学号打开已有资料</label>
        <div class="row">
            <input id="lookupStudentId" name="studentId" type="text" placeholder="例如：231221618">
            <button type="submit">打开</button>
        </div>
    </form>

    <p class="alert success ${empty success ? 'hidden' : ''}">${success}</p>
    <p class="alert error ${empty error ? 'hidden' : ''}">${error}</p>

    <section class="card">
        <h2>简历上传与自动填充</h2>
        <p class="hint">支持上传 PDF、DOC、DOCX。新上传的简历会覆盖旧简历。</p>
        <p class="hint">自动填充会读取当前学号最新保存的简历，并尝试提取专业、技能与经历。</p>
        <p class="hint">当前简历：<strong id="currentCvName">${empty cvFilename ? '暂未上传简历' : cvFilename}</strong></p>
        <form id="saveCvForm" method="post" action="${pageContext.request.contextPath}/ta/upload-cv" enctype="multipart/form-data" class="profile-form">
            <input id="uploadStudentId" type="hidden" name="studentId" value="${profile.studentId}">
            <label for="cvFile">选择简历文件 *</label>
            <input id="cvFile" name="cvFile" type="file" accept=".pdf,.doc,.docx" required>
        </form>
        <div class="row action-row">
            <button type="button" id="saveCvBtn">保存简历</button>
            <button type="button" id="parseCvBtn" class="secondary-btn">自动填充字段</button>
        </div>
        <p class="alert info hidden" id="parseStatus"></p>
    </section>

    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form">
        <label for="name">姓名 *</label>
        <input id="name" name="name" type="text" value="${profile.name}" required>

        <label for="studentId">学号 *</label>
        <input id="studentId" name="studentId" type="text" value="${profile.studentId}" required>

        <label for="email">邮箱 *</label>
        <input id="email" name="email" type="email" value="${profile.email}" required>

        <label for="programme">专业 / 项目 *</label>
        <input id="programme" name="programme" type="text" value="${profile.programme}" required>

        <label for="skills">技能 *</label>
        <textarea id="skills" name="skills" rows="4" required>${profile.skills}</textarea>

        <label for="experience">经历（可由简历自动填充）</label>
        <textarea id="experience" name="experience" rows="4">${profile.experience}</textarea>

        <label for="availability">可工作时间 *</label>
        <textarea id="availability" name="availability" rows="3" required>${profile.availability}</textarea>

        <button type="submit">保存申请资料</button>
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
            showStatus('请先选择要上传的简历文件。', 'error');
            return;
        }
        if (uploadStudentIdInput && studentIdInput) {
            uploadStudentIdInput.value = studentIdInput.value;
        }
        if (!uploadStudentIdInput.value.trim()) {
            showStatus('请先填写学号，再保存简历。', 'error');
            return;
        }

        saveCvBtn.disabled = true;
        parseBtn.disabled = true;
        showStatus('正在保存简历，请稍候...', 'info');

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
                showStatus('保存简历失败：服务器返回了无法识别的响应。', 'error');
                return;
            }

            if (result.success && result.data && result.data.profile && result.data.profile.cvFilePath) {
                currentCvName.textContent = extractFileName(result.data.profile.cvFilePath);
                fileInput.value = '';
                showStatus(result.message || '简历保存成功。', 'success');
            } else {
                showStatus(result.message || '简历保存失败。', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showStatus('保存简历时发生异常。', 'error');
        } finally {
            saveCvBtn.disabled = false;
            parseBtn.disabled = false;
        }
    });

    parseBtn.addEventListener('click', async function() {
        const studentIdValue = studentIdInput ? studentIdInput.value.trim() : '';
        if (!studentIdValue) {
            showStatus('请先填写学号，再使用自动填充。', 'error');
            return;
        }

        parseBtn.disabled = true;
        saveCvBtn.disabled = true;
        parseBtn.textContent = '解析中...';
        showStatus('正在解析最新简历，请稍候...', 'info');

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
                showStatus('简历解析成功，请检查并完善下方字段后再保存。', 'success');
            } else {
                showStatus(result.message || '简历解析失败。', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showStatus('解析简历时发生异常。', 'error');
        } finally {
            parseBtn.disabled = false;
            saveCvBtn.disabled = false;
            parseBtn.textContent = '自动填充字段';
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
            return '暂未上传简历';
        }
        const normalized = String(filePath).replace(/\\/g, '/');
        const lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }
});
</script>
</body>
</html>
