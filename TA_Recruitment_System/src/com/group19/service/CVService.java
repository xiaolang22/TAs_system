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
            return ServiceResult.failure("当前账号缺少学号，暂时无法上传简历。");
        }
        if (cvPart == null || cvPart.getSize() <= 0) {
            return ServiceResult.failure("请选择要上传的简历文件。");
        }
        String submittedName = cvPart.getSubmittedFileName();
        if (!FileUploadUtil.isAllowedCvFile(submittedName)) {
            return ServiceResult.failure("简历仅支持 PDF、DOC 或 DOCX 格式。");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("简历上传目录不可用。");
        }

        String normalizedId = studentId.trim();
        TA existing;
        try {
            existing = taDao.findByStudentId(normalizedId);
        } catch (IOException e) {
            return ServiceResult.failure("读取个人档案失败。");
        }

        if (existing == null) {
            return ServiceResult.failure("请先保存个人档案，再上传简历。");
        }

        String storedFileName = FileUploadUtil.buildStoredCvFileName(normalizedId, submittedName);
        Path targetFile = uploadDir.resolve(storedFileName);

        try {
            deleteExistingCvFiles(normalizedId, uploadDir, targetFile);
            FileUploadUtil.savePartToFile(cvPart, targetFile);
        } catch (IOException e) {
            return ServiceResult.failure("保存简历文件失败。");
        }

        existing.setCvFilePath("/uploads/" + storedFileName);
        try {
            TA saved = taDao.saveOrUpdate(existing);
            CVUploadResult payload = new CVUploadResult(saved, null);
            return ServiceResult.success(payload, "简历上传成功。");
        } catch (IOException e) {
            return ServiceResult.failure("更新简历信息失败。");
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

