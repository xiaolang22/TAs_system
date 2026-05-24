package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Job position service handling core job posting business logic.
 * Includes CRUD operations, open/closed status determination, keyword filtering,
 * deadline parsing, and expiry detection.
 *
 * @author Group19
 * @since 1.0
 */
public class JobService {

    /** Job position data access object. */
    private final JobDao jobDao;

    /**
     * Construct the service instance.
     *
     * @param jobDao job position data access object
     */
    public JobService(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    /**
     * Find a job position by exact job ID.
     *
     * @param jobId job position ID
     * @return the matching job, or {@code null} if not found
     */
    public Job findById(String jobId) {
        return jobDao.findById(jobId);
    }

    /**
     * Get the full list of job positions.
     *
     * @return a copy of the job list
     */
    public List<Job> findAllJobs() {
        return new ArrayList<>(jobDao.findAll());
    }

    /**
     * Jobs that are open for listing: status OPEN (or missing) and deadline not passed.
     */
    public List<Job> findOpenActiveJobs(LocalDate today) {
        return jobDao.findAll().stream()
                .filter(job -> isOpenForListing(job, today))
                .collect(Collectors.toList());
    }

    /**
     * Jobs excluded from the open list: non-OPEN status or application deadline passed.
     */
    public List<Job> findHiddenFromOpenJobs(LocalDate today) {
        return jobDao.findAll().stream()
                .filter(job -> !isOpenForListing(job, today))
                .collect(Collectors.toList());
    }

    public boolean isOpenForListing(Job job, LocalDate today) {
        if (job == null) {
            return false;
        }
        String status = job.getStatus();
        if (status != null && !status.isBlank() && !"OPEN".equalsIgnoreCase(status.trim())) {
            return false;
        }
        LocalDate deadlineDate = parseDeadlineDate(job.getDeadline());
        if (deadlineDate == null) {
            return true;
        }
        return !today.isAfter(deadlineDate);
    }

    public boolean isOpenForApplication(Job job, LocalDate today) {
        return isOpenForListing(job, today);
    }

    /**
     * Open jobs whose application deadline is between today (inclusive) and today + withinDays (inclusive),
     * sorted by deadline ascending.
     */
    public List<Job> findUpcomingDeadlineJobs(LocalDate today, int withinDays) {
        if (today == null || withinDays < 0) {
            return new ArrayList<>();
        }
        LocalDate maxDate = today.plusDays(withinDays);
        List<Job> upcoming = new ArrayList<>();
        for (Job job : jobDao.findAll()) {
            if (!isOpenStatus(job)) {
                continue;
            }
            LocalDate deadlineDate = parseDeadlineDate(job.getDeadline());
            if (deadlineDate == null) {
                continue;
            }
            if (deadlineDate.isBefore(today) || deadlineDate.isAfter(maxDate)) {
                continue;
            }
            upcoming.add(job);
        }
        upcoming.sort(Comparator.comparing(job -> parseDeadlineDate(job.getDeadline()),
                Comparator.nullsLast(Comparator.naturalOrder())));
        return upcoming;
    }

    private static boolean isOpenStatus(Job job) {
        if (job == null) {
            return false;
        }
        String status = job.getStatus();
        return status == null || status.isBlank() || "OPEN".equalsIgnoreCase(status.trim());
    }

    public List<Job> filterJobs(List<Job> jobs, String keyword, String scheduleHint, String skillsHint) {
        if (jobs == null || jobs.isEmpty()) {
            return new ArrayList<>();
        }
        return jobs.stream()
                .filter(job -> matchesKeyword(job, keyword))
                .filter(job -> containsIgnoreCase(job.getSchedule(), scheduleHint))
                .filter(job -> containsIgnoreCase(job.getRequirements(), skillsHint))
                .collect(Collectors.toList());
    }

    public List<Job> filterJobsByOwner(List<Job> jobs, String ownerMoUserId) {
        if (jobs == null || jobs.isEmpty()) {
            return new ArrayList<>();
        }
        if (ownerMoUserId == null || ownerMoUserId.isBlank()) {
            return new ArrayList<>();
        }
        String normalizedOwner = ownerMoUserId.trim();
        return jobs.stream()
                .filter(job -> isOwnedBy(job, normalizedOwner))
                .collect(Collectors.toList());
    }

    public List<Job> findJobsOwnedBy(String ownerMoUserId) {
        return filterJobsByOwner(jobDao.findAll(), ownerMoUserId);
    }

    public boolean isOwnedBy(Job job, String ownerMoUserId) {
        if (job == null || ownerMoUserId == null || ownerMoUserId.isBlank()) {
            return false;
        }
        String jobOwner = job.getOwnerMoUserId();
        return jobOwner != null && ownerMoUserId.trim().equalsIgnoreCase(jobOwner.trim());
    }

    private static boolean matchesKeyword(Job job, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.trim().toLowerCase(Locale.ROOT);
        return fieldContains(job.getTitle(), k)
                || fieldContains(job.getDescription(), k)
                || fieldContains(job.getRequirements(), k)
                || fieldContains(job.getSchedule(), k)
                || fieldContains(job.getHours(), k);
    }

    private static boolean fieldContains(String field, String keywordLower) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(keywordLower);
    }

