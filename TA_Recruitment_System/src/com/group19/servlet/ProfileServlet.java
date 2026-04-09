package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dto.ServiceResult;
import com.group19.model.LoginUser;
import com.group19.model.TA;
import com.group19.service.ProfileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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

        String studentId = req.getParameter("studentId");
        if (studentId != null && !studentId.isBlank()) {
            ServiceResult<TA> result = profileService.getProfileByStudentId(studentId);
            if (result.isSuccess()) {
                req.setAttribute("profile", result.getData());
            } else {
                req.setAttribute("error", result.getMessage());
            }
        }

        if ("true".equalsIgnoreCase(req.getParameter("saved"))) {
            req.setAttribute("success", "Profile saved successfully.");
        }

        if (req.getAttribute("profile") == null) {
            LoginUser loginUser = (LoginUser) req.getAttribute("loginUser");
            if (loginUser != null && "TA".equalsIgnoreCase(loginUser.getRole())) {
                TA draft = new TA();
                draft.setName(loginUser.getDisplayName());
                draft.setStudentId(loginUser.getUserId());
                req.setAttribute("profile", draft);
            }
        }

        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        String name = req.getParameter("name");
        String studentId = req.getParameter("studentId");
        String email = req.getParameter("email");
        String programme = req.getParameter("programme");
        String skills = req.getParameter("skills");
        String availability = req.getParameter("availability");

        ServiceResult<TA> result =
                profileService.saveProfile(name, studentId, email, programme, skills, availability);

        if (result.isSuccess()) {
            String encodedId = URLEncoder.encode(studentId.trim(), StandardCharsets.UTF_8);
            String redirectUrl = req.getContextPath() + "/profile?studentId=" + encodedId + "&saved=true";
            resp.sendRedirect(redirectUrl);
            return;
        }

        TA draft = new TA(name, studentId, email, programme, skills, availability);
        req.setAttribute("profile", draft);
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
}
