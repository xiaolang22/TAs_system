package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.ProfileService;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProfileServlet extends HttpServlet {
    private ProfileService profileService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.profileService = new ProfileService(new TADao(filePath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        loadProfileForCurrentUser(req, loginUser);

        if ("true".equalsIgnoreCase(req.getParameter("saved"))) {
            req.setAttribute("success", "个人档案已保存。");
        } else if ("true".equalsIgnoreCase(req.getParameter("cvSaved"))) {
            req.setAttribute("success", "简历已上传。");
        }

        TA profile = (TA) req.getAttribute("profile");
        if (profile != null) {
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(profile.getCvFilePath()));
        }

        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        LoginUser loginUser = currentLoginUser(req);
        if (loginUser == null || !"TA".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = loginUser.getUserId();
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String programme = req.getParameter("programme");
        String skills = req.getParameter("skills");
        String experience = req.getParameter("experience");
        String availability = req.getParameter("availability");

        ServiceResult<TA> result = profileService.saveProfile(
                name, studentId, email, programme, skills, experience, availability);

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/profile?saved=true");
            return;
        }

        TA draft = new TA(name, studentId, email, programme, skills, availability);
        draft.setExperience(experience);

        ServiceResult<TA> existingResult = profileService.getProfileByStudentId(studentId);
        if (existingResult.isSuccess() && existingResult.getData() != null) {
            draft.setCvFilePath(existingResult.getData().getCvFilePath());
            draft.setUpdatedAt(existingResult.getData().getUpdatedAt());
            req.setAttribute("cvFilename", FileUploadUtil.extractFileNameFromPath(existingResult.getData().getCvFilePath()));
        }

        req.setAttribute("profile", draft);
        req.setAttribute("error", result.getMessage());
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    private void loadProfileForCurrentUser(HttpServletRequest req, LoginUser loginUser) {
        ServiceResult<TA> result = profileService.getProfileByStudentId(loginUser.getUserId());
        if (result.isSuccess() && result.getData() != null) {
            req.setAttribute("profile", result.getData());
            return;
        }

        TA draft = new TA();
        draft.setName(loginUser.getDisplayName());
        draft.setStudentId(loginUser.getUserId());
        req.setAttribute("profile", draft);

        if (!"未找到个人档案。".equals(result.getMessage())) {
            req.setAttribute("error", result.getMessage());
        }
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
}
