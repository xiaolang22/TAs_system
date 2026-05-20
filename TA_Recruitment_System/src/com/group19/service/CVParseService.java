package com.group19.service;

import com.group19.dao.TADao;
import com.group19.dto.CVExtractedInfo;
import com.group19.dto.ParsedCVData;
import com.group19.dto.ServiceResult;
import com.group19.model.TA;
import com.group19.util.CVParserUtil;
import com.group19.util.FileUploadUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CVParseService {
    private final TADao taDao;

    public CVParseService(TADao taDao) {
        this.taDao = taDao;
    }

    public ServiceResult<ParsedCVData> parseSavedCv(String studentId, Path uploadDir) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("Student ID is required to auto-fill from resume.");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("Upload directory is not configured.");
        }

        TA existing;
        try {
            existing = taDao.findByStudentId(studentId.trim());
        } catch (IOException e) {
            return ServiceResult.failure("Failed to load profile data.");
        }

        if (existing == null) {
            return ServiceResult.failure("Please save your profile before using auto-fill.");
        }

        String fileName = FileUploadUtil.extractFileNameFromPath(existing.getCvFilePath());
        if (fileName == null || fileName.isBlank()) {
            return ServiceResult.failure("Please save a resume before using auto-fill.");
        }

        Path cvPath = uploadDir.resolve(fileName);
        if (!Files.exists(cvPath)) {
            return ServiceResult.failure("Saved resume file was not found. Please upload it again.");
        }

        ParsedCVData parsedData = parseFile(cvPath);
        return ServiceResult.success(parsedData, "CV parsed successfully.");
    }

    private ParsedCVData parseFile(Path cvPath) {
        ParsedCVData data = new ParsedCVData();
        CVExtractedInfo extractedInfo = CVParserUtil.extract(cvPath);
        data.setProgramme(extractedInfo.getEducation());
        data.setSkills(extractedInfo.getSkills());
        data.setExperience(extractedInfo.getExperience());
        return data;
    }
}
