package com.group19.servlet;

import com.google.gson.Gson;
import com.group19.dao.TADao;
import com.group19.dto.ParsedCVData;
import com.group19.dto.ServiceResult;
import com.group19.service.CVParseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@MultipartConfig
public class ParseCVServlet extends HttpServlet {

    private CVParseService cvParseService;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.cvParseService = new CVParseService(new TADao(filePath));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        String studentId = req.getParameter("studentId");
        Path uploadDir = resolveUploadDir();

        ServiceResult<ParsedCVData> result;
        try {
            result = cvParseService.parseSavedCv(studentId, uploadDir);
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
            result = ServiceResult.failure("Error parsing CV: " + e.getClass().getName() + " - " + e.getMessage());
        }

        String jsonResponse = gson.toJson(result);

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonResponse);
        }
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
