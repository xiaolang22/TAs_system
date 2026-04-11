<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.group19.model.Job" %>
<%
    Job job = (Job) request.getAttribute("job");
    if (job == null) {
        job = new Job();
    }

    String errorMsg = (String) request.getAttribute("errorMsg");
    String success = request.getParameter("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Post Job - TA Recruitment System</title>
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f3f4f6;
            color: #0f172a;
        }

        .page {
            min-height: 100vh;
            display: flex;
            align-items: flex-start;
            justify-content: center;
            padding: 40px 16px;
        }

        .card {
            width: 100%;
            max-width: 760px;
            background: #ffffff;
            border-radius: 20px;
            padding: 32px 28px 24px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
        }

        .role-tag {
            display: inline-block;
            margin-bottom: 12px;
            font-size: 15px;
            font-weight: 700;
            color: #1e40af;
            background: #eaf1ff;
            border-radius: 999px;
            padding: 8px 14px;
        }

        h1 {
            margin: 0 0 8px;
            font-size: 42px;
            line-height: 1.15;
            font-weight: 800;
            color: #0f172a;
        }

        .subtitle {
            margin: 0 0 28px;
            font-size: 16px;
            color: #475569;
            line-height: 1.6;
        }

        .action-row {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 20px;
        }

        .action-link {
            display: inline-block;
            text-decoration: none;
            border-radius: 12px;
            padding: 12px 18px;
            font-weight: 700;
        }

        .action-link.primary {
            background: #4763e4;
            color: #ffffff;
        }

        .action-link.primary:hover {
            background: #3d57d0;
        }

        .action-link.secondary {
            background: #eef2ff;
            color: #334155;
        }

        .action-link.secondary:hover {
            background: #e2e8f0;
        }

        .info-box {
            background: #eef4ff;
            border: 1px solid #dbe7ff;
            border-radius: 14px;
            padding: 16px 18px;
            margin-bottom: 22px;
        }

        .info-box strong {
            display: block;
            margin-bottom: 6px;
            font-size: 16px;
            color: #0f172a;
        }

        .info-box p {
            margin: 0;
            color: #475569;
            line-height: 1.6;
            font-size: 14px;
        }

        .message {
            margin-bottom: 18px;
            padding: 14px 16px;
            border-radius: 12px;
            font-size: 14px;
            line-height: 1.5;
        }

        .message.success {
            background: #ecfdf3;
            border: 1px solid #bbf7d0;
            color: #166534;
        }

        .message.error {
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #b91c1c;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-size: 15px;
            font-weight: 700;
            color: #0f172a;
        }

        input,
        select,
        textarea {
            width: 100%;
            border: 1px solid #d1d5db;
            border-radius: 12px;
            padding: 13px 14px;
            font-size: 15px;
            color: #111827;
            background: #ffffff;
            outline: none;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        input:focus,
        select:focus,
        textarea:focus {
            border-color: #4f46e5;
            box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.10);
        }

        textarea {
            min-height: 120px;
            resize: vertical;
        }

        .row {
            display: flex;
            gap: 16px;
        }

        .row .form-group {
            flex: 1;
        }

        .btn {
            width: 100%;
            margin-top: 8px;
            border: none;
            border-radius: 12px;
            background: #4763e4;
            color: #ffffff;
            font-size: 18px;
            font-weight: 700;
            padding: 15px 16px;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .btn:hover {
            background: #3d57d0;
        }

        @media (max-width: 700px) {
            .card {
                padding: 24px 18px 20px;
                border-radius: 16px;
            }

            h1 {
                font-size: 34px;
            }

            .row {
                flex-direction: column;
                gap: 0;
            }
        }
    </style>
</head>
<body>
<div class="page">
    <div class="card">
        <div class="role-tag">Current role: MO</div>

        <h1>Post a Job</h1>
        <p class="subtitle">
            Create a new TA recruitment post for applicants.
        </p>

        <div class="action-row">
            <a class="action-link primary" href="<%= request.getContextPath() %>/mo/jobs">View Job List</a>
            <a class="action-link secondary" href="<%= request.getContextPath() %>/home">Back to Home</a>
        </div>

        <div class="info-box">
            <strong>MO Workspace</strong>
            <p>
                Fill in the information below and submit the form to publish a new job.
            </p>
        </div>

        <% if ("true".equals(success)) { %>
        <div class="message success">Job posted successfully.</div>
        <% } %>

        <% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
        <div class="message error"><%= errorMsg %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/mo/post-job">
            <div class="form-group">
                <label for="title">Title</label>
                <input
                        type="text"
                        id="title"
                        name="title"
                        placeholder="e.g. Introduction to Java Teaching Assistant"
                        value="<%= job.getTitle() == null ? "" : job.getTitle() %>">
            </div>

            <div class="row">
                <div class="form-group">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <option value="">Please select</option>
                        <option value="TA" <%= "TA".equals(job.getCategory()) ? "selected" : "" %>>TA</option>
                        <option value="Invigilator" <%= "Invigilator".equals(job.getCategory()) ? "selected" : "" %>>Invigilator</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="hours">Hours</label>
                    <input
                            type="text"
                            id="hours"
                            name="hours"
                            placeholder="e.g. 10 hours/week"
                            value="<%= job.getHours() == null ? "" : job.getHours() %>">
                </div>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea
                        id="description"
                        name="description"
                        placeholder="Describe the job responsibilities."><%= job.getDescription() == null ? "" : job.getDescription() %></textarea>
            </div>

            <div class="form-group">
                <label for="requirements">Requirements</label>
                <textarea
                        id="requirements"
                        name="requirements"
                        placeholder="List the skills or experience required."><%= job.getRequirements() == null ? "" : job.getRequirements() %></textarea>
            </div>

            <div class="row">
                <div class="form-group">
                    <label for="schedule">Schedule</label>
                    <input
                            type="text"
                            id="schedule"
                            name="schedule"
                            placeholder="e.g. Monday, Wednesday, Friday 2-4pm"
                            value="<%= job.getSchedule() == null ? "" : job.getSchedule() %>">
                </div>

                <div class="form-group">
                    <label for="deadline">Deadline</label>
                    <input
                            type="date"
                            id="deadline"
                            name="deadline"
                            value="<%= job.getDeadline() == null ? "" : job.getDeadline() %>">
                </div>
            </div>

            <button class="btn" type="submit">Post Job</button>
        </form>
    </div>
</div>
</body>
</html>