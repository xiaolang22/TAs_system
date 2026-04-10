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

public class CandidateReviewServlet extends HttpServlet {
    private static final String DEFAULT_REQUIRED_SKILLS = "Java, communication, problem solving";

    private final Gson gson = new Gson();
    private MatchingService matchingService;

    @Override
    public void init() {
        String configuredPath = getServletContext().getInitParameter("taDataFile");
        String relativePath = configuredPath == null || configuredPath.isBlank()
                ? "/data/tas.json"
                : configuredPath;

        Path filePath = resolveDataPath(relativePath);
        this.matchingService = new MatchingService(new TADao(filePath));
    }

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
            req.setAttribute("resultMessage", "Input job-required skills, then click Calculate Match Score.");
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

    private Path resolveDataPath(String webRelativePath) {
        String realPath = getServletContext().getRealPath(webRelativePath);
        if (realPath != null && !realPath.isBlank()) {
            return Paths.get(realPath);
        }

        return Paths.get(System.getProperty("user.dir"), "data", "tas.json");
    }

    private String toInlineJson(List<CandidateMatchResult> results) {
        String json = gson.toJson(results == null ? Collections.emptyList() : results);
        return json
                .replace("<", "\\u003c")
                .replace(">", "\\u003e")
                .replace("&", "\\u0026");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
