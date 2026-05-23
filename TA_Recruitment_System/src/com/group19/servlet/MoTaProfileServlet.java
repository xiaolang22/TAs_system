package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dao.UserAccountDao;
import com.group19.dto.MoTaCandidateCard;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.service.MoTaDirectoryService;
import com.group19.util.DataPathResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoTaProfileServlet extends HttpServlet {
    private MoTaDirectoryService moTaDirectoryService;

    @Override
    public void init() {
        Path taPath = DataPathResolver.resolve(
                getServletContext(), "taDataFile", "/data/tas.json", "tas.json");
        Path userPath = DataPathResolver.resolve(
                getServletContext(), "userDataFile", "/data/users.json", "users.json");
        this.moTaDirectoryService = new MoTaDirectoryService(new TADao(taPath), new UserAccountDao(userPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute("loginUser");
        if (loginUser == null || !"MO".equalsIgnoreCase(loginUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentId = trimToNull(req.getParameter("studentId"));
        req.setAttribute("backUrl", resolveBackUrl(req));
        req.setAttribute("backLabel", resolveBackLabel(req));
        ServiceResult<MoTaCandidateCard> result = moTaDirectoryService.loadCandidateByStudentId(studentId);
        if (!result.isSuccess()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/mo_ta_profile.jsp").forward(req, resp);
            return;
        }

        MoTaCandidateCard candidate = result.getData();
        attachCandidateAssets(req, candidate);
        req.setAttribute("loginUser", loginUser);
        req.setAttribute("candidate", candidate);
        req.getRequestDispatcher("/WEB-INF/jsp/mo_ta_profile.jsp").forward(req, resp);
    }

    private String resolveBackUrl(HttpServletRequest req) {
        String fallback = req.getContextPath() + "/mo/home";
        String raw = trimToNull(req.getParameter("backUrl"));
        if (raw == null) {
            return fallback;
        }
        String contextPath = req.getContextPath();
        if (raw.startsWith(contextPath + "/mo/applications")
                || raw.startsWith(contextPath + "/mo/home")
                || raw.startsWith(contextPath + "/mo/jobs")) {
            return raw;
        }
        return fallback;
    }

    private String resolveBackLabel(HttpServletRequest req) {
        String backUrl = resolveBackUrl(req);
        String rawLabel = trimToNull(req.getParameter("backLabel"));
        if (rawLabel != null) {
            return rawLabel;
        }
        if (backUrl.contains("/mo/applications")) {
            return "返回申请人列表";
        }
        if (backUrl.contains("/mo/jobs")) {
            return "返回岗位列表";
        }
        return "返回首页";
    }

    private void attachCandidateAssets(HttpServletRequest req, MoTaCandidateCard candidate) {
        if (candidate == null) {
            return;
        }
        String contextPath = req.getContextPath();
        String avatarPath = candidate.getAvatarPath();
        candidate.setAvatarUrl(avatarPath == null || avatarPath.isBlank() ? "" : contextPath + avatarPath);

        String cvFilePath = candidate.getCvFilePath();
        boolean resumeAvailable = hasUploadedResume(cvFilePath);
        candidate.setResumeAvailable(resumeAvailable);
        candidate.setCvUrl(resumeAvailable ? contextPath + cvFilePath : "");
    }

    private boolean hasUploadedResume(String cvFilePath) {
        if (cvFilePath == null || cvFilePath.isBlank()) {
            return false;
        }
        String normalized = cvFilePath.trim().replace("/", java.io.File.separator);
        if (normalized.startsWith(java.io.File.separator)) {
            normalized = normalized.substring(1);
        }
        String realPath = getServletContext().getRealPath("/" + normalized.replace(java.io.File.separatorChar, '/'));
        if (realPath != null && !realPath.isBlank()) {
            return Files.exists(Paths.get(realPath));
        }
        return Files.exists(Paths.get(System.getProperty("user.dir"), "web", normalized));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