    private static boolean containsIgnoreCase(String field, String fragment) {
        if (fragment == null || fragment.isBlank()) {
            return true;
        }
        return field != null && field.toLowerCase(Locale.ROOT).contains(fragment.trim().toLowerCase(Locale.ROOT));
    }

    /**
     * Parses application deadline; supports ISO date, ISO date-time, and leading yyyy-MM-dd in longer strings.
     */
    public static LocalDate parseDeadlineDate(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (s.length() >= 10) {
            String head = s.substring(0, 10);
            try {
                return LocalDate.parse(head, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            // fall through
        }
        try {
            return LocalDateTime.parse(s).toLocalDate();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    public ServiceResult<Job> createJob(Job job) {
        ServiceResult<Job> validation = validateDraft(job);
        if (!validation.isSuccess()) {
            return validation;
        }

        job.setJobId(UUID.randomUUID().toString());
        job.setStatus("OPEN");
        job.setCreatedAt(LocalDateTime.now().toString());

        boolean success = jobDao.save(job);
        if (!success) {
            return ServiceResult.failure("Failed to save the job.");
        }

        return ServiceResult.success(job, "Job posted successfully.");
    }

    public ServiceResult<Job> updateJob(Job job, String moUserId) {
        if (job == null || job.getJobId() == null || job.getJobId().trim().isEmpty()) {
            return ServiceResult.failure("Job ID is required.");
        }
        Job existing = jobDao.findById(job.getJobId().trim());
        if (existing == null) {
            return ServiceResult.failure("Job not found.");
        }
        if (!isOwnedBy(existing, moUserId)) {
            return ServiceResult.failure("You can only edit jobs that you posted.");
        }

        ServiceResult<Job> validation = validateDraft(job);
        if (!validation.isSuccess()) {
            return validation;
        }

        job.setOwnerMoUserId(existing.getOwnerMoUserId());
        job.setCreatedAt(existing.getCreatedAt());
        job.setStatus(normalizeStatus(existing.getStatus()));

        boolean updated = jobDao.update(job);
        if (!updated) {
            return ServiceResult.failure("Failed to update the job.");
        }
        return ServiceResult.success(job, "Job information updated successfully.");
    }

    public ServiceResult<Job> updateJobAsAdmin(Job job) {
        if (job == null || job.getJobId() == null || job.getJobId().trim().isEmpty()) {
            return ServiceResult.failure("Job ID is required.");
        }
        Job existing = jobDao.findById(job.getJobId().trim());
        if (existing == null) {
            return ServiceResult.failure("Job not found.");
        }

        ServiceResult<Job> validation = validateDraft(job);
        if (!validation.isSuccess()) {
            return validation;
        }

        job.setOwnerMoUserId(existing.getOwnerMoUserId());
        job.setCreatedAt(existing.getCreatedAt());
        job.setStatus(normalizeStatus(job.getStatus()));

        boolean updated = jobDao.update(job);
        if (!updated) {
            return ServiceResult.failure("Failed to update the job.");
        }
        return ServiceResult.success(job, "Job information updated successfully.");
    }

    public ServiceResult<Void> deleteJob(String jobId, String moUserId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return ServiceResult.failure("Job ID is required.");
        }
        Job existing = jobDao.findById(jobId.trim());
        if (existing == null) {
            return ServiceResult.failure("Job not found.");
        }
        if (!isOwnedBy(existing, moUserId)) {
            return ServiceResult.failure("You can only delete jobs that you posted.");
        }
        boolean deleted = jobDao.delete(jobId.trim());
        if (!deleted) {
            return ServiceResult.failure("Failed to delete the job.");
        }
        return ServiceResult.success(null, "Job deleted.");
    }

    public ServiceResult<Void> deleteJobAsAdmin(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return ServiceResult.failure("Job ID is required.");
        }
        Job existing = jobDao.findById(jobId.trim());
        if (existing == null) {
            return ServiceResult.failure("Job not found.");
        }
        boolean deleted = jobDao.delete(jobId.trim());
        if (!deleted) {
            return ServiceResult.failure("Failed to delete the job.");
        }
        return ServiceResult.success(null, "Job deleted.");
    }

    private ServiceResult<Job> validateDraft(Job job) {
        if (job == null) {
            return ServiceResult.failure("Job information cannot be empty.");
        }
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            return ServiceResult.failure("Job title cannot be empty.");
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            return ServiceResult.failure("Job description cannot be empty.");
        }
        if (job.getRequirements() == null || job.getRequirements().trim().isEmpty()) {
            return ServiceResult.failure("Requirements cannot be empty.");
        }
        if (job.getHours() == null || job.getHours().trim().isEmpty()) {
            return ServiceResult.failure("Workload cannot be empty.");
        }
        if (job.getSchedule() == null || job.getSchedule().trim().isEmpty()) {
            return ServiceResult.failure("Schedule cannot be empty.");
        }
        if (job.getDeadline() == null || job.getDeadline().trim().isEmpty()) {
            return ServiceResult.failure("Deadline cannot be empty.");
        }
        if (job.getOwnerMoUserId() == null || job.getOwnerMoUserId().trim().isEmpty()) {
            return ServiceResult.failure("Job owner cannot be empty.");
        }
        return ServiceResult.success(job, "");
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "OPEN";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}
