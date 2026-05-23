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

@MultipartConfig
public class UploadCVServlet extends HttpServlet {
    private CVService cvService;
    private ProfileService profileService;
    private final Gson gson = new Gson();

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            if (wantsJsonResponse(req)) {
                writeJsonResponse(resp, ServiceResult.failure("当前登录状态无效，请重新登录。"));
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
        ServiceResult<CVUploadResult> result = cvService.uploadCv(studentId, cvPart, uploadDir);

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

    private LoginUser currentLoginUser(HttpServletRequest req) {
        Object requestUser = req.getAttribute("loginUser");
        if (requestUser instanceof LoginUser) {
            return (LoginUser) requestUser;
        }
        HttpSession session = req.getSession(false);
        return session == null ? null : (LoginUser) session.getAttribute("loginUser");
    }

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "data", "tas.json");
    }

    private Path resolveUploadDir() {
        String realPath = getServletContext().getRealPath("/uploads");
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return Paths.get(System.getProperty("user.dir"), "uploads");
    }

    private boolean wantsJsonResponse(HttpServletRequest req) {
        String ajaxParam = req.getParameter("ajax");
        if ("true".equalsIgnoreCase(ajaxParam)) {
            return true;
        }
        String requestedWith = req.getHeader("X-Requested-With");
        return requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    private void writeJsonResponse(HttpServletResponse resp, ServiceResult<CVUploadResult> result) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (var writer = resp.getWriter()) {
            writer.write(gson.toJson(result));
        }
    }
}
