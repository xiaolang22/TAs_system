package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import com.group19.service.CVService;
import com.group19.service.ProfileService;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@MultipartConfig
public class UploadCVServlet extends HttpServlet {
    private CVService cvService;
    private ProfileService profileService;

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

        String studentId = req.getParameter("studentId");
        Part cvPart;
        try {
            cvPart = req.getPart("cvFile");
        } catch (IllegalStateException e) {
            cvPart = null;
        }

        Path uploadDir = resolveUploadDir();
        ServiceResult<TA> result = cvService.uploadCv(studentId, cvPart, uploadDir);

        if (result.isSuccess()) {
            String encodedId = URLEncoder.encode(studentId.trim(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/profile?studentId=" + encodedId + "&cvSaved=true");
            return;
        }

        ServiceResult<TA> profileResult = profileService.getProfileByStudentId(studentId);
        if (profileResult.isSuccess()) {
            req.setAttribute("profile", profileResult.getData());
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(profileResult.getData().getCvFilePath()));
        } else {
            TA draft = new TA();
            if (studentId != null) {
                draft.setStudentId(studentId.trim());
            }
            req.setAttribute("profile", draft);
            req.setAttribute("cvFilename", null);
        }
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
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
}

