<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.model.Job" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MO Job List - TA Recruitment System</title>
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
            max-width: 1100px;
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
            line-height: 1.6;
        }

        .top-actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 22px;
        }

        .top-actions a {
            display: inline-block;
            text-decoration: none;
            padding: 12px 18px;
            border-radius: 12px;
            font-weight: 700;
        }

        .btn-primary {
            background: #4763e4;
            color: #ffffff;
        }

        .btn-primary:hover {
            background: #3d57d0;
        }

        .btn-secondary {
            background: #eef2ff;
            color: #334155;
        }

        .btn-secondary:hover {
            background: #e2e8f0;
        }

        .job-list {
            display: grid;
            gap: 18px;
        }

        .job-item {
            border: 1px solid #e5e7eb;
            border-radius: 16px;
            padding: 20px;
            background: #fafafa;
        }

        .job-title {
            margin: 0 0 10px;
            font-size: 22px;
            font-weight: 800;
            color: #0f172a;
        }

        .job-meta {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 10px 18px;
            margin-bottom: 14px;
            font-size: 14px;
            color: #475569;
        }

        .job-meta strong {
            color: #111827;
        }

        .job-desc {
            margin: 0 0 16px;
            color: #334155;
            line-height: 1.6;
        }

        .job-actions a {
            display: inline-block;
            text-decoration: none;
            background: #4763e4;
            color: #ffffff;
            padding: 10px 16px;
            border-radius: 10px;
            font-weight: 700;
        }

        .job-actions a:hover {
            background: #3d57d0;
        }

        .empty {
            padding: 18px;
            background: #f8fafc;
            border-radius: 12px;
            color: #475569;
        }

        @media (max-width: 700px) {
            .job-meta {
                grid-template-columns: 1fr;
            }

            h1 {
                font-size: 32px;
            }
        }
    </style>
</head>
<body>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {
        jobs = new ArrayList<>();
    }
%>

<div class="page">
    <div class="container">
        <div class="card">
            <h1>MO Job List</h1>
            <p class="subtitle">
                View all posted jobs and choose one to manage its applicants.
            </p>

            <div class="top-actions">
                <a class="btn-primary" href="<%= request.getContextPath() %>/mo/post-job">Post New Job</a>
                <a class="btn-secondary" href="<%= request.getContextPath() %>/home">Back to Home</a>
            </div>

            <% if (jobs.isEmpty()) { %>
            <div class="empty">No jobs found.</div>
            <% } else { %>
            <div class="job-list">
                <% for (Job job : jobs) { %>
                <div class="job-item">
                    <h2 class="job-title"><%= job.getTitle() == null ? "" : job.getTitle() %></h2>

                    <div class="job-meta">
                        <div><strong>Job ID:</strong> <%= job.getJobId() == null ? "" : job.getJobId() %></div>
                        <div><strong>Category:</strong> <%= job.getCategory() == null ? "" : job.getCategory() %></div>
                        <div><strong>Status:</strong> <%= job.getStatus() == null ? "" : job.getStatus() %></div>
                        <div><strong>Deadline:</strong> <%= job.getDeadline() == null ? "" : job.getDeadline() %></div>
                    </div>

                    <p class="job-desc">
                        <%= job.getDescription() == null ? "" : job.getDescription() %>
                    </p>

                    <div class="job-actions">
                        <a href="<%= request.getContextPath() %>/mo/applications?jobId=<%= job.getJobId() %>">
                            View Applicants
                        </a>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>