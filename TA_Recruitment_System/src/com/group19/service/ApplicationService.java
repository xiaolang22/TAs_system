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

/**
 * Application service handling core business logic for TA position applications,
 * including submission, queries, status updates with state-transition validation,
 * timeline recording, and notification triggers.
 *
 * <p>Application status transition rules: SUBMITTED &rarr; IN_REVIEW &rarr; SHORTLISTED &rarr; ACCEPTED / REJECTED.</p>
 *
 * @author Group19
 * @since 1.0
 */
public class ApplicationService {

    /** Valid application status list (ordered by workflow progression). */
    private static final List<String> VALID_STATUSES = Arrays.asList(
            "SUBMITTED",
            "IN_REVIEW",
            "SHORTLISTED",
            "ACCEPTED",
            "REJECTED");

    /** Application data access object. */
    private final ApplicationDao applicationDao;

    /** Timeline recorder. */
    private final ApplicationTimelineRecorder timelineRecorder;

    /** TA status change notification service (nullable). */
    private final TaStatusNotificationService taStatusNotificationService;

    /** MO new application notification service (nullable). */
    private final MoNewApplicationNotificationService moNewApplicationNotificationService;

    /**
     * Basic constructor without notification services.
     */
    public ApplicationService(ApplicationDao applicationDao, ApplicationTimelineRecorder timelineRecorder) {
        this(applicationDao, timelineRecorder, null, null);
    }

    /**
     * Constructor with TA notification service.
     */
    public ApplicationService(
            ApplicationDao applicationDao,
            ApplicationTimelineRecorder timelineRecorder,
            TaStatusNotificationService taStatusNotificationService) {
        this(applicationDao, timelineRecorder, taStatusNotificationService, null);
    }

    /**
     * Full constructor.
     *
     * @param applicationDao                     application data access object
     * @param timelineRecorder                   timeline recorder
     * @param taStatusNotificationService        TA status notification service
     * @param moNewApplicationNotificationService MO new application notification service
     */
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

    /**
     * Submit an application for a job position.
     * Validates parameters, creates the application record, records timeline, and sends notifications.
     *
     * @param jobId       job position ID
     * @param taStudentId TA student ID
     * @param taName      TA name
     * @param cvFilePath  CV file path
     * @return operation result containing the newly created application
     */
    public ServiceResult<Application> applyForJob(String jobId, String taStudentId, String taName, String cvFilePath) {
        if (jobId == null || jobId.isBlank()) {
            return ServiceResult.failure("Job ID is required.");
        }
        if (taStudentId == null || taStudentId.isBlank()) {
            return ServiceResult.failure("TA student ID is required.");
        }
        if (taName == null || taName.isBlank()) {
            return ServiceResult.failure("TA name is required.");
        }

        if (applicationDao.hasApplied(jobId, taStudentId)) {
            return ServiceResult.failure("You have already applied for this job.");
        }

        if (cvFilePath == null || cvFilePath.isBlank()) {
            return ServiceResult.failure("Please upload your resume before applying.");
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
            return ServiceResult.failure("Failed to submit the application.");
        }

        timelineRecorder.recordSubmitted(application.getApplicationId(), application.getSubmittedAt());

        if (moNewApplicationNotificationService != null) {
            moNewApplicationNotificationService.notifyNewApplication(application);
        }

        return ServiceResult.success(application, "Application submitted successfully.");
    }

    /**
     * Look up all applications by job position ID.
     *
     * @param jobId job position ID
     * @return list of applications
     */
    public List<Application> getApplicationsByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return new ArrayList<>();
        }
        return applicationDao.findByJobId(jobId);
    }

    /**
     * Update application status. Includes state-transition validation,
     * timeline recording, and notification dispatch.
     *
     * @param applicationId application ID
     * @param newStatus     new status
     * @param decisionNote  review note
     * @return operation result
     */
    public ServiceResult<Application> updateApplicationStatus(String applicationId, String newStatus, String decisionNote) {
        if (applicationId == null || applicationId.isBlank()) {
            return ServiceResult.failure("Application ID is required.");
        }

        if (newStatus == null || newStatus.isBlank()) {
            return ServiceResult.failure("Please choose an application status.");
        }

        String normalizedStatus = normalizeStatus(newStatus);
        if (!isValidStatus(normalizedStatus)) {
            return ServiceResult.failure("The application status is invalid.");
        }

        Application application = applicationDao.findByApplicationId(applicationId);
        if (application == null) {
            return ServiceResult.failure("Application not found.");
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
            return ServiceResult.failure("Failed to update the application status.");
        }

        if (!normalizedStatus.equals(previousStatus)) {
            timelineRecorder.recordStatusChange(application.getApplicationId(), normalizedStatus, updateTime, trimmedNote);
            if (taStatusNotificationService != null) {
                taStatusNotificationService.notifyStatusChanged(application, previousStatus, normalizedStatus);
            }
        }

        return ServiceResult.success(application, "Application status updated successfully.");
    }

    /**
     * Determine whether the given status string is a valid status.
     */
    public static boolean isValidStatus(String status) {
        return VALID_STATUSES.contains(normalizeStatus(status));
    }

    /**
     * Determine whether the status can transition from currentStatus to nextStatus.
     * Status may only move forward, never backward.
     */
    public static boolean canTransition(String currentStatus, String nextStatus) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        String normalizedNext = normalizeStatus(nextStatus);
        if (!isValidStatus(normalizedCurrent) || !isValidStatus(normalizedNext)) {
            return false;
        }
        return statusRank(normalizedNext) >= statusRank(normalizedCurrent);
    }

    /**
     * Get the list of target statuses that are allowed from the current status.
     */
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
        String normalized = normalizeStatus(status);
        if ("SUBMITTED".equals(normalized)) {
            return 0;
        }
        if ("IN_REVIEW".equals(normalized)) {
            return 1;
        }
        if ("SHORTLISTED".equals(normalized)) {
            return 2;
        }
        if ("ACCEPTED".equals(normalized) || "REJECTED".equals(normalized)) {
            return 3;
        }
        return -1;
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "SUBMITTED";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private static String buildInvalidTransitionMessage(String previousStatus, String nextStatus) {
        return "The current application status cannot move from "
                + previousStatus
                + " to "
                + nextStatus
                + ". Allowed flow: SUBMITTED -> IN_REVIEW -> SHORTLISTED -> ACCEPTED/REJECTED.";
    }
}
