package com.group19.service;

import com.group19.dao.ApplicationDao;
import com.group19.dao.JobDao;
import com.group19.dto.ServiceResult;
import com.group19.model.Job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JobService {

    private final JobDao jobDao;
    private final ApplicationDao applicationDao;

    public JobService() {
        this(new JobDao(), null);
    }

    public JobService(JobDao jobDao) {
        this(jobDao, null);
    }

    public JobService(JobDao jobDao, ApplicationDao applicationDao) {
        this.jobDao = jobDao == null ? new JobDao() : jobDao;
        this.applicationDao = applicationDao;
    }

    public List<Job> searchJobs(String keyword, String category, String status) {
        return searchJobs(keyword, category, status, null, null, null);
    }

    public List<Job> searchJobs(
            String keyword,
            String category,
            String status,
            String deadlineFrom,
            String deadlineTo,
            String excludeAppliedForTaStudentId
    ) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        String cat = category == null ? "" : category.trim().toLowerCase();
        String st = status == null ? "OPEN" : status.trim().toUpperCase();

        LocalDate from = parseDate(deadlineFrom);
        LocalDate to = parseDate(deadlineTo);

        Set<String> excludeJobIds = new HashSet<>();
        if (applicationDao != null &&
                excludeAppliedForTaStudentId != null &&
                !excludeAppliedForTaStudentId.trim().isEmpty()
        ) {
            applicationDao.findByTaStudentId(excludeAppliedForTaStudentId).forEach(a -> {
                if (a != null && a.getJobId() != null && !a.getJobId().trim().isEmpty()) {
                    excludeJobIds.add(a.getJobId().trim());
                }
            });
        }

        List<Job> all = jobDao.findAll();
        List<Job> result = new ArrayList<>();
        for (Job job : all) {
            if (job == null) {
                continue;
            }

            if (job.getJobId() != null && excludeJobIds.contains(job.getJobId().trim())) {
                continue;
            }

            if (!cat.isEmpty()) {
                String jobCat = job.getCategory() == null ? "" : job.getCategory().trim().toLowerCase();
                if (!jobCat.equals(cat)) {
                    continue;
                }
            }

            if (!"ALL".equals(st)) {
                String jobStatus = job.getStatus() == null ? "" : job.getStatus().trim().toUpperCase();
                if (!st.equals(jobStatus)) {
                    continue;
                }
            }

            if (from != null || to != null) {
                LocalDate jobDeadline = parseDate(job.getDeadline());
                if (jobDeadline == null) {
                    continue;
                }
                if (from != null && jobDeadline.isBefore(from)) {
                    continue;
                }
                if (to != null && jobDeadline.isAfter(to)) {
                    continue;
                }
            }

            if (!kw.isEmpty()) {
                String haystack = (safe(job.getTitle()) + " " +
                        safe(job.getDescription()) + " " +
                        safe(job.getRequirements()) + " " +
                        safe(job.getCategory()) + " " +
                        safe(job.getCourseCode()) + " " +
                        safe(job.getSalary()) + " " +
                        safe(job.getHours()) + " " +
                        safe(job.getSchedule()) + " " +
                        safe(job.getDeadline())).toLowerCase();
                if (!haystack.contains(kw)) {
                    continue;
                }
            }

            result.add(job);
        }

        result.sort(Comparator.comparing(Job::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public Job getJobById(String jobId) {
        return jobDao.findById(jobId);
    }

    private static String safe(String v) {
        return v == null ? "" : v;
    }

    private static LocalDate parseDate(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDateTime.parse(s).toLocalDate();
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        } catch (DateTimeParseException ignored) {
        }
        return null;
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

        if (job.getTotalSlots() != null && job.getRemainingSlots() == null) {
            job.setRemainingSlots(job.getTotalSlots());
        }
        if (job.getTotalSlots() == null && job.getRemainingSlots() != null) {
            job.setTotalSlots(job.getRemainingSlots());
        }

        boolean success = jobDao.save(job);
        if (!success) {
            return ServiceResult.failure("Failed to save job");
        }

        return ServiceResult.success(job, "Job posted successfully");
    }
}