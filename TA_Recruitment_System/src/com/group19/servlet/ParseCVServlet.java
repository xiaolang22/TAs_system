package com.group19.servlet;

import com.google.gson.Gson;
import com.group19.dao.TADao;
import com.group19.dto.ParsedCVData;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.CVParseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CV parsing servlet, handling automatic parsing of CVs uploaded by TAs.
 *
 * <p>URL handled: /parse-cv
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Verify TA login state</li>
 *   <li>Read the CV file already uploaded by the current TA</li>
 *   <li>Call CVParseService for automatic parsing (extracting name, skills,
 *       education background, etc.)</li>
 *   <li>Return the parsing result as JSON to the front-end AJAX call</li>
 * </ol>
 *
 * <p>Permissions: accessible only by TA role (POST method).
 * <p>Note: the {@code @MultipartConfig} annotation supports file upload handling.
 *
 * @author Group 19
 * @see CVParseService
 */
@MultipartConfig
public class ParseCVServlet extends HttpServlet {
    private CVParseService cvParseService;
    private final Gson gson = new Gson();

    /**
     * Initialises the Servlet, loads the TA data file, and creates the CVParseService
     * instance.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.cvParseService = new CVParseService(new TADao(filePath));
    }

    /**
     * Handles POST requests: performs automatic CV parsing and returns the result
     * as JSON.
     *
     * <p>Processing steps:
     * <ol>
     *   <li>Verify TA login state</li>
     *   <li>Call CVParseService to parse the TA's saved CV file</li>
     *   <li>Write the parsing result (ServiceResult&lt;ParsedCVData&gt;) as JSON to
     *       the response</li>
     * </ol>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a Servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        ServiceResult<ParsedCVData> result;

        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            result = ServiceResult.failure("Your session is invalid. Please sign in again.");
        } else {
            Path uploadDir = resolveUploadDir();
            try {
                result = cvParseService.parseSavedCv(loginUser.getUserId(), uploadDir);
            } catch (Exception | NoClassDefFoundError e) {
                e.printStackTrace();
                result = ServiceResult.failure("Resume parsing failed. Please try again later.");
            }
        }

        String jsonResponse = gson.toJson(result);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonResponse);
        }
    }

    /**
     * Retrieves the currently logged-in user, preferring the request attribute
     * first, then falling back to the session.
     *
     * @param req the HTTP request
     * @return the currently logged-in user, or null if not logged in
     */
    private LoginUser currentLoginUser(HttpServletRequest req) {
        Object requestUser = req.getAttribute("loginUser");
        if (requestUser instanceof LoginUser) {
            return (LoginUser) requestUser;
        }
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    /**
     * Resolves the absolute path of a data file, preferring the web application's
     * real path and falling back to the working directory.
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
     * Resolves the absolute path of the upload directory.
     *
     * @return the path of the upload directory
     */
    private Path resolveUploadDir() {
        String realPath = getServletContext().getRealPath("/uploads");
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "uploads");
    }
}
