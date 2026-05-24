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
import java.time.LocalDateTime;

/**
 * CV file service handling curriculum vitae uploads and cleanup of previous files.
 * Supports PDF, DOC, and DOCX formats, and automatically removes old CV files
 * belonging to the same user.
 *
 * @author Group19
 * @since 1.0
 */
public class CVService {

    /** TA data access object. */
    private final TADao taDao;

    /**
     * Construct the service instance.
     *
     * @param taDao TA data access object
     */
    public CVService(TADao taDao) {
        this.taDao = taDao;
    }

    /**
     * Upload a curriculum vitae file. Automatically creates/updates the TA profile,
     * removes old CV files, and saves the new one.
     *
     * @param studentId   student ID
     * @param displayName display name (used when creating a new profile)
     * @param cvPart      uploaded file Part
     * @param uploadDir   upload target directory
     * @return operation result containing the updated profile and optional extracted data
     */
    public ServiceResult<CVUploadResult> uploadCv(String studentId, String displayName, Part cvPart, Path uploadDir) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("The current account is missing a student ID, so the resume cannot be uploaded.");
        }
        if (cvPart == null || cvPart.getSize() <= 0) {
            return ServiceResult.failure("Please choose a resume file to upload.");
        }
        String submittedName = cvPart.getSubmittedFileName();
        if (!FileUploadUtil.isAllowedCvFile(submittedName)) {
            return ServiceResult.failure("Only PDF, DOC, and DOCX files are supported.");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("The resume upload directory is unavailable.");
        }

        String normalizedId = studentId.trim();
        TA existing;
        try {
            existing = taDao.findByStudentId(normalizedId);
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read the current profile.");
        }

        if (existing == null) {
            existing = new TA();
            existing.setStudentId(normalizedId);
            existing.setName(displayName == null || displayName.isBlank() ? normalizedId : displayName.trim());
            existing.setUpdatedAt(LocalDateTime.now().toString());
        }

        String storedFileName = FileUploadUtil.buildStoredCvFileName(normalizedId, submittedName);
        Path targetFile = uploadDir.resolve(storedFileName);

        try {
            deleteExistingCvFiles(normalizedId, uploadDir, targetFile);
            FileUploadUtil.savePartToFile(cvPart, targetFile);
        } catch (IOException e) {
            return ServiceResult.failure("Failed to save the resume file.");
        }

        existing.setCvFilePath("/uploads/" + storedFileName);
        existing.setUpdatedAt(LocalDateTime.now().toString());
        try {
            TA saved = taDao.saveOrUpdate(existing);
            CVUploadResult payload = new CVUploadResult(saved, null);
            return ServiceResult.success(payload, "Resume uploaded successfully.");
        } catch (IOException e) {
            return ServiceResult.failure("Failed to update the resume information.");
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
