<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.group19.dto.AssignedPositionDto" %>
<%@ page import="com.group19.dto.TaWorkloadRow" %>
<%@ page import="com.group19.util.HtmlEscape" %>
<%
    @SuppressWarnings("unchecked")
    List<TaWorkloadRow> workloadRows = (List<TaWorkloadRow>) request.getAttribute("workloadRows");
    if (workloadRows == null) {
        workloadRows = new ArrayList<>();
    }

    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) {
        keyword = "";
    }

    String assignmentFilter = (String) request.getAttribute("assignmentFilter");
    if (assignmentFilter == null || assignmentFilter.isBlank()) {
        assignmentFilter = "all";
    }

    Object rowCountAttr = request.getAttribute("workloadRowCount");
    String rowCount = rowCountAttr == null ? String.valueOf(workloadRows.size()) : String.valueOf(rowCountAttr);

    Object warningCountAttr = request.getAttribute("workloadWarningCount");
    String warningCount = warningCountAttr == null ? "0" : String.valueOf(warningCountAttr);

    Object maxHoursAttr = request.getAttribute("maxWeeklyWorkloadHours");
    String maxWeeklyWorkloadHours = maxHoursAttr == null ? "20" : String.valueOf(maxHoursAttr);

    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TA Workload Dashboard - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container wide workload-shell">
    <header class="page-header">
        <div>
            <h1>TA Workload Dashboard</h1>
            <p class="hint">Monitor accepted TA assignments, total assigned hours, and workload warnings across all positions.</p>
        </div>
        <div class="header-actions">
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
            <a class="link-btn secondary" href="${pageContext.request.contextPath}/admin/workload">Refresh</a>
        </div>
    </header>

    <p class="alert error <%= errorMsg == null || errorMsg.isBlank() ? "hidden" : "" %>">
        <%= HtmlEscape.escape(errorMsg) %>
    </p>

    <section class="card workload-toolbar">
        <div>
            <h2 class="section-title">Search and Filter</h2>
            <p class="hint">Warnings use a <%= HtmlEscape.escape(maxWeeklyWorkloadHours) %>-hour threshold and accepted assignment schedules.</p>
        </div>
        <form method="get" action="<%= request.getContextPath() %>/admin/workload" class="workload-filter-form">
            <div class="form-field">
                <label for="keyword">Keyword</label>
                <input
                        type="text"
                        id="keyword"
                        name="keyword"
                        value="<%= HtmlEscape.escape(keyword) %>"
                        placeholder="Name, student ID, email, programme">
            </div>
            <div class="form-field">
                <label for="assignmentFilter">Assignment status</label>
                <select id="assignmentFilter" name="assignmentFilter">
                    <option value="all" <%= "all".equals(assignmentFilter) ? "selected" : "" %>>All TAs</option>
                    <option value="assigned" <%= "assigned".equals(assignmentFilter) ? "selected" : "" %>>Assigned only</option>
                    <option value="unassigned" <%= "unassigned".equals(assignmentFilter) ? "selected" : "" %>>Unassigned only</option>
                </select>
            </div>
            <div class="filter-actions workload-filter-actions">
                <button type="submit">Apply</button>
                <a class="link-btn secondary" href="<%= request.getContextPath() %>/admin/workload">Reset</a>
            </div>
        </form>
    </section>

    <section class="review-summary-grid workload-summary-grid">
        <div class="summary-card">
            <span class="label">TAs shown</span>
            <span class="value"><%= HtmlEscape.escape(rowCount) %></span>
        </div>
        <div class="summary-card">
            <span class="label">Assignment source</span>
            <span class="value">Accepted applications</span>
        </div>
        <div class="summary-card">
            <span class="label">Hour rule</span>
            <span class="value">First number in job hours</span>
        </div>
        <div class="summary-card">
            <span class="label">Workload warnings</span>
            <span class="value"><%= HtmlEscape.escape(warningCount) %></span>
        </div>
    </section>

    <section class="card table-card workload-table-card">
        <table>
            <thead>
            <tr>
                <th>TA</th>
                <th>Programme</th>
                <th>Assigned positions</th>
                <th>Position count</th>
                <th>Total assigned hours</th>
                <th>Warnings</th>
            </tr>
            </thead>
            <tbody>
            <% if (workloadRows.isEmpty()) { %>
            <tr>
                <td colspan="6">
                    <div class="empty-state">No TA workload records match the current search or filter.</div>
                </td>
            </tr>
            <% } else { %>
            <% for (TaWorkloadRow row : workloadRows) { %>
            <tr class="<%= row.isHasWorkloadWarning() ? "row-warning" : "" %>">
                <td>
                    <div class="table-main">
                        <strong><%= HtmlEscape.escape(row.getName()) %></strong>
                        <span class="table-subtext"><%= HtmlEscape.escape(row.getStudentId()) %></span>
                        <span class="table-subtext"><%= HtmlEscape.escape(row.getEmail()) %></span>
                    </div>
                </td>
                <td><%= HtmlEscape.escape(row.getProgramme().isEmpty() ? "Not provided" : row.getProgramme()) %></td>
                <td>
                    <% if (row.getAssignedPositions().isEmpty()) { %>
                    <span class="muted">No accepted assignments.</span>
                    <% } else { %>
                    <ul class="assigned-position-list">
                        <% for (AssignedPositionDto position : row.getAssignedPositions()) { %>
                        <li>
                            <div class="assigned-position-title">
                                <%= HtmlEscape.escape(position.getTitle()) %>
                            </div>
                            <div class="assigned-position-meta">
                                <span><%= HtmlEscape.escape(position.getCategory().isEmpty() ? "Uncategorized" : position.getCategory()) %></span>
                                <span>Job ID: <%= HtmlEscape.escape(position.getJobId()) %></span>
                                <span>Hours: <%= HtmlEscape.escape(position.getHoursText().isEmpty() ? "Not provided" : position.getHoursText()) %></span>
                                <span>Schedule: <%= HtmlEscape.escape(position.getScheduleText().isEmpty() ? "Not provided" : position.getScheduleText()) %></span>
                                <% if (position.isTimeConflict()) { %>
                                <span class="status-pill tag-alert">conflict</span>
                                <% } %>
                                <% if (!position.isHoursCounted()) { %>
                                <span class="muted">not included in total</span>
                                <% } %>
                            </div>
                        </li>
                        <% } %>
                    </ul>
                    <% } %>
                </td>
                <td>
                    <span class="status-pill tag-neutral"><%= row.getAssignedPositionCount() %></span>
                </td>
                <td>
                    <span class="workload-hours"><%= HtmlEscape.escape(row.getTotalAssignedHoursLabel()) %></span>
                </td>
                <td>
                    <% if (row.isHasWorkloadWarning()) { %>
                    <div class="workload-warning-list">
                        <% for (String reason : row.getWorkloadWarningReasons()) { %>
                        <span class="status-pill tag-alert"><%= HtmlEscape.escape(reason) %></span>
                        <% } %>
                    </div>
                    <% } else { %>
                    <span class="status-pill tag-good">No warning</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
