package com.group19.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FileUploadUtil {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private FileUploadUtil() {
    }

    public static boolean isAllowedCvFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        return lower.endsWith(".pdf")
                || lower.endsWith(".doc")
                || lower.endsWith(".docx")
                || lower.endsWith(".txt");
    }

    public static String saveCvPart(Part cvPart, Path uploadDir, String studentId) throws IOException {
        Files.createDirectories(uploadDir);
        String originalFileName = getSubmittedFileName(cvPart);
        String safeName = sanitizeFileName(originalFileName);
        String extension = getExtension(safeName);
        String prefix = (studentId == null || studentId.isBlank()) ? "ta" : studentId.trim();
        String storedName = prefix + "_" + LocalDateTime.now().format(TS) + extension;
        Path target = uploadDir.resolve(storedName).normalize();

        try (InputStream in = cvPart.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return storedName;
    }

    public static String getSubmittedFileName(Part part) {
        if (part == null) {
            return "";
        }
        String submitted = part.getSubmittedFileName();
        return submitted == null ? "" : submitted.trim();
    }

    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "cv.txt";
        }
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length() - 1) {
            return ".txt";
        }
        return fileName.substring(idx);
    }
}
