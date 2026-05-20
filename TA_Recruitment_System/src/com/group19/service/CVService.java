package com.group19.service;

import com.group19.dao.TADao;
import com.group19.dto.CVUploadResult;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import com.group19.util.FileUploadUtil;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CVService {
    private final TADao taDao;

    public CVService(TADao taDao) {
        this.taDao = taDao;
    }

    public ServiceResult<CVUploadResult> uploadCv(String studentId, Part cvPart, Path uploadDir) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("Student ID is required to upload CV.");
        }
        if (cvPart == null || cvPart.getSize() <= 0) {
            return ServiceResult.failure("Please choose a CV file to upload.");
        }
        String submittedName = cvPart.getSubmittedFileName();
        if (!FileUploadUtil.isAllowedCvFile(submittedName)) {
            return ServiceResult.failure("Invalid file type. Please upload a PDF or DOC/DOCX file.");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("Upload directory is not configured.");
        }

        String normalizedId = studentId.trim();
        TA existing;
        try {
            existing = taDao.findByStudentId(normalizedId);
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load profile data.");
        }

        if (existing == null) {
            return ServiceResult.failure("Please save your profile before uploading a CV.");
        }

        String storedFileName = FileUploadUtil.buildStoredCvFileName(normalizedId, submittedName);
        Path targetFile = uploadDir.resolve(storedFileName);

        try {
            deleteExistingCvFiles(normalizedId, uploadDir, targetFile);
            FileUploadUtil.savePartToFile(cvPart, targetFile);
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save uploaded CV file.");
        }

        existing.setCvFilePath("/uploads/" + storedFileName);
        try {
            TA saved = taDao.saveOrUpdate(existing);
            CVUploadResult payload = new CVUploadResult(saved, null);
            return ServiceResult.success(payload, "CV uploaded successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to update profile with CV information.");
        }
    }

    private void deleteExistingCvFiles(String studentId, Path uploadDir, Path targetFile) throws IOException {
        if (studentId == null || studentId.isBlank() || uploadDir == null) {
            return;
        }

        Files.createDirectories(uploadDir);
        String safeStudentId = studentId.trim().replaceAll("[^A-Za-z0-9_-]", "_");
        String pattern = "cv_" + safeStudentId + ".*";

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, pattern)) {
            for (Path existingFile : stream) {
                if (targetFile != null && existingFile.equals(targetFile)) {
                    continue;
                }
                Files.deleteIfExists(existingFile);
            }
        }
    }
}

