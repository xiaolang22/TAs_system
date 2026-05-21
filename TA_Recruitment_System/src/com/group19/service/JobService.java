package com.group19.service;

import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobService {

    private final JobDao jobDao;

    public JobService(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    public Job findById(String jobId) {
        return jobDao.findById(jobId);
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

    public List<Job> filterJobs(List<Job> jobs, String keyword, String category, String scheduleHint, String skillsHint) {
        if (jobs == null || jobs.isEmpty()) {
            return new ArrayList<>();
        }
        return jobs.stream()
                .filter(job -> matchesKeyword(job, keyword))
                .filter(job -> matchesCategory(job, category))
                .filter(job -> containsIgnoreCase(job.getSchedule(), scheduleHint))
                .filter(job -> containsIgnoreCase(job.getRequirements(), skillsHint))
                .collect(Collectors.toList());
    }

    private static boolean matchesCategory(Job job, String category) {
        if (category == null || category.isBlank()) {
            return true;
        }
        String c = job.getCategory();
        return c != null && c.trim().equalsIgnoreCase(category.trim());
    }

    private static boolean matchesKeyword(Job job, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.trim().toLowerCase(Locale.ROOT);
        return fieldContains(job.getTitle(), k)
                || fieldContains(job.getCategory(), k)
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
    private static LocalDate parseDeadlineDate(String raw) {
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
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            return ServiceResult.failure("Title is required");
        }
        if (job.getCategory() == null || job.getCategory().trim().isEmpty()) {
            return ServiceResult.failure("Category is required");
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            return ServiceResult.failure("Description is required");
        }
        if (job.getRequirements() == null || job.getRequirements().trim().isEmpty()) {
            return ServiceResult.failure("Requirements are required");
        }
        if (job.getHours() == null || job.getHours().trim().isEmpty()) {
            return ServiceResult.failure("Hours are required");
        }
        if (job.getSchedule() == null || job.getSchedule().trim().isEmpty()) {
            return ServiceResult.failure("Schedule is required");
        }
        if (job.getDeadline() == null || job.getDeadline().trim().isEmpty()) {
            return ServiceResult.failure("Deadline is required");
        }

        job.setJobId(UUID.randomUUID().toString());
        job.setStatus("OPEN");
        job.setCreatedAt(LocalDateTime.now().toString());

        boolean success = jobDao.save(job);
        if (!success) {
            return ServiceResult.failure("Failed to save job");
        }

        return ServiceResult.success(job, "Job posted successfully");
    }
}