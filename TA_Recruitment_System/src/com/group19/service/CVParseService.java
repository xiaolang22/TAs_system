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
            return ServiceResult.failure("当前账号缺少学号，暂时无法自动填充。");
        }
        if (uploadDir == null) {
            return ServiceResult.failure("简历目录不可用。");
        }

        TA existing;
        try {
            existing = taDao.findByStudentId(studentId.trim());
        } catch (IOException e) {
            return ServiceResult.failure("读取个人档案失败。");
        }

        if (existing == null) {
            return ServiceResult.failure("请先保存个人档案，再使用自动填充。");
        }

        String fileName = FileUploadUtil.extractFileNameFromPath(existing.getCvFilePath());
        if (fileName == null || fileName.isBlank()) {
            return ServiceResult.failure("请先上传简历，再使用自动填充。");
        }

        Path cvPath = uploadDir.resolve(fileName);
        if (!Files.exists(cvPath)) {
            return ServiceResult.failure("未找到已保存的简历文件，请重新上传。");
        }

        ParsedCVData parsedData = parseFile(cvPath);
        return ServiceResult.success(parsedData, "简历解析成功。");
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
