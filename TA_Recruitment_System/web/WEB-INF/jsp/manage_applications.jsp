<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.model.Application" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Applications - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f3f4f6;
            color: #0f172a;
        }

        .page {
            min-height: 100vh;
            padding: 36px 16px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .card {
            background: #ffffff;
            border-radius: 20px;
            padding: 28px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
        }

        h1 {
            margin: 0 0 8px;
            font-size: 38px;
            font-weight: 800;
        }

        .subtitle {
            margin: 0 0 24px;
            color: #475569;
        }

        .message {
            padding: 14px 16px;
            border-radius: 12px;
            margin-bottom: 18px;
            font-size: 14px;
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

        .top-link {
            margin-bottom: 18px;
        }

        .top-link a {
            color: #4763e4;
            text-decoration: none;
            font-weight: 600;
        }

        .top-link a:hover {
            text-decoration: underline;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
        }

        th, td {
            border: 1px solid #e5e7eb;
            padding: 12px;
            vertical-align: top;
            text-align: left;
            font-size: 14px;
        }

        th {
            background: #f8fafc;
            font-weight: 700;
        }

        select, textarea, button {
            width: 100%;
            font-size: 14px;
            border-radius: 10px;
        }

        select, textarea {
            border: 1px solid #d1d5db;
            padding: 10px 12px;
            margin-bottom: 8px;
        }

        textarea {
            min-height: 70px;
            resize: vertical;
        }

        button {
            border: none;
            background: #4763e4;
            color: #ffffff;
            padding: 10px 12px;
            font-weight: 700;
            cursor: pointer;
        }

        button:hover {
            background: #3d57d0;
        }

        .empty {
            padding: 18px;
            background: #f8fafc;
            border-radius: 12px;
            color: #475569;
        }
    </style>
</head>
<body>
<%
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    if (applications == null) {
        applications = new ArrayList<>();
    }

    String jobId = (String) request.getAttribute("jobId");
    String errorMsg = (String) request.getAttribute("errorMsg");
    String updated = request.getParameter("updated");
%>

<div class="page">
    <div class="container">
        <div class="card">
            <div class="top-link">
                <a href="<%= request.getContextPath() %>/home">Back to Home</a>
            </div>

            <h1>Manage Applications</h1>
            <p class="subtitle">
                Current Job ID: <strong><%= jobId == null ? "" : jobId %></strong>
            </p>

            <% if ("true".equals(updated)) { %>
            <div class="message success">Application status updated successfully.</div>
            <% } %>

            <% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
            <div class="message error"><%= errorMsg %></div>
            <% } %>

            <% if (applications.isEmpty()) { %>
            <div class="empty">No applications found for this job.</div>
            <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>Applicant Name</th>
                    <th>Student ID</th>
                    <th>CV File</th>
                    <th>Status</th>
                    <th>Submitted At</th>
                    <th>Updated At</th>
                    <th>Decision Note</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (Application app : applications) { %>
                <tr>
                    <td><%= app.getTaName() == null ? "" : app.getTaName() %></td>
                    <td><%= app.getTaStudentId() == null ? "" : app.getTaStudentId() %></td>
                    <td><%= app.getCvFilePath() == null ? "" : app.getCvFilePath() %></td>
                    <td><%= app.getStatus() == null ? "" : app.getStatus() %></td>
                    <td><%= app.getSubmittedAt() == null ? "" : app.getSubmittedAt() %></td>
                    <td><%= app.getUpdatedAt() == null ? "" : app.getUpdatedAt() %></td>
                    <td><%= app.getDecisionNote() == null ? "" : app.getDecisionNote() %></td>
                    <td>
                        <form method="post" action="<%= request.getContextPath() %>/mo/applications">
                            <input type="hidden" name="jobId" value="<%= jobId == null ? "" : jobId %>">
                            <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">

                            <select name="status">
                                <option value="SHORTLISTED" <%= "SHORTLISTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>SHORTLISTED</option>
                                <option value="ACCEPTED" <%= "ACCEPTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>ACCEPTED</option>
                                <option value="REJECTED" <%= "REJECTED".equalsIgnoreCase(app.getStatus()) ? "selected" : "" %>>REJECTED</option>
                            </select>

                            <textarea name="decisionNote" placeholder="Optional feedback"><%= app.getDecisionNote() == null ? "" : app.getDecisionNote() %></textarea>

                            <button type="submit">Update</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>