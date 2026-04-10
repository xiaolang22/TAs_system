package com.group19.servlet;

import com.google.gson.Gson;
import com.group19.dto.ParsedCVData;
import com.group19.dto.ServiceResult;
import com.group19.service.CVParseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@MultipartConfig
public class ParseCVServlet extends HttpServlet {

    private CVParseService cvParseService;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        this.cvParseService = new CVParseService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");

        Part cvPart;
        try {
            cvPart = req.getPart("cvParseFile");
        } catch (IllegalStateException e) {
            cvPart = null;
        }

        ServiceResult<ParsedCVData> result;
        try {
            result = cvParseService.parseCV(cvPart);
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
            result = ServiceResult.failure("Error parsing CV: " + e.getClass().getName() + " - " + e.getMessage());
        }

        String jsonResponse = gson.toJson(result);

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonResponse);
        }
    }
}
