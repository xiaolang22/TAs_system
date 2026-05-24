package com.group19.servlet;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.dto.TaWorkloadRow;
import com.group19.model.LoginUser;
import com.group19.service.WorkloadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Workload monitoring servlet, providing the admin dashboard for TA workload
 * and timetable conflict oversight.
 *
 * <p>URL handled: /admin/workload
 *
 * <p>Supported features:
 * <ul>
 *   <li>Search TAs by keyword</li>
 *   <li>Filter by assignment status (assigned / unassigned / all)</li>
 *   <li>Display weekly hour statistics and workload warnings for each TA</li>
 *   <li>Display timetable conflict information</li>
 * </ul>
 *
 * <p>The maximum weekly workload threshold is configured via the
 * maxWeeklyWorkloadHours parameter in web.xml, defaulting to 20 hours.
 *
 * <p>Permissions: accessible only by ADMIN role.
 *
 * @author Group 19
 * @see WorkloadService
 */
public class WorkloadServlet extends HttpServlet {
    private WorkloadService workloadService;
    /** Maximum weekly workload threshold in hours; defaults to 20 */
    private int maxWeeklyWorkloadHours;

    /**
     * Initialises the Servlet, loads TA, application, and job data files, and
     * creates the WorkloadService instance. Reads the maxWeeklyWorkloadHours
     * configuration parameter from web.xml.
     */
    @Override
    public void init() {
        String taDataPath = getServletContext().getInitParameter("taDataFile");
        String taRelativePath = taDataPath == null || taDataPath.isBlank()
                ? "/data/tas.json"
                : taDataPath;
        Path taFilePath = resolveDataPath(taRelativePath, "tas.json");

        String applicationDataPath = getServletContext().getInitParameter("applicationDataFile");
        String applicationRelativePath = applicationDataPath == null || applicationDataPath.isBlank()
                ? "/data/applications.json"
                : applicationDataPath;
        Path applicationFilePath = resolveDataPath(applicationRelativePath, "applications.json");

        String jobDataPath = getServletContext().getInitParameter("jobDataFile");
        String jobRelativePath = jobDataPath == null || jobDataPath.isBlank()
                ? "/data/jobs.json"
                : jobDataPath;
        Path jobFilePath = resolveDataPath(jobRelativePath, "jobs.json");

        this.maxWeeklyWorkloadHours = parsePositiveInt(
                getServletContext().getInitParameter("maxWeeklyWorkloadHours"),
                20);
        this.workloadService = new WorkloadService(
                new TADao(taFilePath),
                new ApplicationDao(applicationFilePath),
                new JobDao(jobFilePath),
                maxWeeklyWorkloadHours);
    }

    /**
     * Handles GET requests: displays the TA workload dashboard.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify admin login identity</li>
     *   <li>Obtain keyword and assignment status filter parameters</li>
     *   <li>Call WorkloadService to load workload data</li>
     *   <li>Count workload warnings</li>
     *   <li>Forward to workload_dashboard.jsp for display</li>
     * </ol>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admin can access workload dashboard.");
            return;
        }

        String keyword = trim(req.getParameter("keyword"));
        String assignmentFilter = normalizeAssignmentFilter(req.getParameter("assignmentFilter"));
        ServiceResult<List<TaWorkloadRow>> result = workloadService.loadWorkloadRows(keyword, assignmentFilter);

        List<TaWorkloadRow> rows = result.isSuccess() ? result.getData() : new ArrayList<>();
        req.setAttribute("loginUser", loginUser);
        req.setAttribute("keyword", keyword);
        req.setAttribute("assignmentFilter", assignmentFilter);
        req.setAttribute("workloadRows", rows);
        req.setAttribute("workloadRowCount", rows.size());
        req.setAttribute("workloadWarningCount", countWorkloadWarnings(rows));
        req.setAttribute("maxWeeklyWorkloadHours", maxWeeklyWorkloadHours);
        req.setAttribute("errorMsg", result.isSuccess() ? "" : result.getMessage());

        req.getRequestDispatcher("/WEB-INF/jsp/workload_dashboard.jsp").forward(req, resp);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's
     * real path and falling back to the data folder under the working directory.
     *
     * @param webRelativePath  the web-relative path
     * @param fallbackFileName the fallback file name
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath, String fallbackFileName) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", fallbackFileName);
    }

    /**
     * Normalises the assignment status filter parameter, accepting only "assigned"
     * or "unassigned"; all others default to "all".
     *
     * @param assignmentFilter the raw filter parameter
     * @return the normalised filter value
     */
    private static String normalizeAssignmentFilter(String assignmentFilter) {
        if ("assigned".equalsIgnoreCase(assignmentFilter)) {
            return "assigned";
        }
        if ("unassigned".equalsIgnoreCase(assignmentFilter)) {
            return "unassigned";
        }
        return "all";
    }

    /**
     * Normalises a string, returning an empty string for null values.
     *
     * @param value the input string
     * @return the trimmed string, or an empty string if null
     */
    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Counts the number of TA workload rows that have workload warnings.
     *
     * @param rows the list of workload rows
     * @return the number of rows with warnings
     */
    private static int countWorkloadWarnings(List<TaWorkloadRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (TaWorkloadRow row : rows) {
            if (row != null && row.isHasWorkloadWarning()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Parses a string as a positive integer, returning a default value if the string
     * is invalid or non-positive.
     *
     * @param value        the string value
     * @param defaultValue the default value
     * @return the parsed positive integer
     */
    private static int parsePositiveInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}
