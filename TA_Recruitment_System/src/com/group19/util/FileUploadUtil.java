package com.group19.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

public final class FileUploadUtil {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");

    private FileUploadUtil() {
    }

    public static boolean isAllowedCvFile(String submittedFileName) {
        String ext = getExtension(submittedFileName);
        return ext != null && ALLOWED_EXTENSIONS.contains(ext);
    }

    public static String buildStoredCvFileName(String studentId, String submittedFileName) {
        String ext = getExtension(submittedFileName);
        if (ext == null) {
            ext = "pdf";
        }
        return "cv_" + safeToken(studentId) + "." + ext;
    }

    public static void savePartToFile(Part part, Path targetFile) throws IOException {
        Files.createDirectories(targetFile.getParent());
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String extractFileNameFromPath(String cvFilePath) {
        if (cvFilePath == null) {
            return null;
        }
        String trimmed = cvFilePath.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        int slash = Math.max(trimmed.lastIndexOf('/'), trimmed.lastIndexOf('\\'));
        return slash >= 0 ? trimmed.substring(slash + 1) : trimmed;
    }

    private static String getExtension(String submittedFileName) {
        if (submittedFileName == null) {
            return null;
        }
        String name = submittedFileName.trim();
        if (name.isEmpty()) {
            return null;
        }
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length() - 1) {
            return null;
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String safeToken(String value) {
        if (value == null) {
            return "unknown";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "unknown";
        }
        return trimmed.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}

