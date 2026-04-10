package com.group19.servlet;

import com.group19.dao.TADao;
import com.group19.dto.CVExtractResult;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@MultipartConfig
public class UploadCVServlet extends HttpServlet {
    private ProfileService profileService;
    private CVService cvService;
    private Path uploadDir;

    @Override
    public void init() {
        String configuredDataPath = getServletContext().getInitParameter("taDataFile");
        String dataPath = (configuredDataPath == null || configuredDataPath.isBlank())
                ? "/data/tas.json"
                : configuredDataPath;

        String configuredUploadPath = getServletContext().getInitParameter("uploadDir");
        String relativeUploadPath = (configuredUploadPath == null || configuredUploadPath.isBlank())
                ? "/uploads"
                : configuredUploadPath;

        Path taFilePath = resolvePath(dataPath, true);
        this.uploadDir = resolvePath(relativeUploadPath, false);
        this.profileService = new ProfileService(new TADao(taFilePath));
        this.cvService = new CVService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");

        TA profile = buildDraftFromRequest(req);
        String studentId = safe(req.getParameter("studentId"));
        if (!studentId.isBlank()) {
            profile.setStudentId(studentId);
            mergeWithSavedProfile(profile, studentId);
        }

        String cvText = safe(req.getParameter("cvText"));
        CVExtractResult extracted;
        if (!cvText.isBlank()) {
            extracted = cvService.extractFromText(cvText);
            req.setAttribute("info", "CV text parsed. You can review and edit before saving profile.");
        } else {
            Part cvPart = req.getPart("cvFile");
            String originalName = FileUploadUtil.getSubmittedFileName(cvPart);
            if (originalName.isBlank()) {
                req.setAttribute("error", "Please upload a CV file or paste CV text.");
                req.setAttribute("profile", profile);
                req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
                return;
            }
            if (!FileUploadUtil.isAllowedCvFile(originalName)) {
                req.setAttribute("error", "Invalid file type. Use PDF, DOC, DOCX or TXT.");
                req.setAttribute("profile", profile);
                req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
                return;
            }

            String storedName = FileUploadUtil.saveCvPart(cvPart, uploadDir, studentId);
            extracted = cvService.extractFromUpload(cvPart, originalName, uploadDir.resolve(storedName).toString());
            profile.setCvFileName(storedName);
            req.setAttribute("info", "CV uploaded and parsed. Extracted values have been pre-filled.");
        }

        profile = cvService.mergeExtractedData(profile, extracted, profile.getCvFileName());
        req.setAttribute("profile", profile);
        req.setAttribute("extractedText", shorten(extracted.getExtractedText(), 1200));
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }

    private void mergeWithSavedProfile(TA draft, String studentId) {
        ServiceResult<TA> existing = profileService.getProfileByStudentId(studentId);
        if (!existing.isSuccess() || existing.getData() == null) {
            return;
        }
        TA saved = existing.getData();
        draft.setName(firstNonBlank(draft.getName(), saved.getName()));
        draft.setEmail(firstNonBlank(draft.getEmail(), saved.getEmail()));
        draft.setProgramme(firstNonBlank(draft.getProgramme(), saved.getProgramme()));
        draft.setSkills(firstNonBlank(draft.getSkills(), saved.getSkills()));
        draft.setExperience(firstNonBlank(draft.getExperience(), saved.getExperience()));
        draft.setAvailability(firstNonBlank(draft.getAvailability(), saved.getAvailability()));
        draft.setCvFileName(firstNonBlank(draft.getCvFileName(), saved.getCvFileName()));
    }

    private TA buildDraftFromRequest(HttpServletRequest req) {
        TA profile = new TA();
        profile.setName(safe(req.getParameter("name")));
        profile.setStudentId(safe(req.getParameter("studentId")));
        profile.setEmail(safe(req.getParameter("email")));
        profile.setProgramme(safe(req.getParameter("programme")));
        profile.setSkills(safe(req.getParameter("skills")));
        profile.setExperience(safe(req.getParameter("experience")));
        profile.setAvailability(safe(req.getParameter("availability")));
        return profile;
    }

    private Path resolvePath(String webRelativePath, boolean dataFile) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }
        return dataFile
                ? Paths.get(System.getProperty("user.dir"), "data", "tas.json")
                : Paths.get(System.getProperty("user.dir"), "web", "uploads");
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String shorten(String content, int maxLen) {
        if (content == null || content.length() <= maxLen) {
            return safe(content);
        }
        return content.substring(0, maxLen) + " ...";
    }

    private static String firstNonBlank(String first, String second) {
        return safe(first).isBlank() ? safe(second) : safe(first);
    }
}
