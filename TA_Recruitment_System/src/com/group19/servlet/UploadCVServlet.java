package com.group19.servlet;

import com.google.gson.Gson;
import com.group19.dao.TADao;
import com.group19.dto.CVUploadResult;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.CVService;
import com.group19.service.ProfileService;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CV upload servlet, handling TA user CV file upload operations.
 *
 * <p>URL handled: /upload-cv
 *
 * <p>Processing flow:
 * <ol>
 *   <li>Verify TA login state</li>
 *   <li>Retrieve the uploaded CV file from the multipart request</li>
 *   <li>Call CVService to save the CV file and update the TA profile</li>
 *   <li>Supports two response modes:
 *     <ul>
 *       <li>Normal form submission -- redirect to the profile page</li>
 *       <li>AJAX request -- return the result as JSON</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>Permissions: accessible only by TA role.
 * <p>Note: the {@code @MultipartConfig} annotation supports file upload.
 *
 * @author Group 19
 * @see CVService
 * @see ProfileService
 */
@MultipartConfig
public class UploadCVServlet extends HttpServlet {
    private CVService cvService;
    private ProfileService profileService;
    private final Gson gson = new Gson();

    /**
     * Initialises the Servlet, loads the TA data file, and creates the CVService
     * and ProfileService instances.
     */
    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        TADao taDao = new TADao(filePath);
        this.cvService = new CVService(taDao);
        this.profileService = new ProfileService(taDao);
    }

    /**
     * Handles POST requests: receives and saves the CV file uploaded by the TA.
     *
     * <p>Supports two response modes:
     * <ul>
     *   <li>Normal form submission: on success redirects to /profile?cvSaved=true;
     *       on failure redisplays the form</li>
     *   <li>AJAX request (ajax=true or X-Requested-With header): returns the
     *       ServiceResult as JSON</li>
     * </ul>
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
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            if (wantsJsonResponse(req)) {
                writeJsonResponse(resp, ServiceResult.failure("Your session is invalid. Please sign in again."));
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        String studentId = loginUser.getUserId();
        Part cvPart;
        try {
            cvPart = req.getPart("cvFile");
        } catch (IllegalStateException e) {
            cvPart = null;
        }

        Path uploadDir = resolveUploadDir();
        ServiceResult<CVUploadResult> result =
                cvService.uploadCv(studentId, loginUser.getDisplayName(), cvPart, uploadDir);

        if (wantsJsonResponse(req)) {
            writeJsonResponse(resp, result);
            return;
        }

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/profile?cvSaved=true");
            return;
        }

        bindProfileForCurrentUser(req, loginUser);
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    /**
     * Loads the profile data for the current TA user and binds it to request
     * attributes; if no profile exists, a default draft is created.
     *
     * @param req       the HTTP request
     * @param loginUser the currently logged-in TA user
     */
    private void bindProfileForCurrentUser(HttpServletRequest req, LoginUser loginUser) {
        ServiceResult<TA> profileResult = profileService.getProfileByStudentId(loginUser.getUserId());
        if (profileResult.isSuccess() && profileResult.getData() != null) {
            req.setAttribute("profile", profileResult.getData());
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(profileResult.getData().getCvFilePath()));
            return;
        }

        TA draft = new TA();
        draft.setName(loginUser.getDisplayName());
        draft.setStudentId(loginUser.getUserId());
        req.setAttribute("profile", draft);
        req.setAttribute("cvFilename", null);
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

    /**
     * Determines whether the current request expects a JSON response (AJAX request).
     *
     * @param req the HTTP request
     * @return true if a JSON response is expected
     */
    private boolean wantsJsonResponse(HttpServletRequest req) {
        String ajaxParam = req.getParameter("ajax");
        if ("true".equalsIgnoreCase(ajaxParam)) {
            return true;
        }
        String requestedWith = req.getHeader("X-Requested-With");
        return requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    /**
     * Writes the service-layer result as JSON to the HTTP response.
     *
     * @param resp   the HTTP response
     * @param result the CV upload result
     * @throws IOException if an I/O error occurs
     */
    private void writeJsonResponse(HttpServletResponse resp, ServiceResult<CVUploadResult> result) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (var writer = resp.getWriter()) {
            writer.write(gson.toJson(result));
        }
    }
}
