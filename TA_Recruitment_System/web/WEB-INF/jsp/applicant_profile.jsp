<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Applicant Profile - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="ta-page">
<main class="container wide ta-subpage-shell review-shell">
    <header class="review-hero">
        <div class="hero-copy">
            <h1>Applicant Profile</h1>
            <p class="hint">Review this applicant's profile, resume, and match summary before updating the decision.</p>
            <p class="hint">Current job: <strong>${empty jobTitle ? jobId : jobTitle}</strong></p>
        </div>
        <div class="review-badges">
            <a class="link-btn secondary" href="${backUrl}">Back to Application List</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/mo/jobs">Back to Job List</a>
        </div>
    </header>

    <section class="detail-layout">
        <div class="detail-stack">
            <section class="card detail-card">
                <h2 class="section-title">${applicant.taName}</h2>
                <div class="review-badges">
                    <span class="status-pill tag-neutral">Match Score: ${applicant.matchScore}%</span>
                    <span class="status-pill tag-neutral">${applicant.currentWorkloadLabel}</span>
                    <span class="status-pill tag-neutral">${applicationStatusLabel}</span>
                </div>
            </section>

            <section class="card">
                <h2 class="section-title">Profile Details</h2>
                <div class="detail-grid">
                    <div class="detail-item">
                        <span class="label">Student ID</span>
                        <div class="value">${applicant.taStudentId}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">Programme</span>
                        <div class="value">${empty applicant.programme ? 'Not provided' : applicant.programme}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">Skills</span>
                        <div class="value">
                            <div class="skill-cloud">${applicant.coreSkillsHtml}</div>
                        </div>
                    </div>
                    <div class="detail-item">
                        <span class="label">Availability</span>
                        <div class="value">${empty applicant.availability ? 'Not provided' : applicant.availability}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">Experience</span>
                        <div class="value">${empty applicant.experience ? 'Not provided' : applicant.experience}</div>
                    </div>
                    <div class="detail-item">
                        <span class="label">Application Status</span>
                        <div class="value">${applicationStatusLabel}</div>
                    </div>
                </div>
            </section>
        </div>

        <aside class="detail-stack">
            <section class="card detail-card">
                <h2 class="section-title">Resume</h2>
                <p class="value ${resumeAvailableClass}">
                    <a class="link-btn" href="${resumeHref}" target="_blank" rel="noopener">Open Resume</a>
                </p>
                <p class="value ${resumeMissingClass} muted">No resume has been uploaded.</p>
            </section>

            <section class="card">
                <h2 class="section-title">Match Summary</h2>
                <div class="detail-item">
                    <span class="label">Matched Skills</span>
                    <div class="value">${empty applicant.matchedSkillsText ? 'None' : applicant.matchedSkillsText}</div>
                </div>
                <div class="detail-item detail-note">
                    <span class="label">Missing Skills</span>
                    <div class="value">${empty applicant.missingSkillsText ? 'None' : applicant.missingSkillsText}</div>
                </div>
            </section>
        </aside>
    </section>
</main>
</body>
</html>
