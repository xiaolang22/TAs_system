package com.group19.servlet;

import com.google.gson.Gson;
import com.group19.dao.TADao;
import com.group19.dto.CandidateMatchResult;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.MatchingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Candidate Intelligent Evaluation Servlet, providing MOs with
 * skill-match-based ranking of candidate TAs.
 *
 * <p>URL handled: /candidate-review
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Verify the MO identity</li>
 *   <li>Obtain required skills input by the MO (requiredSkills)</li>
 *   <li>Call the matching service to score and rank candidates</li>
 *   <li>Pass the matching results as JSON to the front-end page for rendering</li>
 * </ol>
 *
 * <p>Permissions: accessible only by MO role.
 *
 * @author Group 19
 * @see MatchingService
 */
public class CandidateReviewServlet extends HttpServlet {
    /** Default required skill keywords */
    private static final String DEFAULT_REQUIRED_SKILLS = "Java, communication, problem solving";

    private final Gson gson = new Gson();
    private MatchingService matchingService;

    /**
     * Initialises the Servlet, resolves the TA data file path, and creates the
     * MatchingService instance.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.matchingService = new MatchingService(new TADao(filePath));
    }

    /**
     * Handles GET requests: displays the candidate intelligent evaluation page.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify the MO login identity</li>
     *   <li>Read the required skills parameter; use default skills if not provided</li>
     *   <li>If the user has already searched, call the matching service to compute
     *       candidate TA scores</li>
     *   <li>Convert the matching results into safe JSON and write to the page</li>
     *   <li>Forward to candidate_review.jsp</li>
     * </ol>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = (LoginUser) req.getAttribute("loginUser");
        if (loginUser == null || !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only MO can access candidate review.");
            return;
        }

        String requiredSkills = normalize(req.getParameter("requiredSkills"));
        boolean hasSearched = !requiredSkills.isBlank();
        if (!hasSearched) {
            requiredSkills = DEFAULT_REQUIRED_SKILLS;
        }

        req.setAttribute("requiredSkills", requiredSkills);
        req.setAttribute("hasSearched", hasSearched);

        if (!hasSearched) {
            req.setAttribute("reviewResultsJson", "[]");
            req.setAttribute("resultMessage", "Enter required job skills to calculate candidate match scores.");
        } else {
            ServiceResult<List<CandidateMatchResult>> result = matchingService.evaluateCandidates(requiredSkills);
            if (result.isSuccess()) {
                req.setAttribute("reviewResultsJson", toInlineJson(result.getData()));
                req.setAttribute("resultMessage", result.getMessage());
            } else {
                req.setAttribute("reviewResultsJson", "[]");
                req.setAttribute("error", result.getMessage());
            }
        }

        req.getRequestDispatcher("/WEB-INF/jsp/candidate_review.jsp").forward(req, resp);
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's real
     * path and falling back to the working directory.
     *
     * @param webRelativePath the web-relative path
     * @return the absolute path of the data file
     */
    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }

        return Paths.get(System.getProperty("user.dir"), "data", "tas.json");
    }

    /**
     * Converts the candidate match result list into a safe JSON string by escaping
     * special HTML characters to prevent XSS.
     *
     * @param results the list of candidate match results
     * @return a safe JSON string
     */
    private String toInlineJson(List<CandidateMatchResult> results) {
        String json = gson.toJson(results == null ? Collections.emptyList() : results);
        return json
                .replace("<", "\\u003c")
                .replace(">", "\\u003e")
                .replace("&", "\\u0026");
    }

    /**
     * Normalises the string, returning an empty string for null values.
     *
     * @param value the input string
     * @return the trimmed string, or an empty string if null
     */
    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
