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

/**
 * CV parse service responsible for automatically extracting information
 * from a user's uploaded curriculum vitae and using it to auto-fill the
 * TA profile form (name, email, skills, etc.).
 *
 * @author Group19
 * @since 1.0
 */
public class CVParseService {
    private final TADao taDao;

    public CVParseService(TADao taDao) {
        this.taDao = taDao;
    }

    public ServiceResult<ParsedCVData> parseSavedCv(String studentId, Path uploadDir) {
        if (studentId == null || studentId.isBlank()) {
            return ServiceResult.failure("The current account is missing a student ID, so auto-fill is unavailable.");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("The resume directory is unavailable.");
        }

        TA existing;
        try {
            existing = taDao.findByStudentId(studentId.trim());
        } catch (IOException e) {
            return ServiceResult.failure("Failed to read the current profile.");
        }

        if (existing == null) {
            return ServiceResult.failure("Please upload and save a resume before using auto-fill.");
        }

        String fileName = FileUploadUtil.extractFileNameFromPath(existing.getCvFilePath());
        if (fileName == null || fileName.isBlank()) {
            return ServiceResult.failure("Please upload and save a resume before using auto-fill.");
        }

        Path cvPath = uploadDir.resolve(fileName);
        if (!Files.exists(cvPath)) {
            return ServiceResult.failure("The saved resume file could not be found. Please upload it again.");
        }

        ParsedCVData parsedData = parseFile(cvPath);
        return ServiceResult.success(parsedData, "Resume parsed successfully.");
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
