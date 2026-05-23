package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ApplicationService {
    private static final List<String> VALID_STATUSES = Arrays.asList(
            "SUBMITTED",
            "IN_REVIEW",
            "SHORTLISTED",
            "ACCEPTED",
            "REJECTED");

    private final ApplicationDao applicationDao;
    private final ApplicationTimelineRecorder timelineRecorder;
    private final TaStatusNotificationService taStatusNotificationService;
    private final MoNewApplicationNotificationService moNewApplicationNotificationService;

    public ApplicationService(ApplicationDao applicationDao, ApplicationTimelineRecorder timelineRecorder) {
        this(applicationDao, timelineRecorder, null, null);
    }

    public ApplicationService(
            ApplicationDao applicationDao,
            ApplicationTimelineRecorder timelineRecorder,
            TaStatusNotificationService taStatusNotificationService) {
        this(applicationDao, timelineRecorder, taStatusNotificationService, null);
    }

    public ApplicationService(
            ApplicationDao applicationDao,
            ApplicationTimelineRecorder timelineRecorder,
            TaStatusNotificationService taStatusNotificationService,
            MoNewApplicationNotificationService moNewApplicationNotificationService) {
        this.applicationDao = applicationDao;
        this.timelineRecorder = timelineRecorder;
        this.taStatusNotificationService = taStatusNotificationService;
        this.moNewApplicationNotificationService = moNewApplicationNotificationService;
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

        if (moNewApplicationNotificationService != null) {
            moNewApplicationNotificationService.notifyNewApplication(application);
        }

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
            return ServiceResult.failure("缺少申请编号。");
        }

        if (newStatus == null || newStatus.isBlank()) {
            return ServiceResult.failure("请选择申请状态。");
        }

        String normalizedStatus = normalizeStatus(newStatus);
        if (!isValidStatus(normalizedStatus)) {
            return ServiceResult.failure("申请状态无效。");
        }

        Application application = applicationDao.findByApplicationId(applicationId);
        if (application == null) {
            return ServiceResult.failure("未找到对应申请。");
        }

        String previousStatus = normalizeStatus(application.getStatus());
        if (!canTransition(previousStatus, normalizedStatus)) {
            return ServiceResult.failure(buildInvalidTransitionMessage(previousStatus, normalizedStatus));
        }

        String updateTime = LocalDateTime.now().toString();
        String trimmedNote = decisionNote == null ? "" : decisionNote.trim();

        application.setStatus(normalizedStatus);
        application.setDecisionNote(trimmedNote);
        application.setUpdatedAt(updateTime);

        boolean success = applicationDao.update(application);
        if (!success) {
            return ServiceResult.failure("更新申请状态失败。");
        }

        if (!normalizedStatus.equals(previousStatus)) {
            timelineRecorder.recordStatusChange(application.getApplicationId(), normalizedStatus, updateTime, trimmedNote);
            if (taStatusNotificationService != null) {
                taStatusNotificationService.notifyStatusChanged(application, previousStatus, normalizedStatus);
            }
        }

        return ServiceResult.success(application, "申请状态更新成功。");
    }

    public static boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(normalizeStatus(status));
    }

    public static boolean canTransition(String currentStatus, String nextStatus) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        String normalizedNext = normalizeStatus(nextStatus);
        if (!isValidStatus(normalizedCurrent) || !isValidStatus(normalizedNext)) {
            return false;
        }
        return statusRank(normalizedNext) >= statusRank(normalizedCurrent);
    }

    public static List<String> getAllowedStatuses(String currentStatus) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        List<String> allowed = new ArrayList<>();
        for (String status : VALID_STATUSES) {
            if (canTransition(normalizedCurrent, status)) {
                allowed.add(status);
            }
        }
        return allowed;
    }

    private static int statusRank(String status) {
        return switch (normalizeStatus(status)) {
            case "SUBMITTED" -> 0;
            case "IN_REVIEW" -> 1;
            case "SHORTLISTED" -> 2;
            case "ACCEPTED", "REJECTED" -> 3;
            default -> -1;
        };
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "SUBMITTED";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private static String buildInvalidTransitionMessage(String previousStatus, String nextStatus) {
        return "当前申请状态不允许从 "
                + previousStatus
                + " 变更为 "
                + nextStatus
                + "。允许的流程为：SUBMITTED -> IN_REVIEW -> SHORTLISTED -> ACCEPTED/REJECTED。";
    }
}
