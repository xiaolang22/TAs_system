package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplicationService {
    private final ApplicationDao applicationDao;
    private final ApplicationTimelineRecorder timelineRecorder;

    public ApplicationService(ApplicationDao applicationDao, ApplicationTimelineRecorder timelineRecorder) {
        this.applicationDao = applicationDao;
        this.timelineRecorder = timelineRecorder;
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
        application.setDecisionNote("");

        boolean success = applicationDao.save(application);
        if (!success) {
            return ServiceResult.failure("Failed to submit application");
        }

        timelineRecorder.recordSubmitted(application.getApplicationId(), application.getSubmittedAt());

        return ServiceResult.success(application, "Application submitted successfully");
    }

    public List<Application> getApplicationsByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return new ArrayList<>();
        }
        return applicationDao.findByJobId(jobId);
    }

    public ServiceResult<Application> updateApplicationStatus(String applicationId, String newStatus, String decisionNote) {
        if (applicationId == null || applicationId.isBlank()) {
            return ServiceResult.failure("Application ID is required");
        }

        if (newStatus == null || newStatus.isBlank()) {
            return ServiceResult.failure("Status is required");
        }

        String normalizedStatus = newStatus.trim().toUpperCase();
        if (!"SUBMITTED".equals(normalizedStatus)
                && !"IN_REVIEW".equals(normalizedStatus)
                && !"SHORTLISTED".equals(normalizedStatus)
                && !"ACCEPTED".equals(normalizedStatus)
                && !"REJECTED".equals(normalizedStatus)) {
            return ServiceResult.failure("Invalid status");
        }

        Application application = applicationDao.findByApplicationId(applicationId);
        if (application == null) {
            return ServiceResult.failure("Application not found");
        }

        String previousStatus = application.getStatus() == null ? "" : application.getStatus().trim().toUpperCase();
        String updateTime = LocalDateTime.now().toString();
        String trimmedNote = decisionNote == null ? "" : decisionNote.trim();

        application.setStatus(normalizedStatus);
        application.setDecisionNote(trimmedNote);
        application.setUpdatedAt(updateTime);

        boolean success = applicationDao.update(application);
        if (!success) {
            return ServiceResult.failure("Failed to update application");
        }

        if (!normalizedStatus.equals(previousStatus)) {
            timelineRecorder.recordStatusChange(application.getApplicationId(), normalizedStatus, updateTime, trimmedNote);
        }

        return ServiceResult.success(application, "Application status updated successfully");
    }
}