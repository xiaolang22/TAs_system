package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;
import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationService {
    private final ApplicationDao applicationDao;

    public ApplicationService(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public ServiceResult<Application> applyForJob(String jobId, String taStudentId, String taName, String cvFilePath) {
        if (jobId == null || jobId.isBlank()) {
            return ServiceResult.failure("Job ID is required");
        }
        if (taStudentId == null || taStudentId.isBlank()) {
            return ServiceResult.failure("TA student ID is required");
        }
        if (taName == null || taName.isBlank()) {
            return ServiceResult.failure("TA name is required");
        }

        if (applicationDao.hasApplied(jobId, taStudentId)) {
            return ServiceResult.failure("You have already applied for this job");
        }

        if (cvFilePath == null || cvFilePath.isBlank()) {
            return ServiceResult.failure("Please upload your CV before applying");
        }

        Application application = new Application();
        application.setApplicationId(UUID.randomUUID().toString());
        application.setJobId(jobId);
        application.setTaStudentId(taStudentId);
        application.setTaName(taName);
        application.setCvFilePath(cvFilePath);
        application.setStatus("SUBMITTED");
        application.setSubmittedAt(LocalDateTime.now().toString());
        application.setUpdatedAt(LocalDateTime.now().toString());

        boolean success = applicationDao.save(application);
        if (!success) {
            return ServiceResult.failure("Failed to submit application");
        }

        return ServiceResult.success(application, "Application submitted successfully");
    }
}
